/**
 * 
 */
package es.caib.ripea.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.AmpliarPlazoForm;
import es.caib.ripea.service.intf.dto.DocumentEnviamentDto;
import es.caib.ripea.service.intf.dto.DocumentEnviamentTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioDto;
import es.caib.ripea.service.intf.dto.DocumentPublicacioDto;
import es.caib.ripea.service.intf.dto.RespostaAmpliarPlazo;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.DocumentEnviamentService;
import lombok.experimental.Delegate;

@Stateless
public class DocumentEnviamentServiceEjb extends AbstractServiceEjb<DocumentEnviamentService> implements DocumentEnviamentService {

	@Delegate private DocumentEnviamentService delegateService;

	protected void setDelegateService(DocumentEnviamentService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
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
	@RolesAllowed("**")
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
	@RolesAllowed("**")
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
	@RolesAllowed("**")
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
	@RolesAllowed("**")
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
	@RolesAllowed("**")
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
	@RolesAllowed("**")
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
	@RolesAllowed("**")
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
	@RolesAllowed("**")
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
	@RolesAllowed("**")
	public List<DocumentEnviamentDto> findAmbDocument(
			Long entitatId,
			Long documentId) {
		return delegateService.findAmbDocument(
				entitatId,
				documentId);
	}
	
	@Override
	@RolesAllowed("**")
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
	@RolesAllowed("**")
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
	@RolesAllowed("**")
	public DocumentNotificacioDto notificacioFindAmbIdAndExpedient(Long entitatId,
			Long expedientId,
			Long notificacioId) {
		return delegateService.notificacioFindAmbIdAndExpedient(entitatId, expedientId, notificacioId);
	}

	@Override
	@RolesAllowed("**")
	public boolean checkIfAnyInteressatIsAdministracio(List<Long> interessatsIds) {
		return delegateService.checkIfAnyInteressatIsAdministracio(interessatsIds);
	}

	@Override
	@RolesAllowed("**")
	public boolean checkIfDocumentIsZip(Long documentId) {
		return delegateService.checkIfDocumentIsZip(documentId);
	}

	@Override
	@RolesAllowed("**")
	public List<RespostaAmpliarPlazo> ampliarPlazoEnviament(AmpliarPlazoForm documentNotificacioDto) {
		return delegateService.ampliarPlazoEnviament(documentNotificacioDto);
	}
}