package es.caib.ripea.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.ArxiuPendentTipusEnumDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioListDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.ResultDto;
import es.caib.ripea.service.intf.dto.ResultEnumDto;
import es.caib.ripea.service.intf.dto.SeguimentArxiuPendentsDto;
import es.caib.ripea.service.intf.dto.SeguimentArxiuPendentsFiltreDto;
import es.caib.ripea.service.intf.dto.SeguimentConsultaFiltreDto;
import es.caib.ripea.service.intf.dto.SeguimentConsultaPinbalDto;
import es.caib.ripea.service.intf.dto.SeguimentDto;
import es.caib.ripea.service.intf.dto.SeguimentFiltreDto;
import es.caib.ripea.service.intf.dto.SeguimentNotificacionsFiltreDto;
import es.caib.ripea.service.intf.service.SeguimentService;
import lombok.experimental.Delegate;

@Stateless
public  class SeguimentServiceEjb extends AbstractServiceEjb<SeguimentService> implements SeguimentService {

	@Delegate private SeguimentService delegateService;

	protected void setDelegateService(SeguimentService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<SeguimentDto> findPortafirmesEnviaments(
			Long entitatId,
			SeguimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual) {
		return delegateService.findPortafirmesEnviaments(entitatId, filtre, paginacioParams, rolActual);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public ResultDto<SeguimentDto> findNotificacionsEnviaments(
			Long entitatId,
			SeguimentNotificacionsFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			ResultEnumDto resultEnum,
			String rolActual) {
		return delegateService.findNotificacionsEnviaments(
				entitatId,
				filtre,
				paginacioParams, 
				resultEnum,
				rolActual);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<SeguimentDto> findTasques(
			Long entitatId,
			SeguimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findTasques(
				entitatId,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<ExpedientPeticioListDto> findAnotacionsPendents(
			Long entitatId,
			ExpedientPeticioFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual) {
		return delegateService.findAnotacionsPendents(
				entitatId,
				filtre,
				paginacioParams,
				rolActual);
	}

	@Override
	@RolesAllowed("**")
	public ResultDto<SeguimentArxiuPendentsDto> findPendentsArxiu(
			Long entitatId,
			SeguimentArxiuPendentsFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual,
			ResultEnumDto resultEnum,
			ArxiuPendentTipusEnumDto arxiuPendentTipusEnum,
			Long organActual) {
		return delegateService.findPendentsArxiu(
				entitatId,
				filtre,
				paginacioParams,
				rolActual,
				resultEnum,
				arxiuPendentTipusEnum,
				organActual);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<SeguimentConsultaPinbalDto> findConsultesPinbal(
			Long entitatId,
			SeguimentConsultaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findConsultesPinbal(
				entitatId,
				filtre,
				paginacioParams);
	}


}
