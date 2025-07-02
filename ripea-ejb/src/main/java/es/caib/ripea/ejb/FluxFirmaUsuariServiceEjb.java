package es.caib.ripea.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.FluxFirmaUsuariDto;
import es.caib.ripea.service.intf.dto.FluxFirmaUsuariFiltreDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxInfoDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.FluxFirmaUsuariService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class FluxFirmaUsuariServiceEjb extends AbstractServiceEjb<FluxFirmaUsuariService> implements FluxFirmaUsuariService {

	@Delegate private FluxFirmaUsuariService delegateService;

	protected void setDelegateService(FluxFirmaUsuariService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public FluxFirmaUsuariDto create(Long entitatId, FluxFirmaUsuariDto flux, PortafirmesFluxInfoDto fluxDetall) throws NotFoundException {
		return delegateService.create(entitatId, flux, fluxDetall);
	}

	@Override
	@RolesAllowed("**")
	public FluxFirmaUsuariDto update(Long id, Long entitatId, PortafirmesFluxInfoDto fluxDetall) throws NotFoundException {
		return delegateService.update(id, entitatId, fluxDetall);
	}

	@Override
	@RolesAllowed("**")
	public FluxFirmaUsuariDto delete(Long entitatId, Long id) throws NotFoundException {
		return delegateService.delete(entitatId, id);
	}

	@Override
	@RolesAllowed("**")
	public FluxFirmaUsuariDto findById(Long entitatId, Long id) throws NotFoundException {
		return delegateService.findById(entitatId, id);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<FluxFirmaUsuariDto> findByEntitatAndUsuariPaginat(Long entitatId, FluxFirmaUsuariFiltreDto filtre,
	                                                                   PaginacioParamsDto paginacioParams) throws NotFoundException {
		return delegateService.findByEntitatAndUsuariPaginat(entitatId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<FluxFirmaUsuariDto> findByEntitatAndUsuari(Long entitatId) throws NotFoundException {
		return delegateService.findByEntitatAndUsuari(entitatId);
	}

}
