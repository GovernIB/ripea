package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.*;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("isAuthenticated()")
public interface SeguimentService {

	@PreAuthorize("isAuthenticated()")
	public PaginaDto<SeguimentDto> findPortafirmesEnviaments(
			Long entitatId,
			SeguimentFiltreDto filtre, 
			PaginacioParamsDto paginacioParams,
			String rolActual);

	@PreAuthorize("isAuthenticated()")
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

	@PreAuthorize("isAuthenticated()")
	public ResultDto<SeguimentArxiuPendentsDto> findPendentsArxiu(
			Long entitatId,
			SeguimentArxiuPendentsFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual,
			ResultEnumDto resultEnum,
			ArxiuPendentTipusEnumDto arxiuPendentTipusEnum,
			Long organActual);

	@PreAuthorize("isAuthenticated()")
	public PaginaDto<SeguimentConsultaPinbalDto> findConsultesPinbal(
			Long entitatId,
			SeguimentConsultaFiltreDto filtre,
			PaginacioParamsDto paginacioParams);


}
