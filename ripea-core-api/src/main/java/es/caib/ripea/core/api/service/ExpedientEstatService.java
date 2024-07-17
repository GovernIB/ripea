package es.caib.ripea.core.api.service;

import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.ResultDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;


public interface ExpedientEstatService {

	PaginaDto<ExpedientEstatDto> findExpedientEstatByMetaExpedientPaginat(Long entitatId, Long metaExpedientId,
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('tothom')")
	ExpedientEstatDto findExpedientEstatById(Long entitatId, Long id);

	@PreAuthorize("hasRole('tothom')")
	ExpedientEstatDto createExpedientEstat(Long entitatId, ExpedientEstatDto estat, String rolActual, Long organId);
	
	@PreAuthorize("hasRole('tothom')")
	ExpedientEstatDto updateExpedientEstat(Long entitatId, ExpedientEstatDto estat, String rolActual, Long organId);

	@PreAuthorize("hasRole('tothom')")
	ExpedientEstatDto moveTo(Long entitatId, Long metaExpedientId, Long expedientEstatId, int posicio, String rolActual)
			throws NotFoundException;
	
	@PreAuthorize("hasRole('tothom')")
	ExpedientEstatDto deleteExpedientEstat(Long entitatId, Long expedientEstatId, String rolActual, Long organId) throws NotFoundException;

	@PreAuthorize("hasRole('tothom')")
	List<ExpedientEstatDto> findExpedientEstats(Long entitatId, Long expedientId, String rolActual);

	@PreAuthorize("hasRole('tothom')")
	ExpedientDto changeExpedientEstat(
			Long entitatId,
			Long expedientId,
			Long expedientEstatId);

	@PreAuthorize("hasRole('tothom')")
	List<ExpedientEstatDto> findExpedientEstatsByMetaExpedient(Long entitatId, Long metaExpedientId);

	@PreAuthorize("hasRole('tothom')")
	ResultDto<ExpedientDto> findExpedientsPerCanviEstatMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			String rolActual, 
			ResultEnumDto resultEnum) throws NotFoundException;

}
