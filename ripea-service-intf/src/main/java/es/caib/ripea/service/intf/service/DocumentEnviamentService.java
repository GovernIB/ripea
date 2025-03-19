/**
 * 
 */
package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

/**
 * Declaració dels mètodes per a gestionar enviaments dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@PreAuthorize("isAuthenticated()")
public interface DocumentEnviamentService {

	/**
	 * Crea una notificació d'un document de l'expedient a un ciutadà.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param documentId
	 *            Atribut id del document.
	 * @param notificacio
	 *            Dades de la notificació.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public Map<String, String>  notificacioCreate(
			Long entitatId,
			Long documentId,
			DocumentNotificacioDto notificacio) throws NotFoundException;

	/**
	 * Modifica una notificació d'un document de l'expedient a un ciutadà.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param documentId
	 *            Atribut id del document.
	 * @param notificacio
	 *            Dades de la notificació.
	 * @return La notificació modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public DocumentNotificacioDto notificacioUpdate(
			Long entitatId,
			Long documentId,
			DocumentNotificacioDto notificacio) throws NotFoundException;

	/**
	 * Esborra una notificació d'un document de l'expedient a un ciutadà.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param documentId
	 *            Atribut id del document.
	 * @param notificacioId
	 *            L'atribut id de la notificació.
	 * @return La notificació modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public DocumentNotificacioDto notificacioDelete(
			Long entitatId,
			Long documentId,
			Long notificacioId) throws NotFoundException;

	/**
	 * Consulta una notificació de l'expedient a un ciutadà.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param documentId
	 *            Atribut id del document.
	 * @param notificacioId
	 *            L'atribut id de la notificació.
	 * @return La notificació trobada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public DocumentNotificacioDto notificacioFindAmbIdAndDocument(
			Long entitatId,
			Long documentId,
			Long notificacioId) throws NotFoundException;

	public List<RespostaAmpliarPlazo> ampliarPlazoEnviament(AmpliarPlazoForm documentNotificacioDto);
	
	/**
	 * Crea una publicació d'un document de l'expedient a un butlletí oficial.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param documentId
	 *            Atribut id del document.
	 * @param publicacio
	 *            Dades de la publicació.
	 * @return La publicació creada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public DocumentPublicacioDto publicacioCreate(
			Long entitatId,
			Long documentId,
			DocumentPublicacioDto publicacio) throws NotFoundException;

	/**
	 * Modifica una publicació d'un document de l'expedient a un butlletí oficial.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param documentId
	 *            Atribut id del document.
	 * @param publicacio
	 *            Dades de la publicació.
	 * @return La notificació modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public DocumentPublicacioDto publicacioUpdate(
			Long entitatId,
			Long documentId,
			DocumentPublicacioDto publicacio) throws NotFoundException;

	/**
	 * Esborra una publicació d'un document de l'expedient a un butlletí oficial.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param documentId
	 *            Atribut id del document.
	 * @param publicacioId
	 *            L'atribut id de la publicació.
	 * @return La publicació modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public DocumentPublicacioDto publicacioDelete(
			Long entitatId,
			Long documentId,
			Long publicacioId) throws NotFoundException;

	/**
	 * Consulta una publicació de l'expedient a un butlletí oficial.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param documentId
	 *            Atribut id del document.
	 * @param publicacioId
	 *            L'atribut id de la publicació.
	 * @return La publicació trobada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public DocumentPublicacioDto publicacioFindAmbId(
			Long entitatId,
			Long documentId,
			Long publicacioId) throws NotFoundException;

	/**
	 * Retorna la llista d'enviaments associats a un expedient.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param documentEnviamentTipus
	 * @return La llista de notificacions.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public List<DocumentEnviamentDto> findAmbExpedient(
			Long entitatId,
			Long expedientId, 
			DocumentEnviamentTipusEnumDto documentEnviamentTipus) throws NotFoundException;

	/**
	 * Retorna la llista d'enviaments associats a un expedient.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @return La llista de notificacions.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public List<DocumentEnviamentDto> findAmbDocument(
			Long entitatId,
			Long documentId) throws NotFoundException;
	
	/**
	 * Retorna la llista de les notificacions associades a un document.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param expedientId
	 *            Atribut id del document.
	 * @return La llista de notificacions.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public List<DocumentEnviamentDto> findNotificacionsAmbDocument(
			Long entitatId,
			Long documentId) throws NotFoundException;
	

	int enviamentsCount(Long entitatId,
			Long expedientId, 
			DocumentEnviamentTipusEnumDto documentEnviamentTipus);

	DocumentNotificacioDto notificacioFindAmbIdAndExpedient(Long entitatId,
			Long expedientId,
			Long notificacioId);

	@PreAuthorize("isAuthenticated()")
	public boolean checkIfAnyInteressatIsAdministracio(List<Long> interessatsIds);

	@PreAuthorize("isAuthenticated()")
	public boolean checkIfDocumentIsZip(Long documentId);

}