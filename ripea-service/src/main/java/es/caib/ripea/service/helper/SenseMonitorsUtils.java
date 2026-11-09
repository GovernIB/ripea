package es.caib.ripea.service.helper;

import java.util.concurrent.Callable;

/**
 * Execució d'una acció sense registrar-ne res als monitors de l'aplicació: ni al monitor d'integracions
 * (IPA_INTEGRACIO_ACCIO), ni al monitor d'excepcions (IPA_EXCEPCIO_LOG), ni a les mètriques d'integracions
 * (METRICS@Integracions.*) que es publiquen a COMANDA.
 *
 * És per als processos massius en segon pla que ja deixen constància de cada element al log i al monitor de tasques.
 * P.ex. la incorporació nocturna de justificants de registre fa milers d'accions amb l'Arxiu cada nit: els
 * stacktraces dels errors omplien el tablespace dels LOB (RIPEA_LOB), i durant l'execució l'estat de l'Arxiu a
 * COMANDA, que es calcula amb les darreres accions del monitor, només reflectia aquests errors. Els errors que no es
 * registren als monitors s'escriuen al log de l'aplicació (veure IntegracioHelper i ExcepcioLogHelper).
 *
 * L'indicador és del fil: només afecta el que s'executa al mateix fil que l'acció. El que s'executi en un altre fil
 * (tasques asíncrones, listeners de respostes) es continua registrant.
 */
public final class SenseMonitorsUtils {

	private static final ThreadLocal<Boolean> ACTIU = new ThreadLocal<>();

	private SenseMonitorsUtils() {
	}

	/**
	 * Executa l'acció sense registrar-la als monitors. En acabar, també si l'acció falla, el fil torna a l'estat
	 * anterior: si ja s'executava sense monitors (crides niades), hi continua.
	 */
	public static <T> T executar(Callable<T> accio) throws Exception {
		boolean anterior = isActiu();
		ACTIU.set(Boolean.TRUE);
		try {
			return accio.call();
		} finally {
			if (!anterior) {
				ACTIU.remove();
			}
		}
	}

	/** Indica si el fil actual s'executa sense monitors. */
	public static boolean isActiu() {
		return Boolean.TRUE.equals(ACTIU.get());
	}

	/** Treu l'indicador del fil. Els fils del planificador es reutilitzen i la tasca següent no l'ha d'heretar. */
	public static void netejar() {
		ACTIU.remove();
	}

}
