/**
 * 
 */
package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.DocumentEnviamentService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * Implementació de ContenidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class DocumentEnviamentServiceEjb implements DocumentEnviamentService {

	@Delegate
	private DocumentEnviamentService delegateService;

	protected void setDelegateService(DocumentEnviamentService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("tothom")
	public Map<String, String>  notificacioCreate(
			Long entitatId,
			Long documentId,
			DocumentNotificacioDto notificacio) {
		 return delegateService.notificacioCreate(
				entitatId,
				documentId,
				notificacio);
	}

	@Override
	@RolesAllowed("tothom")
	public DocumentNotificacioDto notificacioUpdate(
			Long entitatId,
			Long documentId,
			DocumentNotificacioDto notificacio) {
		return delegateService.notificacioUpdate(
				entitatId,
				documentId,
				notificacio);
	}

	@Override
	@RolesAllowed("tothom")
	public DocumentNotificacioDto notificacioDelete(
			Long entitatId,
			Long documentId,
			Long notificacioId) {
		return delegateService.notificacioDelete(
				entitatId,
				documentId,
				notificacioId);
	}

	@Override
	@RolesAllowed("tothom")
	public DocumentNotificacioDto notificacioFindAmbIdAndDocument(
			Long entitatId,
			Long documentId,
			Long notificacioId) {
		return delegateService.notificacioFindAmbIdAndDocument(
				entitatId,
				documentId,
				notificacioId);
	}

	@Override
	@RolesAllowed("tothom")
	public DocumentPublicacioDto publicacioCreate(
			Long entitatId,
			Long documentId,
			DocumentPublicacioDto publicacio) {
		return delegateService.publicacioCreate(
				entitatId,
				documentId,
				publicacio);
	}

	@Override
	@RolesAllowed("tothom")
	public DocumentPublicacioDto publicacioUpdate(
			Long entitatId,
			Long documentId,
			DocumentPublicacioDto publicacio) {
		return delegateService.publicacioUpdate(
				entitatId,
				documentId,
				publicacio);
	}

	@Override
	@RolesAllowed("tothom")
	public DocumentPublicacioDto publicacioFindAmbId(
			Long entitatId,
			Long documentId,
			Long publicacioId) {
		return delegateService.publicacioFindAmbId(
				entitatId,
				documentId,
				publicacioId);
	}
	
	@Override
	@RolesAllowed("tothom")
	public DocumentPublicacioDto publicacioDelete(
			Long entitatId,
			Long documentId,
			Long publicacioId) {
		return delegateService.publicacioDelete(
				entitatId,
				documentId,
				publicacioId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<DocumentEnviamentDto> findAmbExpedient(
			Long entitatId,
			Long expedientId, 
			DocumentEnviamentTipusEnumDto documentEnviamentTipus) {
		return delegateService.findAmbExpedient(
				entitatId,
				expedientId, 
				documentEnviamentTipus);
	}

	@Override
	@RolesAllowed("tothom")
	public List<DocumentEnviamentDto> findAmbDocument(
			Long entitatId,
			Long documentId) {
		return delegateService.findAmbDocument(
				entitatId,
				documentId);
	}
	
	@Override
	@RolesAllowed("tothom")
	public List<DocumentEnviamentDto> findNotificacionsAmbDocument(
			Long entitatId, 
			Long documentId)
			throws NotFoundException {
		return delegateService.findNotificacionsAmbDocument(
				entitatId, 
				documentId);
	}

//	@Override
//	public void notificacioActualitzarEstat() {
//		delegate.notificacioActualitzarEstat();
//	}


	@Override
	@RolesAllowed("tothom")
	public int enviamentsCount(
			Long entitatId,
			Long expedientId, 
			DocumentEnviamentTipusEnumDto documentEnviamentTipus) {
		return delegateService.enviamentsCount(
				entitatId, 
				expedientId, 
				documentEnviamentTipus);
	}

	@Override
	@RolesAllowed("tothom")
	public DocumentNotificacioDto notificacioFindAmbIdAndExpedient(Long entitatId,
			Long expedientId,
			Long notificacioId) {
		return delegateService.notificacioFindAmbIdAndExpedient(entitatId, expedientId, notificacioId);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean checkIfAnyInteressatIsAdministracio(List<Long> interessatsIds) {
		return delegateService.checkIfAnyInteressatIsAdministracio(interessatsIds);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean checkIfDocumentIsZip(Long documentId) {
		return delegateService.checkIfDocumentIsZip(documentId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<RespostaAmpliarPlazo> ampliarPlazoEnviament(AmpliarPlazoForm documentNotificacioDto) {
		return delegateService.ampliarPlazoEnviament(documentNotificacioDto);
	}
}