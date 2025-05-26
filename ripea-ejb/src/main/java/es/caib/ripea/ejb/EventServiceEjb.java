package es.caib.ripea.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.service.EventService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class EventServiceEjb extends AbstractServiceEjb<EventService> implements EventService {

	@Delegate private EventService delegateService;

	protected void setDelegateService(EventService delegateService) {
		this.delegateService = delegateService;
	}
}
