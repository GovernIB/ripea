package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.AlertaDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.AlertaService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class AlertaServiceEjb extends AbstractServiceEjb<AlertaService> implements AlertaService {

	@Delegate private AlertaService delegateService;

	protected void setDelegateService(AlertaService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public AlertaDto create(
			AlertaDto alerta) {
		return delegateService.create(alerta);
	}

	@Override
	@RolesAllowed("**")
	public AlertaDto update(
			AlertaDto alerta) throws NotFoundException {
		return delegateService.update(alerta);
	}

	@Override
	@RolesAllowed("**")
	public AlertaDto delete(
			Long id) throws NotFoundException {
		return delegateService.delete(id);
	}

	@Override
	@RolesAllowed("**")
	public AlertaDto find(
			Long id) {
		return delegateService.find(id);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<AlertaDto> findPaginat(
			PaginacioParamsDto paginacioParams) {
		return delegateService.findPaginat(paginacioParams);
	}

}
