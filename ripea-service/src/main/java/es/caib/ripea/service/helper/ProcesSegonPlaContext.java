package es.caib.ripea.service.helper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import es.caib.ripea.service.intf.dto.ProcesSegonPlaPasEstatEnumDto;
import es.caib.ripea.service.intf.exception.FirmaServidorException;
import es.caib.ripea.service.intf.utils.Utils;

/**
 * Punt d'entrada perquè la lògica de negoci informi del progrés del procés en segon pla que s'està executant en
 * el fil actual (veure {@link ProcesSegonPlaHelper}).
 *
 * Els mètodes són estàtics i, si el fil no executa cap procés (JSP, accions massives, tests...), no fan res: el
 * mateix codi de negoci serveix per a tots els casos sense haver de passar cap paràmetre de més.
 *
 * Exemple:
 * <pre>
 *   ProcesSegonPlaContext.pas("TANCAMENT_COMPROVAR", null, () -> comprovar(...));
 *   ProcesSegonPlaContext.pas("TANCAMENT_FIRMAR_DOCUMENT", ProcesSegonPlaContext.params("document", nom), () -> {
 *       ProcesSegonPlaContext.fase("FIRMAR_SERVIDOR", null);
 *       ...
 *   });
 * </pre>
 *
 * Els codis de pas i de fase els tradueix el front (procesSegonPla.pas.&lt;codi&gt; i procesSegonPla.fase.&lt;fase&gt;).
 */
public final class ProcesSegonPlaContext {

	private static final ThreadLocal<ProcesSegonPla> ACTUAL = new ThreadLocal<>();

	private ProcesSegonPlaContext() {
	}

	static void establir(ProcesSegonPla proces) {
		ACTUAL.set(proces);
	}

	static void netejar() {
		ACTUAL.remove();
	}

	/** Identificador del procés que s'executa en el fil actual, o null si no n'hi ha cap. */
	public static String getOperacioActual() {
		ProcesSegonPla proces = ACTUAL.get();
		return proces != null ? proces.getOperacioId() : null;
	}

	/**
	 * Executa l'acció com un pas del procés: el registra EN_CURS i el marca OK en acabar, o ERROR (amb el missatge de
	 * l'excepció) si falla. L'excepció es torna a llançar sense canvis.
	 */
	public static void pas(String codi, Map<String, String> params, Runnable accio) {
		pas(codi, params, () -> {
			accio.run();
			return null;
		});
	}

	/** Com {@link #pas(String, Map, Runnable)}, per a accions que retornen un resultat. */
	public static <T> T pas(String codi, Map<String, String> params, Supplier<T> accio) {
		ProcesSegonPla proces = ACTUAL.get();
		if (proces == null) {
			return accio.get();
		}
		int index = proces.iniciarPas(codi, params);
		try {
			T resultat = accio.get();
			proces.finalitzarPas(index, ProcesSegonPlaPasEstatEnumDto.OK, null);
			return resultat;
		} catch (RuntimeException | Error ex) {
			proces.finalitzarPas(index, ProcesSegonPlaPasEstatEnumDto.ERROR, missatgeError(ex));
			throw ex;
		}
	}

	/** Informa què s'està fent dins el pas en curs (p.ex. "firmant en servidor"), mentre dura. */
	public static void fase(String fase, Map<String, String> params) {
		ProcesSegonPla proces = ACTUAL.get();
		if (proces != null) {
			proces.fase(fase, params);
		}
	}

	/**
	 * Informa el resultat del pas en curs (p.ex. "3 notificacions actualitzades"), que es mostra a continuació del
	 * text del pas i es manté un cop acabat.
	 */
	public static void resultat(String resultat, Map<String, String> params) {
		ProcesSegonPla proces = ACTUAL.get();
		if (proces != null) {
			proces.resultat(resultat, params);
		}
	}

	/** Afegeix una línia informativa ja acabada (OK) al registre, p.ex. el resultat d'un element concret. */
	public static void info(String codi, Map<String, String> params) {
		ProcesSegonPla proces = ACTUAL.get();
		if (proces != null) {
			proces.afegirPas(codi, params, ProcesSegonPlaPasEstatEnumDto.OK, null);
		}
	}

	/** Afegeix un avís al registre: una incidència que no atura el procés però que l'usuari ha de conèixer. */
	public static void avis(String codi, Map<String, String> params, String detall) {
		ProcesSegonPla proces = ACTUAL.get();
		if (proces != null) {
			proces.afegirPas(codi, params, ProcesSegonPlaPasEstatEnumDto.AVIS, detall);
		}
	}

	/** Construeix el mapa de paràmetres a partir de parelles clau, valor. */
	public static Map<String, String> params(String... clauValor) {
		Map<String, String> params = new LinkedHashMap<>();
		for (int i = 0; i + 1 < clauValor.length; i += 2) {
			params.put(clauValor[i], clauValor[i + 1]);
		}
		return params;
	}

	/** Missatge d'error que es mostra a la línia del pas que ha fallat. */
	static String missatgeError(Throwable ex) {
		if (ex instanceof FirmaServidorException && ((FirmaServidorException) ex).getErrorMsg() != null) {
			return ((FirmaServidorException) ex).getErrorMsg();
		}
		return Utils.getRootMsg(ex);
	}

}
