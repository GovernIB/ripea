package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@PreAuthorize("isAuthenticated()")
public interface ExpedientEstatService {

	PaginaDto<ExpedientEstatDto> findExpedientEstatByMetaExpedientPaginat(Long entitatId, Long metaExpedientId,
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("isAuthenticated()")
	ExpedientEstatDto findExpedientEstatById(Long entitatId, Long id);

	@PreAuthorize("isAuthenticated()")
	ExpedientEstatDto createExpedientEstat(Long entitatId, ExpedientEstatDto estat, String rolActual, Long organId);
	
	@PreAuthorize("isAuthenticated()")
	ExpedientEstatDto updateExpedientEstat(Long entitatId, ExpedientEstatDto estat, String rolActual, Long organId);

	@PreAuthorize("isAuthenticated()")
	ExpedientEstatDto moveTo(Long entitatId, Long metaExpedientId, Long expedientEstatId, int posicio, String rolActual)
			throws NotFoundException;
	
	@PreAuthorize("isAuthenticated()")
	ExpedientEstatDto deleteExpedientEstat(Long entitatId, Long expedientEstatId, String rolActual, Long organId) throws NotFoundException;

	@PreAuthorize("isAuthenticated()")
	List<ExpedientEstatDto> findExpedientEstats(Long entitatId, Long expedientId, String rolActual);

	@PreAuthorize("isAuthenticated()")
	ExpedientDto changeExpedientEstat(
			Long entitatId,
			Long expedientId,
			Long expedientEstatId);

	@PreAuthorize("isAuthenticated()")
	List<ExpedientEstatDto> findExpedientEstatsByMetaExpedient(Long entitatId, Long metaExpedientId);

	@PreAuthorize("isAuthenticated()")
	ResultDto<ExpedientDto> findExpedientsPerCanviEstatMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			String rolActual, 
			ResultEnumDto resultEnum) throws NotFoundException;

}
