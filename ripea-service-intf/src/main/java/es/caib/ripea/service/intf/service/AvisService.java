/**
 * 
 */
package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.AvisDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per a la gestió d'avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AvisService {

	@PreAuthorize("isAuthenticated()")
	AvisDto create(AvisDto avis);

	@PreAuthorize("isAuthenticated()")
	AvisDto update(AvisDto avis);

	@PreAuthorize("isAuthenticated()")
	AvisDto updateActiva(Long id, boolean activa);

	@PreAuthorize("isAuthenticated()")
	AvisDto delete(Long id);

	@PreAuthorize("isAuthenticated()")
	AvisDto findById(Long id);

	@PreAuthorize("isAuthenticated()")
	PaginaDto<AvisDto> findPaginat(PaginacioParamsDto paginacioParams);

	@PreAuthorize("isAuthenticated()")
	List<AvisDto> findActive();

	@PreAuthorize("isAuthenticated()")
	List<AvisDto> findActiveAdmin(Long entitatId);


}
