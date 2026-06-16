package es.caib.ripea.service.helper;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.model.sse.ErrorsValidacioEvictEvent;

/**
 * Helper dedicat al buidat en segon pla de la cache de validacions ("errorsValidacioNode").
 *
 * Es manté com un bean independent amb dependències mínimes (només repositoris i CacheManager) per evitar
 * que la creació del proxy @Async participi en les referències circulars del graf de beans de RIPEA. Si el
 * mètode @Async es posa dins un bean que sí està dins el cicle (com CacheHelper), Spring l'injecta en la seva
 * versió "en cru" abans d'embolicar-lo amb el proxy async i el desplegament falla amb
 * BeanCurrentlyInCreationException. Aquest bean només depèn de fulles del graf, així que cap cicle hi passa.
 *
 * El evict és pla (no recalcula la validesa): es recalcula peresosament quan algú obre o llista el node. El
 * cridador l'invoca després del commit de la transacció (veure TransactionAfterCommitUtils) perquè el recàlcul
 * posterior vegi ja el canvi persistit i no torni a cachejar dades antigues.
 *
 * En el cas dels expedients, a més del evict, publica per JMS els ids afectats (event lleuger
 * ErrorsValidacioEvictEvent, sense payload). El consumidor (SseResourceController, al WAR) recalcula i notifica
 * via SSE NOMÉS els expedients que algú està veient en aquell moment, de manera que els canvis de validació es
 * reflecteixen al navegador sense recarregar la pàgina però sense el cost de recalcular tots els expedients del
 * procediment. La publicació es fa amb JmsTemplate (un bean fulla), de manera que el helper segueix sense
 * participar en cap referència circular (veure el comentari sobre @Async de més amunt).
 */
@Component
public class ValidacioCacheEvictHelper {

	@Autowired private DocumentRepository documentRepository;
	@Autowired private ExpedientRepository expedientRepository;
	@Autowired private CacheManager cacheManager;
	@Autowired private JmsTemplate jmsTemplate;

	/**
	 * Buida la validació dels documents d'un meta-document que estan dins expedients oberts. S'utilitza quan
	 * canvia l'obligatorietat o l'activació d'una metadada d'un meta-document.
	 */
	@Async
	public void evictValidacioDocumentsPerMetaDocumentEnBackground(Long metaDocumentId) {
		List<Long> documentIds = documentRepository.findIdsByMetaNodeIdAndExpedientEstatAndNoEsborrat(
				metaDocumentId,
				ExpedientEstatEnumDto.OBERT);
		evictNodes(documentIds, "documents del meta-document " + metaDocumentId);
	}

	/**
	 * Buida la validació dels expedients d'un meta-expedient. S'utilitza quan canvia un meta-document o una
	 * metadada del procediment, o quan s'actualitza el propi meta-expedient.
	 * Si {@code estat} és null s'agafen tots els expedients no esborrats; en cas contrari només els d'aquell estat.
	 */
	@Async
	public void evictValidacioExpedientsPerMetaExpedientEnBackground(Long metaExpedientId, ExpedientEstatEnumDto estat) {
		List<Long> expedientIds = expedientRepository.findIdsByMetaExpedientIdAndEstatAndNoEsborrat(
				metaExpedientId,
				estat);
		evictNodes(expedientIds, "expedients del meta-expedient " + metaExpedientId);
		notificarEvictExpedients(expedientIds);
	}

	/**
	 * Publica per JMS els ids dels expedients afectats perquè el consumidor SSE (SseResourceController) recalculi i
	 * notifiqui via SSE només els que algú estigui veient. No recalcula res aquí: només envia els ids.
	 */
	private void notificarEvictExpedients(List<Long> expedientIds) {
		if (expedientIds == null || expedientIds.isEmpty()) {
			return;
		}
		try {
			jmsTemplate.convertAndSend("errorsValidacioEvict", new ErrorsValidacioEvictEvent(expedientIds));
		} catch (Exception ex) {
			logger.error("Error notificant per SSE el evict de validacions dels expedients " + expedientIds, ex);
		}
	}

	private void evictNodes(List<Long> nodeIds, String descripcio) {
		if (nodeIds == null || nodeIds.isEmpty()) {
			return;
		}
		Cache cache = cacheManager.getCache("errorsValidacioNode");
		if (cache == null) {
			return;
		}
		for (Long nodeId : nodeIds) {
			cache.evict(nodeId);
		}
		logger.debug("### EVICT en segon pla de validacions de " + nodeIds.size() + " " + descripcio);
	}

	private static final Logger logger = LoggerFactory.getLogger(ValidacioCacheEvictHelper.class);

}
