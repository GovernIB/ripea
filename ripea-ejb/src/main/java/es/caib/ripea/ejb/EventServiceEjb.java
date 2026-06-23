package es.caib.ripea.ejb;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.ValidacioErrorDto;
import es.caib.ripea.service.intf.model.sse.AvisosActiusEvent;
import es.caib.ripea.service.intf.service.EventService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class EventServiceEjb extends AbstractServiceEjb<EventService> implements EventService {

	@Delegate private EventService delegateService;

	protected void setDelegateService(EventService delegateService) {
		this.delegateService = delegateService;
	}
	
	@PermitAll
	public List<ValidacioErrorDto> getValidacionsInicialsExpedient(Long expedientId) {
		return delegateService.getValidacionsInicialsExpedient(expedientId);
	}

	// @PermitAll perquè es crida des d'un fil de @JmsListener (handleEventAvisos) sense petició web ni caller autenticat.
	@PermitAll
	public AvisosActiusEvent getAvisosActiusPerUsuariCodi(String usuariCodi) {
		return delegateService.getAvisosActiusPerUsuariCodi(usuariCodi);
	}
}
