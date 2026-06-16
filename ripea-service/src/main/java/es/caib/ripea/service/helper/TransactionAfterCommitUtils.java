package es.caib.ripea.service.helper;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Utilitat per executar una tasca un cop la transacció actual s'ha confirmat (afterCommit).
 *
 * S'utilitza per encadenar evicts de cache en segon pla després del commit, evitant que un recàlcul concurrent
 * torni a cachejar dades antigues mentre la transacció encara no s'ha confirmat. Si no hi ha cap transacció
 * activa (p.ex. en tests unitaris), la tasca s'executa immediatament.
 */
public final class TransactionAfterCommitUtils {

	private TransactionAfterCommitUtils() {
	}

	public static void run(final Runnable task) {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					task.run();
				}
			});
		} else {
			task.run();
		}
	}

}
