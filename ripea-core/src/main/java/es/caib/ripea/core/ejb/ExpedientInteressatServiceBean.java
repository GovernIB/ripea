/**
 * 
 */
package es.caib.ripea.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.InteressatAdministracioDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.core.api.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.core.api.service.ExpedientInteressatService;

/**
 * Implementació de InteressatService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientInteressatServiceBean implements ExpedientInteressatService {

	@Autowired
	ExpedientInteressatService delegate;



	@Override
	@RolesAllowed("tothom")
	public InteressatDto create(
			Long entitatId,
			Long expedientId,
			InteressatDto interessat) {
		return delegate.create(entitatId, expedientId, interessat);
	}
	
	@Override
	@RolesAllowed("tothom")
	public InteressatDto create(
			Long entitatId,
			Long expedientId,
			Long interessatId,
			InteressatDto representant,
			boolean propagarArxiu) {
		return delegate.create(entitatId, expedientId, interessatId, representant, propagarArxiu);
	}

	@Override
	@RolesAllowed("tothom")
	public InteressatDto update(
			Long entitatId,
			Long expedientId,
			InteressatDto interessat) {
		return delegate.update(entitatId, expedientId, interessat);
	}
	
	@Override
	@RolesAllowed("tothom")
	public InteressatDto update(
			Long entitatId,
			Long expedientId,
			Long interessatId,
			InteressatDto representant) {
		return delegate.update(entitatId, expedientId, interessatId, representant);
	}
	
	@Override
	@RolesAllowed("tothom")
	public void delete(
			Long entitatId,
			Long expedientId,
			Long interessatId) {
		delegate.delete(entitatId, expedientId, interessatId);
	}
	
	@Override
	@RolesAllowed("tothom")
	public void delete(
			Long entitatId,
			Long expedientId,
			Long interessatId,
			Long representantId) {
		delegate.delete(entitatId, expedientId, interessatId, representantId);
	}
	
	@Override
	@RolesAllowed("tothom")
	public InteressatDto findById(
			Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed("tothom")
	public InteressatDto findRepresentantById(
			Long interessatId, 
			Long id) {
		return delegate.findRepresentantById(interessatId, id);
	}
	
	@Override
	@RolesAllowed("tothom")
	public List<InteressatDto> findByExpedient(
			Long entitatId,
			Long expedientId) {
		return delegate.findByExpedient(entitatId, expedientId);
	}
	
	@Override
	@RolesAllowed("tothom")
	public long countByExpedient(
			Long entitatId,
			Long expedientId) {
		return delegate.countByExpedient(entitatId, expedientId);
	}
	
	@Override
	@RolesAllowed("tothom")
	public List<InteressatDto> findAmbDocumentPerNotificacio(
			Long entitatId,
			Long documentId) {
		return delegate.findAmbDocumentPerNotificacio(entitatId, documentId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<InteressatPersonaFisicaDto> findByFiltrePersonaFisica(
			String documentNum,
			String nom,
			String llinatge1,
			String llinatge2,
			Long expedientId) {
		return delegate.findByFiltrePersonaFisica(
				documentNum,
				nom,
				llinatge1,
				llinatge2,
				expedientId);
	}
	
	@Override
	@RolesAllowed("tothom")
	public List<InteressatPersonaJuridicaDto> findByFiltrePersonaJuridica(
			String documentNum,
			String raoSocial,
			Long expedientId) {
		return delegate.findByFiltrePersonaJuridica(
				documentNum, 
				raoSocial,
				expedientId);
	}
	
	@Override
	@RolesAllowed("tothom")
	public List<InteressatAdministracioDto> findByFiltreAdministracio(
			String organCodi,
			Long expedientId) {
		return delegate.findByFiltreAdministracio(
				organCodi,
				expedientId);
	}



}
