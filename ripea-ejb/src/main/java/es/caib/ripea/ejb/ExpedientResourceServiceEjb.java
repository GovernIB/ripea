package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.ExpedientResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class ExpedientResourceServiceEjb extends AbstractServiceEjb<ExpedientResourceService> implements ExpedientResourceService {

	@Delegate private ExpedientResourceService delegateService;

	protected void setDelegateService(ExpedientResourceService delegateService) {
		this.delegateService = delegateService;
	}

}