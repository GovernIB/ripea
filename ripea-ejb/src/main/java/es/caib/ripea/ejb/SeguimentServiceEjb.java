package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.service.SeguimentService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * Implementació de SeguimentService com a EJB que empra una clase delegada
 * per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public  class SeguimentServiceEjb implements SeguimentService {

	@Delegate
	private SeguimentService delegateService;

	protected void delegate(SeguimentService delegateService) {
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
	@RolesAllowed({ "IPA_ADMIN", "IPA_ORGAN_ADMIN", "tothom" })
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
	@RolesAllowed({ "IPA_ADMIN", "IPA_ORGAN_ADMIN", "tothom" })
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
