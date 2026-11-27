package es.caib.ripea.service.resourcehelper.inici;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.Ticker;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * Caché de la pantalla d'inici: guarda en memòria, durant un temps curt, el resultat de cada grup de targetes i de
 * cada llista per a cada usuari i context (entitat, rol, òrgan i idioma). Si l'usuari torna a la pantalla, la
 * recarrega o l'obre en una altra pestanya dins d'aquest temps, no es torna a consultar la base de dades.
 *
 * <ul>
 *   <li>El càlcul es fa al fil de la petició, que és qui té l'usuari autenticat i el context actual (entitat, rol i
 *   òrgan de ConfigHelper).</li>
 *   <li>Les peticions simultànies amb la mateixa clau esperen el mateix càlcul en lloc de repetir-lo.</li>
 *   <li>La caducitat és per entrada, es compta des que es guarda el resultat i llegir-lo no l'allarga. Cada resultat
 *   pot tenir la seva (p. ex. no guardar els errors).</li>
 *   <li>És local a cada node: cada node calcula la seva còpia.</li>
 * </ul>
 */
@Slf4j
@Component
public class CacheInici {

	/** Entrades com a màxim (cada usuari i context en fa servir una per grup de targetes i una per llista). */
	static final long MIDA_MAXIMA = 20000;
	/**
	 * Temps màxim que una petició espera el càlcul d'una altra amb la mateixa clau. Si se supera, calcula pel seu
	 * compte (sense guardar-ho) en lloc de quedar bloquejada.
	 */
	static final Duration ESPERA_MAXIMA = Duration.ofMinutes(2);

	private final Cache<Clau, Entrada> entrades;

	public CacheInici() {
		this(Ticker.systemTicker());
	}

	/** Amb un rellotge propi, per als tests de caducitat. */
	CacheInici(Ticker ticker) {
		this.entrades = Caffeine.newBuilder()
				.maximumSize(MIDA_MAXIMA)
				.ticker(ticker)
				.expireAfter(new Expiry<Clau, Entrada>() {
					@Override
					public long expireAfterCreate(Clau clau, Entrada entrada, long ara) {
						return entrada.getCaducitat().toNanos();
					}
					@Override
					public long expireAfterUpdate(Clau clau, Entrada entrada, long ara, long restant) {
						return entrada.getCaducitat().toNanos();
					}
					@Override
					public long expireAfterRead(Clau clau, Entrada entrada, long ara, long restant) {
						return restant;
					}
				})
				.build();
	}

	/**
	 * Resultat guardat per a la clau o, si no n'hi ha, el calcula, el guarda i el torna.
	 *
	 * @param clau               usuari, context i element (grup de targetes o llista).
	 * @param caducitat          temps que es guarda el resultat. Zero o negatiu: sense caché (es calcula sempre).
	 * @param calcul             càlcul del resultat; s'executa al fil que crida.
	 * @param caducitatResultat  temps que es guarda un resultat concret: null per fer servir {@code caducitat}, zero o
	 *                           negatiu per no guardar-lo (p. ex. un error). No pot ser més llarg que {@code caducitat}.
	 */
	@SuppressWarnings("unchecked")
	public <T> T obtenir(
			Clau clau,
			Duration caducitat,
			Supplier<T> calcul,
			Function<T, Duration> caducitatResultat) {
		if (caducitat == null || caducitat.isZero() || caducitat.isNegative()) {
			return calcul.get();
		}
		Entrada nova = new Entrada(new CompletableFuture<>(), caducitat);
		Entrada existent = entrades.asMap().putIfAbsent(clau, nova);
		if (existent != null) {
			return (T)esperar(existent, clau, calcul);
		}
		T resultat;
		try {
			resultat = calcul.get();
		} catch (RuntimeException | Error ex) {
			// No es guarda: la petició següent ho torna a provar. Les que esperaven reben el mateix error.
			entrades.asMap().remove(clau, nova);
			nova.getResultat().completeExceptionally(ex);
			throw ex;
		}
		nova.getResultat().complete(resultat);
		Duration durada = caducitatResultat != null ? caducitatResultat.apply(resultat) : null;
		if (durada != null && (durada.isZero() || durada.isNegative())) {
			entrades.asMap().remove(clau, nova);
		} else if (durada != null && durada.compareTo(caducitat) < 0) {
			// Mateix resultat, caducitat més curta (es compta des d'ara)
			entrades.asMap().replace(clau, nova, new Entrada(nova.getResultat(), durada));
		}
		return resultat;
	}

	/** Buida la caché (tests i manteniment). */
	public void buidar() {
		entrades.invalidateAll();
	}

	/** Espera el càlcul d'una altra petició amb la mateixa clau. */
	private Object esperar(Entrada existent, Clau clau, Supplier<?> calcul) {
		try {
			return existent.getResultat().get(ESPERA_MAXIMA.toMillis(), TimeUnit.MILLISECONDS);
		} catch (ExecutionException ex) {
			Throwable causa = ex.getCause();
			if (causa instanceof RuntimeException) {
				throw (RuntimeException)causa;
			}
			if (causa instanceof Error) {
				throw (Error)causa;
			}
			throw new IllegalStateException(causa);
		} catch (TimeoutException ex) {
			log.warn("La pantalla d'inici calcula pel seu compte " + clau + ": el càlcul d'una altra petició supera " + ESPERA_MAXIMA);
			return calcul.get();
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			return calcul.get();
		}
	}

	/** Usuari i context de la pantalla d'inici, i element (grup de targetes o llista) que es guarda. */
	@Value
	public static class Clau {
		String usuari;
		String entitat;
		String rol;
		String organ;
		/** Algunes llistes porten textos traduïts (p. ex. el nom de la integració). */
		String idioma;
		String element;
	}

	@Value
	private static class Entrada {
		CompletableFuture<Object> resultat;
		Duration caducitat;
	}

}
