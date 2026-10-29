package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * La tasca s'ha d'executar un sol cop i només quan la transacció s'ha confirmat (afterCompletion amb
 * STATUS_COMMITTED). Es reprodueix l'ordre de callbacks de JBoss (EJB amb CMT + JtaTransactionManager que hi
 * participa): afterCommit abans del commit JTA real, i després afterCommit i afterCompletion des de la JTA.
 */
class TransactionAfterCommitUtilsTest {

	@AfterEach
	void netejaSincronitzacio() {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.clearSynchronization();
		}
	}

	@Test
	void senseSincronitzacio_executaImmediatament() {
		AtomicInteger execucions = new AtomicInteger();

		TransactionAfterCommitUtils.run(execucions::incrementAndGet);

		assertThat(execucions.get()).isEqualTo(1);
	}

	@Test
	void ambSincronitzacio_afterCommit_noExecuta() {
		AtomicInteger execucions = new AtomicInteger();
		TransactionSynchronizationManager.initSynchronization();

		TransactionAfterCommitUtils.run(execucions::incrementAndGet);
		assertThat(execucions.get()).isZero();

		// A JBoss afterCommit es pot invocar abans del commit JTA real (i un altre cop després).
		TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
		TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);

		assertThat(execucions.get()).isZero();
	}

	@Test
	void ambSincronitzacio_afterCompletionCommitted_executaUnSolCop() {
		AtomicInteger execucions = new AtomicInteger();
		TransactionSynchronizationManager.initSynchronization();

		TransactionAfterCommitUtils.run(execucions::incrementAndGet);
		TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
		completa(TransactionSynchronization.STATUS_COMMITTED);
		completa(TransactionSynchronization.STATUS_COMMITTED);

		assertThat(execucions.get()).isEqualTo(1);
	}

	@Test
	void ambSincronitzacio_rollback_noExecuta() {
		AtomicInteger execucions = new AtomicInteger();
		TransactionSynchronizationManager.initSynchronization();

		TransactionAfterCommitUtils.run(execucions::incrementAndGet);
		completa(TransactionSynchronization.STATUS_ROLLED_BACK);

		assertThat(execucions.get()).isZero();
	}

	@Test
	void ambSincronitzacio_estatDesconegut_noExecuta() {
		AtomicInteger execucions = new AtomicInteger();
		TransactionSynchronizationManager.initSynchronization();

		TransactionAfterCommitUtils.run(execucions::incrementAndGet);
		completa(TransactionSynchronization.STATUS_UNKNOWN);

		assertThat(execucions.get()).isZero();
	}

	private static void completa(int status) {
		TransactionSynchronizationManager.getSynchronizations().forEach(s -> s.afterCompletion(status));
	}

}
