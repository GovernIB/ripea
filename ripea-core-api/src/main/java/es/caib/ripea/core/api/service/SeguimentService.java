package es.caib.ripea.core.api.service;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.ExpedientPeticioDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
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
	public PaginaDto<ExpedientPeticioDto> findExpedientsPendents(Long entitatId,
			ExpedientPeticioFiltreDto filtre,
			PaginacioParamsDto paginacioParams);


	

}