package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.InteressatResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class InteressatResourceServiceEjb extends AbstractServiceEjb<InteressatResourceService> implements InteressatResourceService {
	
	@Delegate private InteressatResourceService delegateService;
	
	protected void setDelegateService(InteressatResourceService delegateService) {
		this.delegateService = delegateService;
	}

}