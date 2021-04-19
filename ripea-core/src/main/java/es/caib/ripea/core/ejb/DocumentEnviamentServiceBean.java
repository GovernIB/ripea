/**
 * 
 */
package es.caib.ripea.core.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.DocumentEnviamentDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioDto;
import es.caib.ripea.core.api.dto.DocumentPublicacioDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.DocumentEnviamentService;

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
	public void notificacioCreate(
			Long entitatId,
			Long documentId,
			DocumentNotificacioDto notificacio) {
		 delegate.notificacioCreate(
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
			Long expedientId) {
		return delegate.findAmbExpedient(
				entitatId,
				expedientId);
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
	public void notificacioActualitzarEstat(String identificador, String referencia) {
		delegate.notificacioActualitzarEstat(
				identificador,
				referencia);
	}

	@Override
	@RolesAllowed("tothom")
	public int enviamentsCount(Long entitatId,
			Long expedientId) {
		return delegate.enviamentsCount(entitatId, expedientId);
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
	public Map<String, String> consultaErrorsNotificacio() {
		return delegate.consultaErrorsNotificacio();
	}

}
