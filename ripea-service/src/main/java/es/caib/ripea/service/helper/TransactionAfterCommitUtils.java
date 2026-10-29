package es.caib.ripea.service.helper;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Utilitat per executar una tasca un cop la transacció actual s'ha confirmat.
 *
 * S'utilitza per encadenar evicts de cache, recàlculs en segon pla i notificacions SSE després del commit, evitant
 * que un lector concurrent (un altre fil, un consumidor JMS) llegeixi o torni a cachejar l'estat anterior mentre
 * la transacció encara no s'ha confirmat. Si no hi ha cap transacció activa (p.ex. en tests unitaris), la tasca
 * s'executa immediatament.
 *
 * La tasca s'executa a afterCompletion(STATUS_COMMITTED) i no a afterCommit(). A JBoss, l'EJB (CMT) obre la
 * transacció JTA i el @Transactional de Spring (JtaTransactionManager) només hi participa: en sortir-ne, Spring
 * invoca afterCommit() sense haver fet cap commit (AbstractPlatformTransactionManager.processCommit), és a dir,
 * ABANS del commit JTA real i del flush de Hibernate. Després registra la sincronització a la transacció JTA, que
 * torna a invocar afterCommit() i afterCompletion() quan el commit ja s'ha fet. A Spring Boot (JpaTransactionManager)
 * i quan Spring obre la seva pròpia transacció JTA, totes dues callbacks arriben després del commit. Així,
 * afterCompletion(STATUS_COMMITTED) és l'únic punt que és posterior al commit en tots els casos, i si la transacció
 * es desfà la tasca no s'executa.
 *
 * El flag garanteix que la tasca s'executi com a màxim una vegada encara que el gestor de transaccions invoqui
 * la callback més d'un cop.
 *
 * A afterCompletion la transacció ja és acabada però els seus recursos encara poden estar lligats al fil: si la
 * tasca accedeix a la BD en aquest mateix fil, ho ha de fer amb una transacció nova (REQUIRES_NEW). Una excepció
 * de la tasca no arriba a qui ha cridat el mètode transaccional: Spring la registra al log i continua.
 */
public final class TransactionAfterCommitUtils {

	private TransactionAfterCommitUtils() {
	}

	public static void run(final Runnable task) {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				private final AtomicBoolean executat = new AtomicBoolean(false);
				@Override
				public void afterCompletion(int status) {
					if (status == STATUS_COMMITTED && executat.compareAndSet(false, true)) {
						task.run();
					}
				}
			});
		} else {
			task.run();
		}
	}

}
