package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.*;
import org.springframework.security.access.prepost.PreAuthorize;


public interface SeguimentService {


	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('tothom')")
	public PaginaDto<SeguimentDto> findPortafirmesEnviaments(
			Long entitatId,
			SeguimentFiltreDto filtre, 
			PaginacioParamsDto paginacioParams,
			String rolActual);

	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('tothom')")
	public ResultDto<SeguimentDto> findNotificacionsEnviaments(
			Long entitatId,
			SeguimentNotificacionsFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			ResultEnumDto resultEnum, 
			String rolActual);

	@PreAuthorize("hasRole('IPA_ADMIN')")
	public PaginaDto<SeguimentDto> findTasques(
			Long entitatId,
			SeguimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('IPA_ADMIN')")
	public PaginaDto<ExpedientPeticioListDto> findAnotacionsPendents(
			Long entitatId,
			ExpedientPeticioFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual);

	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN') or hasRole('tothom')")
	public ResultDto<SeguimentArxiuPendentsDto> findPendentsArxiu(
			Long entitatId,
			SeguimentArxiuPendentsFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual,
			ResultEnumDto resultEnum,
			ArxiuPendentTipusEnumDto arxiuPendentTipusEnum,
			Long organActual);

	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN') or hasRole('tothom')")
	public PaginaDto<SeguimentConsultaPinbalDto> findConsultesPinbal(
			Long entitatId,
			SeguimentConsultaFiltreDto filtre,
			PaginacioParamsDto paginacioParams);


}
