/**
 * 
 */
package es.caib.ripea.ejb;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.ElementTipusEnumDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaContingutDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.intf.service.ExecucioMassivaService;
import lombok.experimental.Delegate;

@Stateless
public class ExecucioMassivaServiceEjb extends AbstractServiceEjb<ExecucioMassivaService> implements ExecucioMassivaService {

	@Delegate private ExecucioMassivaService delegateService;

	protected void setDelegateService(ExecucioMassivaService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public void crearExecucioMassiva(Long entitatId, ExecucioMassivaDto dto) throws NotFoundException, ValidationException {
		delegateService.crearExecucioMassiva(entitatId, dto);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto descarregarDocumentExecMassiva(Long entitatId, Long execMassivaId) {
		return delegateService.descarregarDocumentExecMassiva(entitatId, execMassivaId);
	}
	
	@Override
	@RolesAllowed("**")
	public List<ExecucioMassivaDto> findExecucionsMassivesPerUsuari(Long entitatId, UsuariDto usuari, int pagina) throws NotFoundException {
		return delegateService.findExecucionsMassivesPerUsuari(entitatId, usuari, pagina);
	}

	@Override
	@RolesAllowed("**")
	public List<ExecucioMassivaDto> findExecucionsMassivesGlobals() throws NotFoundException {
		return delegateService.findExecucionsMassivesGlobals();
	}

	@Override
	@RolesAllowed("**")
	public List<ExecucioMassivaContingutDto> findContingutPerExecucioMassiva(Long exm_id) throws NotFoundException {
		return delegateService.findContingutPerExecucioMassiva(exm_id);
	}

	@Override
	@PermitAll
	public void executeNextMassiveScheduledTask() {
		delegateService.executeNextMassiveScheduledTask();
	}

	@Override
	@RolesAllowed("**")
	public void saveExecucioMassiva(
			Long entitatId,
			ExecucioMassivaDto dto,
			List<ExecucioMassivaContingutDto> exc,
			ElementTipusEnumDto elementTipus) throws NotFoundException, ValidationException {
		delegateService.saveExecucioMassiva(
				entitatId,
				dto,
				exc,
				elementTipus);
	}

}
