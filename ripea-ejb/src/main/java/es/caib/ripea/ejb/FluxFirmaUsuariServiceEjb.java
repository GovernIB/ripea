package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.FluxFirmaUsuariService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de FluxFirmaUsuariService com a EJB que empra una clase delegada
 * per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class FluxFirmaUsuariServiceEjb implements FluxFirmaUsuariService {

	@Delegate
	private FluxFirmaUsuariService delegateService;

	protected void setDelegateService(FluxFirmaUsuariService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("tothom")
	public FluxFirmaUsuariDto create(Long entitatId, FluxFirmaUsuariDto flux, PortafirmesFluxInfoDto fluxDetall) throws NotFoundException {
		return delegateService.create(entitatId, flux, fluxDetall);
	}

	@Override
	@RolesAllowed("tothom")
	public FluxFirmaUsuariDto update(Long id, Long entitatId, PortafirmesFluxInfoDto fluxDetall) throws NotFoundException {
		return delegateService.update(id, entitatId, fluxDetall);
	}

	@Override
	@RolesAllowed("tothom")
	public FluxFirmaUsuariDto delete(Long entitatId, Long id) throws NotFoundException {
		return delegateService.delete(entitatId, id);
	}

	@Override
	@RolesAllowed("tothom")
	public FluxFirmaUsuariDto findById(Long entitatId, Long id) throws NotFoundException {
		return delegateService.findById(entitatId, id);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<FluxFirmaUsuariDto> findByEntitatAndUsuariPaginat(Long entitatId, FluxFirmaUsuariFiltreDto filtre,
	                                                                   PaginacioParamsDto paginacioParams) throws NotFoundException {
		return delegateService.findByEntitatAndUsuariPaginat(entitatId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public List<FluxFirmaUsuariDto> findByEntitatAndUsuari(Long entitatId) throws NotFoundException {
		return delegateService.findByEntitatAndUsuari(entitatId);
	}

}
