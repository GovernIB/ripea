/**
 * 
 */
package es.caib.ripea.core.ejb;

import es.caib.ripea.core.api.dto.AmpliarPlazoForm;
import es.caib.ripea.core.api.dto.DocumentEnviamentDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioDto;
import es.caib.ripea.core.api.dto.DocumentPublicacioDto;
import es.caib.ripea.core.api.dto.RespostaAmpliarPlazo;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.DocumentEnviamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;
import java.util.Map;

/**
 * Implementació de ContenidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class DocumentEnviamentServiceBean implements DocumentEnviamentService {

	@Autowired
	DocumentEnviamentService delegate;

	@Override
	@RolesAllowed("tothom")
	public Map<String, String>  notificacioCreate(
			Long entitatId,
			Long documentId,
			DocumentNotificacioDto notificacio) {
		 return delegate.notificacioCreate(
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
		return delegate.notificacioUpdate(
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
		return delegate.notificacioDelete(
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
		return delegate.notificacioFindAmbIdAndDocument(
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
		return delegate.publicacioCreate(
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
		return delegate.publicacioUpdate(
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
		return delegate.publicacioFindAmbId(
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
		return delegate.publicacioDelete(
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
		return delegate.findAmbExpedient(
				entitatId,
				expedientId, 
				documentEnviamentTipus);
	}

	@Override
	@RolesAllowed("tothom")
	public List<DocumentEnviamentDto> findAmbDocument(
			Long entitatId,
			Long documentId) {
		return delegate.findAmbDocument(
				entitatId,
				documentId);
	}
	
	@Override
	@RolesAllowed("tothom")
	public List<DocumentEnviamentDto> findNotificacionsAmbDocument(
			Long entitatId, 
			Long documentId)
			throws NotFoundException {
		return delegate.findNotificacionsAmbDocument(
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
		return delegate.enviamentsCount(
				entitatId, 
				expedientId, 
				documentEnviamentTipus);
	}

	@Override
	@RolesAllowed("tothom")
	public DocumentNotificacioDto notificacioFindAmbIdAndExpedient(Long entitatId,
			Long expedientId,
			Long notificacioId) {
		return delegate.notificacioFindAmbIdAndExpedient(entitatId, expedientId, notificacioId);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean checkIfAnyInteressatIsAdministracio(List<Long> interessatsIds) {
		return delegate.checkIfAnyInteressatIsAdministracio(interessatsIds);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean checkIfDocumentIsZip(Long documentId) {
		return delegate.checkIfDocumentIsZip(documentId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<RespostaAmpliarPlazo> ampliarPlazoEnviament(AmpliarPlazoForm documentNotificacioDto) {
		return delegate.ampliarPlazoEnviament(documentNotificacioDto);
	}
}