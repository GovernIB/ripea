package es.caib.ripea.service.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import es.caib.ripea.service.intf.dto.EntitatDto;

/**
 * Punt d'entrada per refrescar les validacions d'un node després d'un canvi a BBDD sense penalitzar el temps de
 * resposta de l'operació.
 *
 * La feina (recàlcul de validacions + avís a COMANDA + notificació SSE) es difereix dues vegades:
 * <ol>
 * <li>fins que la transacció de l'operació s'ha confirmat ({@link TransactionAfterCommitUtils}), perquè el
 * càlcul es faci sobre dades ja confirmades i el evict no es pugui desfer amb un rollback;</li>
 * <li>a un fil de segon pla ({@link ValidacioRecalculAsyncExecutor}, @Async), perquè la crida a COMANDA i el
 * recàlcul no allarguin la petició.</li>
 * </ol>
 *
 * El context de l'usuari (entitat, òrgan i rol actuals, i autenticació) viu en ThreadLocals del fil de la
 * petició, així que es captura aquí i es passa al fil de segon pla: sense entitat, ConfigHelper resoldria les
 * propietats amb el valor general en lloc del de l'entitat (p.ex. si COMANDA està activa o no).
 */
@Component
public class ValidacioPostCommitHelper {

	@Autowired private ValidacioRecalculAsyncExecutor validacioRecalculAsyncExecutor;
	@Autowired private ConfigHelper configHelper;

	/** Recàlcul amb avís a COMANDA (comportament dels canvis de document i de dada). */
	public void programarRecalculINotificacio(Long nodeId) {
		programarRecalculINotificacio(nodeId, true);
	}

	public void programarRecalculINotificacio(Long nodeId, boolean enviarAvisComanda) {
		if (nodeId == null) {
			return;
		}
		final EntitatDto entitat = ConfigHelper.getEntitat().get();
		final String organCodi = configHelper.getOrganActualCodi();
		final String rolActual = configHelper.getRolActual();
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			// Sense sincronització de Spring la tasca s'engega immediatament (veure TransactionAfterCommitUtils):
			// el fil de segon pla podria llegir dades encara no confirmades i notificar validacions equivocades.
			logger.warn("No hi ha sincronització de transacció activa: el recàlcul de validacions del node " + nodeId
					+ " s'executarà sense esperar el commit.");
		}
		TransactionAfterCommitUtils.run(() -> validacioRecalculAsyncExecutor.recalcularINotificar(
				nodeId,
				enviarAvisComanda,
				entitat,
				organCodi,
				rolActual,
				authentication));
	}

	private static final Logger logger = LoggerFactory.getLogger(ValidacioPostCommitHelper.class);

}
