package es.caib.ripea.core.ejb;

import es.caib.ripea.core.api.dto.GrupDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.GrupService;

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
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegate.findById(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
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
	@RolesAllowed("IPA_ADMIN")
	public void relacionarAmbMetaExpedient(Long entitatId,
			Long metaExpedientId,
			Long id) {
		delegate.relacionarAmbMetaExpedient(
				entitatId,
				metaExpedientId,
				id);

	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void desvincularAmbMetaExpedient(Long entitatId,
			Long metaExpedientId,
			Long id) {
		delegate.desvincularAmbMetaExpedient(
				entitatId,
				metaExpedientId,
				id);
	}
    
    
    
}
