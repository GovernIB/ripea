package es.caib.ripea.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.FluxFirmaUsuariDto;
import es.caib.ripea.core.api.dto.FluxFirmaUsuariFiltreDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PortafirmesFluxInfoDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.FluxFirmaUsuariService;

/**
 * Implementació de FluxFirmaUsuariService com a EJB que empra una clase delegada
 * per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class FluxFirmaUsuariServiceBean implements FluxFirmaUsuariService {

	@Autowired
	FluxFirmaUsuariService delegate;

	@Override
	@RolesAllowed("tothom")
	public FluxFirmaUsuariDto create(Long entitatId, FluxFirmaUsuariDto flux, PortafirmesFluxInfoDto fluxDetall) throws NotFoundException {
		return delegate.create(entitatId, flux, fluxDetall);
	}

	@Override
	@RolesAllowed("tothom")
	public FluxFirmaUsuariDto update(Long id, Long entitatId, PortafirmesFluxInfoDto fluxDetall) throws NotFoundException {
		return delegate.update(id, entitatId, fluxDetall);
	}

	@Override
	@RolesAllowed("tothom")
	public FluxFirmaUsuariDto delete(Long entitatId, Long id) throws NotFoundException {
		return delegate.delete(entitatId, id);
	}

	@Override
	@RolesAllowed("tothom")
	public FluxFirmaUsuariDto findById(Long entitatId, Long id) throws NotFoundException {
		return delegate.findById(entitatId, id);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<FluxFirmaUsuariDto> findByEntitatAndUsuariPaginat(Long entitatId, FluxFirmaUsuariFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		return delegate.findByEntitatAndUsuariPaginat(entitatId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public List<FluxFirmaUsuariDto> findByEntitatAndUsuari(Long entitatId) throws NotFoundException {
		return delegate.findByEntitatAndUsuari(entitatId);
	}

}
