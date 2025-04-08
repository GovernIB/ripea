package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.ExpedientTascaResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class ExpedientTascaResourceServiceEjb extends AbstractServiceEjb<ExpedientTascaResourceService> implements ExpedientTascaResourceService {

	@Delegate private ExpedientTascaResourceService delegateService;

	protected void setDelegateService(ExpedientTascaResourceService delegateService) {
		this.delegateService = delegateService;
	}

}