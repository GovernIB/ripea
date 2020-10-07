package es.caib.ripea.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.exception.NotFoundException;


public interface ExpedientEstatService {

	PaginaDto<ExpedientEstatDto> findExpedientEstatByMetaExpedientPaginat(Long entitatId, Long metaExpedientId,
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('tothom')")
	ExpedientEstatDto findExpedientEstatById(Long entitatId, Long id);

	@PreAuthorize("hasRole('tothom')")
	ExpedientEstatDto createExpedientEstat(Long entitatId, ExpedientEstatDto estat);
	
	@PreAuthorize("hasRole('tothom')")
	ExpedientEstatDto updateExpedientEstat(Long entitatId, ExpedientEstatDto estat);

	@PreAuthorize("hasRole('tothom')")
	ExpedientEstatDto moveTo(Long entitatId, Long metaExpedientId, Long expedientEstatId, int posicio)
			throws NotFoundException;
	
	@PreAuthorize("hasRole('tothom')")
	ExpedientEstatDto deleteExpedientEstat(Long entitatId, Long expedientEstatId) throws NotFoundException;

	@PreAuthorize("hasRole('tothom')")
	List<ExpedientEstatDto> findExpedientEstats(Long entitatId, Long expedientId);

	@PreAuthorize("hasRole('tothom')")
	ExpedientDto changeEstatOfExpedient(Long entitatId, Long expedientId, Long expedientEstatId);

	@PreAuthorize("hasRole('tothom')")
	List<ExpedientEstatDto> findExpedientEstatsByMetaExpedient(Long entitatId, Long metaExpedientId);

	@PreAuthorize("hasRole('tothom')")
	PaginaDto<ExpedientDto> findExpedientsPerCanviEstatMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException;

	@PreAuthorize("hasRole('tothom')")
	List<Long> findIdsExpedientsPerCanviEstatMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre) throws NotFoundException;

}
