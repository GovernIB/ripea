package es.caib.ripea.service.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.model.sse.ErrorsValidacioChangedEvent;

/**
 * Recàlcul de les validacions d'un node i notificació del resultat (SSE i avís a COMANDA) en segon pla.
 *
 * Va en un bean separat de {@link ValidacioPostCommitHelper} perquè @Async només actua quan la crida travessa
 * el proxy de Spring: si el mètode es cridàs des del mateix bean (self-invocation) o des d'un mètode privat,
 * s'executaria al fil de la petició i la feina seguiria essent síncrona.
 *
 * El fil que l'executa és d'un pool i no és el de la petició: no hi ha ni els ThreadLocals de ConfigHelper
 * (entitat, òrgan i rol actuals) ni SecurityContext ni sessió de Hibernate oberta. Per això el context es
 * captura al fil de la petició, es passa per paràmetre i es restableix aquí; i el recàlcul es fa dins
 * {@link CacheHelper#findErrorsValidacioPerNodeAndSendComanda(Long)}, marcat @Transactional(readOnly = true),
 * que obre la sessió que necessiten les relacions lazy.
 *
 * Aquest bean només l'injecta {@link ValidacioPostCommitHelper}, de manera que cap referència circular del graf
 * de beans hi passa pel mig (el proxy de @Async no suporta l'exposició anticipada que resol els cicles; veure
 * el comentari de {@link ValidacioCacheEvictHelper}).
 */
@Component
public class ValidacioRecalculAsyncExecutor {

	@Autowired private CacheHelper cacheHelper;
	@Autowired private EventHelper eventHelper;

	/**
	 * @param enviarAvisComanda si a més de notificar per SSE s'ha d'enviar l'avís de validacions a COMANDA.
	 *        Es manté com a paràmetre perquè no tots els orígens ho feien: els canvis de document i de dada sí,
	 *        i els d'interessat no.
	 */
	@Async
	public void recalcularINotificar(
			Long nodeId,
			boolean enviarAvisComanda,
			EntitatDto entitat,
			String organCodi,
			String rolActual,
			Authentication authentication) {
		try {
			inicialitzarContext(entitat, organCodi, rolActual, authentication);
			// Primer el evict, i després el càlcul amb dades ja confirmades a BBDD
			cacheHelper.evictErrorsValidacioPerNode(nodeId);
			ErrorsValidacioChangedEvent event = new ErrorsValidacioChangedEvent(
					nodeId,
					enviarAvisComanda ?
							cacheHelper.findErrorsValidacioPerNodeAndSendComanda(nodeId) :
							cacheHelper.findErrorsValidacioPerNodeEnTransaccio(nodeId));
			eventHelper.notifyErrorsValidacio(event);
		} catch (Exception ex) {
			// La petició ja ha respost a l'usuari: aquí només es pot deixar constància de l'error.
			logger.error("Error recalculant i notificant les validacions del node " + nodeId, ex);
		} finally {
			netejarContext();
		}
	}

	private void inicialitzarContext(
			EntitatDto entitat,
			String organCodi,
			String rolActual,
			Authentication authentication) {
		ConfigHelper.setEntitat(entitat);
		ConfigHelper.setOrganCodi(organCodi);
		ConfigHelper.setRol(rolActual);
		if (authentication != null) {
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
	}

	/** Els fils del pool es reutilitzen: cal deixar-los sense context per no contaminar la tasca següent. */
	private void netejarContext() {
		ConfigHelper.setEntitat(null);
		ConfigHelper.setOrganCodi(null);
		ConfigHelper.setRol(null);
		SecurityContextHolder.clearContext();
	}

	private static final Logger logger = LoggerFactory.getLogger(ValidacioRecalculAsyncExecutor.class);

}
