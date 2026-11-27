package es.caib.ripea.service.resourcehelper.inici;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

import com.github.benmanes.caffeine.cache.Ticker;

/**
 * Caché de la pantalla d'inici: caducitat per entrada, sense caché amb caducitat zero, errors que no es guarden,
 * caducitat més curta per a un resultat concret i peticions simultànies que esperen el mateix càlcul.
 */
class CacheIniciTest {

	private static final Duration DOS_MINUTS = Duration.ofMinutes(2);

	private final AtomicLong ara = new AtomicLong();
	private final Ticker rellotge = ara::get;
	private final CacheInici cache = new CacheInici(rellotge);

	private static CacheInici.Clau clau(String usuari, String element) {
		return new CacheInici.Clau(usuari, "GOIB", "tothom", null, "ca", element);
	}

	private void avancar(Duration durada) {
		ara.addAndGet(durada.toNanos());
	}

	@Test
	void guardaElResultatFinsQueCaduca() {
		AtomicInteger calculs = new AtomicInteger();
		assertThat(cache.obtenir(clau("u1", "A"), DOS_MINUTS, calculs::incrementAndGet, null)).isEqualTo(1);
		avancar(Duration.ofSeconds(119));
		assertThat(cache.obtenir(clau("u1", "A"), DOS_MINUTS, calculs::incrementAndGet, null)).isEqualTo(1);
		avancar(Duration.ofSeconds(2));
		assertThat(cache.obtenir(clau("u1", "A"), DOS_MINUTS, calculs::incrementAndGet, null)).isEqualTo(2);
	}

	@Test
	void clausDiferentsNoComparteixenResultat() {
		AtomicInteger calculs = new AtomicInteger();
		cache.obtenir(clau("u1", "A"), DOS_MINUTS, calculs::incrementAndGet, null);
		cache.obtenir(clau("u2", "A"), DOS_MINUTS, calculs::incrementAndGet, null);
		cache.obtenir(clau("u1", "B"), DOS_MINUTS, calculs::incrementAndGet, null);
		cache.obtenir(new CacheInici.Clau("u1", "GOIB", "tothom", null, "es", "A"), DOS_MINUTS, calculs::incrementAndGet, null);
		assertThat(calculs.get()).isEqualTo(4);
	}

	@Test
	void ambCaducitatZeroNoEsGuarda() {
		AtomicInteger calculs = new AtomicInteger();
		cache.obtenir(clau("u1", "A"), Duration.ZERO, calculs::incrementAndGet, null);
		cache.obtenir(clau("u1", "A"), Duration.ZERO, calculs::incrementAndGet, null);
		assertThat(calculs.get()).isEqualTo(2);
	}

	@Test
	void unResultatAmbCaducitatZeroNoEsGuarda() {
		AtomicInteger calculs = new AtomicInteger();
		cache.obtenir(clau("u1", "A"), DOS_MINUTS, calculs::incrementAndGet, resultat -> Duration.ZERO);
		cache.obtenir(clau("u1", "A"), DOS_MINUTS, calculs::incrementAndGet, resultat -> Duration.ZERO);
		assertThat(calculs.get()).isEqualTo(2);
	}

	@Test
	void unResultatPotCaducarAbans() {
		AtomicInteger calculs = new AtomicInteger();
		Duration quinzeMinuts = Duration.ofMinutes(15);
		cache.obtenir(clau("u1", "A"), quinzeMinuts, calculs::incrementAndGet, resultat -> DOS_MINUTS);
		avancar(Duration.ofSeconds(110));
		cache.obtenir(clau("u1", "A"), quinzeMinuts, calculs::incrementAndGet, resultat -> DOS_MINUTS);
		assertThat(calculs.get()).isEqualTo(1);
		avancar(Duration.ofSeconds(20));
		cache.obtenir(clau("u1", "A"), quinzeMinuts, calculs::incrementAndGet, resultat -> DOS_MINUTS);
		assertThat(calculs.get()).isEqualTo(2);
	}

	@Test
	void unaExcepcioNoEsGuarda() {
		AtomicInteger calculs = new AtomicInteger();
		assertThatThrownBy(() -> cache.obtenir(clau("u1", "A"), DOS_MINUTS, () -> {
			calculs.incrementAndGet();
			throw new IllegalStateException("prova");
		}, null)).isInstanceOf(IllegalStateException.class);
		assertThat(cache.obtenir(clau("u1", "A"), DOS_MINUTS, calculs::incrementAndGet, null)).isEqualTo(2);
	}

	@Test
	void lesPeticionsSimultaniesEsperenElMateixCalcul() throws Exception {
		AtomicInteger calculs = new AtomicInteger();
		CountDownLatch calculant = new CountDownLatch(1);
		CountDownLatch acabar = new CountDownLatch(1);
		ExecutorService fils = Executors.newFixedThreadPool(2);
		try {
			Future<Integer> primera = fils.submit(() -> cache.obtenir(clau("u1", "A"), DOS_MINUTS, () -> {
				calculant.countDown();
				esperar(acabar);
				return calculs.incrementAndGet();
			}, null));
			assertThat(calculant.await(5, TimeUnit.SECONDS)).isTrue();
			Future<Integer> segona = fils.submit(() -> cache.obtenir(clau("u1", "A"), DOS_MINUTS, calculs::incrementAndGet, null));
			// La segona espera el càlcul de la primera: encara no ha acabat
			Thread.sleep(200);
			assertThat(segona.isDone()).isFalse();
			acabar.countDown();
			assertThat(primera.get(5, TimeUnit.SECONDS)).isEqualTo(1);
			assertThat(segona.get(5, TimeUnit.SECONDS)).isEqualTo(1);
			assertThat(calculs.get()).isEqualTo(1);
		} finally {
			fils.shutdownNow();
		}
	}

	private static void esperar(CountDownLatch latch) {
		try {
			latch.await(5, TimeUnit.SECONDS);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

}
