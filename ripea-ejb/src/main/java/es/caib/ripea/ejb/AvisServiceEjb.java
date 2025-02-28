/**
 * 
 */
package es.caib.ripea.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.AvisDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.service.AvisService;
import lombok.experimental.Delegate;

@Stateless
public class AvisServiceEjb extends AbstractServiceEjb<AvisService> implements AvisService {

	@Delegate private AvisService delegateService;

	protected void setDelegateService(AvisService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public AvisDto create(AvisDto avis) {
		return delegateService.create(avis);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public AvisDto update(AvisDto avis) {
		return delegateService.update(avis);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public AvisDto updateActiva(Long id, boolean activa) {
		return delegateService.updateActiva(id, activa);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public AvisDto delete(Long id) {
		return delegateService.delete(id);
	}

	@Override
	@RolesAllowed("**")
	public AvisDto findById(Long id) {
		return delegateService.findById(id);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<AvisDto> findPaginat(PaginacioParamsDto paginacioParams) {
		return delegateService.findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<AvisDto> findActive() {
		return delegateService.findActive();
	}

    @Override
	@RolesAllowed("**")
    public List<AvisDto> findActiveAdmin(Long entitatId) {
        return delegateService.findActiveAdmin(entitatId);
    }

}
