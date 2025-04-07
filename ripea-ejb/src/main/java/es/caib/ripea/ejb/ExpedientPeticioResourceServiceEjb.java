package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.ExpedientPeticioResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class ExpedientPeticioResourceServiceEjb extends AbstractServiceEjb<ExpedientPeticioResourceService> implements ExpedientPeticioResourceService {

	@Delegate private ExpedientPeticioResourceService delegateService;

	protected void setDelegateService(ExpedientPeticioResourceService delegateService) {
		this.delegateService = delegateService;
	}
	
}