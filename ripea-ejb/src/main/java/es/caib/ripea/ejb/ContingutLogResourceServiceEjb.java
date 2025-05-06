package es.caib.ripea.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.ContingutLogResourceService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class ContingutLogResourceServiceEjb extends AbstractServiceEjb<ContingutLogResourceService> implements ContingutLogResourceService {

	@Delegate private ContingutLogResourceService delegateService;
	
	protected void setDelegateService(ContingutLogResourceService delegateService) {
		this.delegateService = delegateService;
	}

}