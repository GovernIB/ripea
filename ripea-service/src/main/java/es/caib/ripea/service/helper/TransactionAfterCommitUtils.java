package es.caib.ripea.service.helper;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Utilitat per executar una tasca un cop la transacció actual s'ha confirmat (afterCommit).
 *
 * S'utilitza per encadenar evicts de cache en segon pla després del commit, evitant que un recàlcul concurrent
 * torni a cachejar dades antigues mentre la transacció encara no s'ha confirmat. Si no hi ha cap transacció
 * activa (p.ex. en tests unitaris), la tasca s'executa immediatament.
 *
 * IMPORTANT: en aquest desplegament (EJB amb CMT que obre la transacció JTA + un @Transactional de Spring que hi
 * participa amb un JtaTransactionManager) s'ha comprovat que afterCommit() es pot invocar més d'un cop sobre la
 * mateixa sincronització. Per això protegim la tasca amb un flag perquè s'executi exactament una vegada; altrament
 * els evicts (i el seu @Async / notificació SSE) es disparaven dues vegades per una sola operació.
 */
public final class TransactionAfterCommitUtils {

	private TransactionAfterCommitUtils() {
	}

	public static void run(final Runnable task) {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				private final AtomicBoolean executat = new AtomicBoolean(false);
				@Override
				public void afterCommit() {
					if (executat.compareAndSet(false, true)) {
						task.run();
					}
				}
			});
		} else {
			task.run();
		}
	}

}
