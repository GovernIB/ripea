package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PinbalServeiDto;
import es.caib.ripea.service.intf.service.PinbalServeiService;
import lombok.experimental.Delegate;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class PinbalServeiServiceEjb implements PinbalServeiService {

	@Delegate
	private PinbalServeiService delegateService;

	protected void delegate(PinbalServeiService delegateService) {
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
	@PreAuthorize("isAuthenticated()")
	public List<PinbalServeiDto> findActius() {
		return delegateService.findActius();
	}
	
	@Override
	@PreAuthorize("isAuthenticated()")
	public List<PinbalServeiDto> findAll() {
		return delegateService.findAll();
	}
}