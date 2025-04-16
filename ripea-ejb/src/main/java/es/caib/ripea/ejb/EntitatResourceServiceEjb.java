package es.caib.ripea.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.EntitatResourceService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class EntitatResourceServiceEjb extends AbstractServiceEjb<EntitatResourceService> implements EntitatResourceService{

	@Delegate private EntitatResourceService delegateService;
	
	protected void setDelegateService(EntitatResourceService delegateService) {
		this.delegateService = delegateService;
	}
}
