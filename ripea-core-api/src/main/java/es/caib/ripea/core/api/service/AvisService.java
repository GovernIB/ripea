/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.AvisDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;

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


}
