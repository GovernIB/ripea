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

	@PreAuthorize("hasRole('tothom')")
	AvisDto create(AvisDto avis);

	@PreAuthorize("hasRole('tothom')")
	AvisDto update(AvisDto avis);

	@PreAuthorize("hasRole('tothom')")
	AvisDto updateActiva(Long id, boolean activa);

	@PreAuthorize("hasRole('tothom')")
	AvisDto delete(Long id);

	@PreAuthorize("hasRole('tothom')")
	AvisDto findById(Long id);

	@PreAuthorize("hasRole('tothom')")
	PaginaDto<AvisDto> findPaginat(PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('tothom')")
	List<AvisDto> findActive();

	@PreAuthorize("hasRole('tothom')")
	List<AvisDto> findActiveAdmin(Long entitatId);


}
