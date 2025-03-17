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
@PreAuthorize("isAuthenticated()")
public interface AvisService {

	AvisDto create(AvisDto avis);

	AvisDto update(AvisDto avis);

	AvisDto updateActiva(Long id, boolean activa);

	AvisDto delete(Long id);

	AvisDto findById(Long id);

	PaginaDto<AvisDto> findPaginat(PaginacioParamsDto paginacioParams);

	List<AvisDto> findActive();

	List<AvisDto> findActiveAdmin(Long entitatId);
}