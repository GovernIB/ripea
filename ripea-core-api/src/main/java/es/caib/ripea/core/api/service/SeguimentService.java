package es.caib.ripea.core.api.service;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.ArxiuPendentTipusEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioListDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.ResultDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.dto.SeguimentArxiuPendentsDto;
import es.caib.ripea.core.api.dto.SeguimentArxiuPendentsFiltreDto;
import es.caib.ripea.core.api.dto.SeguimentDto;
import es.caib.ripea.core.api.dto.SeguimentFiltreDto;


public interface SeguimentService {


	@PreAuthorize("hasRole('IPA_ADMIN')")
	public PaginaDto<SeguimentDto> findPortafirmesEnviaments(
			Long entitatId,
			SeguimentFiltreDto filtre, 
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('IPA_ADMIN')")
	public PaginaDto<SeguimentDto> findNotificacionsEnviaments(
			Long entitatId,
			SeguimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams);

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
			ArxiuPendentTipusEnumDto arxiuPendentTipusEnum);


}
