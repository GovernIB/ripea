package es.caib.ripea.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PinbalServeiDto;
import es.caib.ripea.service.intf.service.PinbalServeiService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class PinbalServeiServiceEjb extends AbstractServiceEjb<PinbalServeiService> implements PinbalServeiService {

	@Delegate private PinbalServeiService delegateService;

	protected void setDelegateService(PinbalServeiService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public PaginaDto<PinbalServeiDto> findPaginat(PaginacioParamsDto paginacioParams) {
		return delegateService.findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public PinbalServeiDto update(PinbalServeiDto pinbalServei) {
		return delegateService.update(pinbalServei);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public PinbalServeiDto findById(Long id) {
		return delegateService.findById(id);
	}

	@Override
	@RolesAllowed("**")
	public List<PinbalServeiDto> findActius() {
		return delegateService.findActius();
	}
	
	@Override
	@RolesAllowed("**")
	public List<PinbalServeiDto> findAll() {
		return delegateService.findAll();
	}
}