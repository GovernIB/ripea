package es.caib.ripea.core.ejb;

import es.caib.ripea.core.api.dto.GrupDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.GrupService;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;
import org.springframework.beans.factory.annotation.Autowired;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class GrupServiceBean implements GrupService {

    @Autowired
    private GrupService delegate;
    
    
    
	@Override
	@RolesAllowed("IPA_ADMIN")
	public GrupDto create(
			Long entitatId, 
			GrupDto tipusDocumental) throws NotFoundException {
		return delegate.create(
				entitatId, 
				tipusDocumental);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public GrupDto update(
			Long entitatId, 
			GrupDto tipusDocumental) throws NotFoundException {
		return delegate.update(
				entitatId,
				tipusDocumental);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public GrupDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegate.delete(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public GrupDto findById(
			Long id) throws NotFoundException {
		return delegate.findById(
				id);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<GrupDto> findByEntitatPaginat(
			Long entitatId,
			Long metaExpedientId, 
			PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		return delegate.findByEntitatPaginat(
				entitatId, 
				metaExpedientId, 
				paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public void relacionarAmbMetaExpedient(Long entitatId,
			Long metaExpedientId,
			Long id, String rolActual, Long organId) {
		delegate.relacionarAmbMetaExpedient(
				entitatId,
				metaExpedientId,
				id, rolActual, organId);

	}

	@Override
	@RolesAllowed("tothom")
	public void desvincularAmbMetaExpedient(Long entitatId,
			Long metaExpedientId,
			Long id, String rolActual, Long organId) {
		delegate.desvincularAmbMetaExpedient(
				entitatId,
				metaExpedientId,
				id, rolActual, organId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<PermisDto> findPermisos(
			Long id) {
		return delegate.findPermisos(id);
	}

	@Override
	@RolesAllowed("tothom")
	public void updatePermis(
			Long id,
			PermisDto permis) {
		delegate.updatePermis(id, permis);
	}

	@Override
	@RolesAllowed("tothom")
	public void deletePermis(
			Long id,
			Long permisId) {
		delegate.deletePermis(id, permisId);
	}
    
	@Override
	@RolesAllowed("tothom")
	public boolean checkIfAlreadyExistsWithCodi(
			Long entitatId,
			String codi) {
		return delegate.checkIfAlreadyExistsWithCodi(
				entitatId,
				codi);
	}
    
}