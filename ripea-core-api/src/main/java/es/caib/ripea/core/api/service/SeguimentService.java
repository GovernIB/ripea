package es.caib.ripea.core.api.service;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.ExpedientPeticioDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.SeguimentArxiuPendentsDto;
import es.caib.ripea.core.api.dto.SeguimentArxiuPendentsFiltreDto;
import es.caib.ripea.core.api.dto.SeguimentDto;
import es.caib.ripea.core.api.dto.SeguimentFiltreDto;

import java.util.List;


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
	public PaginaDto<ExpedientPeticioDto> findExpedientsPendents(
			Long entitatId,
			ExpedientPeticioFiltreDto filtre,
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('IPA_SUPER')")
	public PaginaDto<SeguimentArxiuPendentsDto> findArxiuPendentsExpedients(
			Long entitatId,
			SeguimentArxiuPendentsFiltreDto filtre,
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('IPA_SUPER')")
	public List<Long> findArxiuPendentsExpedients(
			Long entitatId,
			SeguimentArxiuPendentsFiltreDto filtre);

	@PreAuthorize("hasRole('IPA_SUPER')")
	public PaginaDto<SeguimentArxiuPendentsDto> findArxiuPendentsDocuments(
			Long entitatId,
			SeguimentArxiuPendentsFiltreDto filtre,
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('IPA_SUPER')")
	public List<Long> findArxiuPendentsDocuments(
			Long entitatId,
			SeguimentArxiuPendentsFiltreDto filtre);

	@PreAuthorize("hasRole('IPA_SUPER')")
	public PaginaDto<SeguimentArxiuPendentsDto> findArxiuPendentsInteressats(
			Long entitatId,
			SeguimentArxiuPendentsFiltreDto filtre,
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('IPA_SUPER')")
	public List<Long> findArxiuPendentsInteressats(
			Long entitatId,
			SeguimentArxiuPendentsFiltreDto filtre);

}
