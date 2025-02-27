/**
 * 
 */
package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.CarpetaService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Implementació de ContenidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class CarpetaServiceEjb implements CarpetaService {

	@Delegate
	private CarpetaService delegateService;

	protected void setDelegateService(CarpetaService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public CarpetaDto create(
			Long entitatId,
			Long contenidorId,
			String nom) {
		return delegateService.create(
				entitatId,
				contenidorId,
				nom);
	}

	@Override
	@RolesAllowed("**")
	public void update(
			Long entitatId,
			Long id,
			String nom) {
		delegateService.update(
				entitatId,
				id,
				nom);
	}

	@Override
	@RolesAllowed("**")
	public CarpetaDto findById(
			Long entitatId,
			Long id) {
		return delegateService.findById(entitatId, id);
	}

	@Override
	@RolesAllowed("**")
	public List<CarpetaDto> findByEntitatAndExpedient(Long entitatId, Long expedientId) throws NotFoundException {
		return delegateService.findByEntitatAndExpedient(entitatId, expedientId);
	}

	@Override
	@RolesAllowed("**")
	public List<ArbreDto<ExpedientCarpetaArbreDto>> findArbreCarpetesExpedient(Long entitatId, List<ExpedientDto> expedients, Long expedientId, String rolActual) {
		return delegateService.findArbreCarpetesExpedient(entitatId, expedients, expedientId, rolActual);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto exportIndexCarpetes(Long entitatId, Set<Long> carpetaIds, String format) throws IOException {
		return delegateService.exportIndexCarpetes(entitatId, carpetaIds, format);
	}
	
}
