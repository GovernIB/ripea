package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.ExpedientEstatService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class ExpedientEstatServiceEjb implements ExpedientEstatService {

	@Delegate
	private ExpedientEstatService delegateService;

	protected void setDelegateService(ExpedientEstatService delegateService) {
		this.delegateService = delegateService;
	}

	@RolesAllowed("**")
	public PaginaDto<ExpedientEstatDto> findExpedientEstatByMetaExpedientPaginat(
			Long entitatId,
			Long metaExpedientId,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findExpedientEstatByMetaExpedientPaginat(entitatId, metaExpedientId, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public ExpedientEstatDto findExpedientEstatById(Long entitatId, Long id) {
		return delegateService.findExpedientEstatById(entitatId, id);
	}

	@Override
	@RolesAllowed("**")
	public ExpedientEstatDto createExpedientEstat(Long entitatId, ExpedientEstatDto estat, String rolActual, Long organId) {
		return delegateService.createExpedientEstat(entitatId, estat, rolActual, organId);
	}

	@Override
	@RolesAllowed("**")
	public ExpedientEstatDto updateExpedientEstat(Long entitatId, ExpedientEstatDto estat, String rolActual, Long organId) {
		return delegateService.updateExpedientEstat(entitatId, estat, rolActual, organId);
	}

	@Override
	@RolesAllowed("**")
	public ExpedientEstatDto moveTo(
			Long entitatId,
			Long metaExpedientId,
			Long expedientEstatId,
			int posicio, String rolActual) throws NotFoundException {
		return delegateService.moveTo(entitatId, metaExpedientId, expedientEstatId, posicio, rolActual);
	}

	@Override
	@RolesAllowed("**")
	public ExpedientEstatDto deleteExpedientEstat(Long entitatId, Long expedientEstatId, String rolActual, Long organId) throws NotFoundException {
		return delegateService.deleteExpedientEstat(entitatId, expedientEstatId, rolActual, organId);
	}

	@Override
	@RolesAllowed("**")
	public List<ExpedientEstatDto> findExpedientEstats(Long entitatId, Long expedientId, String rolActual) {
		return delegateService.findExpedientEstats(entitatId, expedientId, rolActual);
	}

	@Override
	@RolesAllowed("**")
	public ExpedientDto changeExpedientEstat(
			Long entitatId,
			Long expedientId,
			Long expedientEstatId) {
		return delegateService.changeExpedientEstat(
				entitatId,
				expedientId,
				expedientEstatId);
	}

	@Override
	@RolesAllowed("**")
	public List<ExpedientEstatDto> findExpedientEstatsByMetaExpedient(Long entitatId, Long metaExpedientId) {
		return delegateService.findExpedientEstatsByMetaExpedient(entitatId, metaExpedientId);
	}

	@Override
	@RolesAllowed("**")
	public ResultDto<ExpedientDto> findExpedientsPerCanviEstatMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual,
			ResultEnumDto resultEnum) throws NotFoundException {
		return delegateService.findExpedientsPerCanviEstatMassiu(
				entitatId,
				filtre,
				paginacioParams,
				rolActual,
				resultEnum);
	}



}
