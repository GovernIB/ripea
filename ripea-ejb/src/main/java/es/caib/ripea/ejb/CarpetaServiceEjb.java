/**
 * 
 */
package es.caib.ripea.ejb;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.ArbreDto;
import es.caib.ripea.service.intf.dto.CarpetaDto;
import es.caib.ripea.service.intf.dto.ExpedientCarpetaArbreDto;
import es.caib.ripea.service.intf.dto.ExpedientDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.CarpetaService;
import lombok.experimental.Delegate;

@Stateless
public class CarpetaServiceEjb extends AbstractServiceEjb<CarpetaService> implements CarpetaService {

	@Delegate private CarpetaService delegateService;

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
