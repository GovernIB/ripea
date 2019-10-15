/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.List;

import es.caib.ripea.core.api.dto.DocumentEnviamentDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioDto;
import es.caib.ripea.core.api.dto.DocumentPublicacioDto;
import es.caib.ripea.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a gestionar enviaments dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
	 * @return La notificació creada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public DocumentNotificacioDto notificacioCreate(
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
	public DocumentNotificacioDto notificacioFindAmbId(
			Long entitatId,
			Long documentId,
			Long notificacioId) throws NotFoundException;

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
	 * @return La llista de notificacions.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public List<DocumentEnviamentDto> findAmbExpedient(
			Long entitatId,
			Long expedientId) throws NotFoundException;

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
	 * Actualitza l'estat d'un enviament de la notificació.
	 * 
	 * @param identificador
	 * 			Identificador de la notificació 
	 * @param referencia
	 * 			Referència de l'enviament
	 * 
	 */
	public void notificacioActualitzarEstat(String identificador, String referencia);

	int enviamentsCount(Long entitatId,
			Long expedientId);
	
//	/**
//	 * Actualitza l'estat de les notificacions pendents de forma periòdica.
//	 */
//	public void notificacioActualitzarEstat();

}