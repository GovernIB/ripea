package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.ExpedientSeguidorResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class ExpedientSeguidorResourceServiceEjb extends AbstractServiceEjb<ExpedientSeguidorResourceService> implements ExpedientSeguidorResourceService {

	@Delegate private ExpedientSeguidorResourceService delegateService;

	protected void setDelegateService(ExpedientSeguidorResourceService delegateService) {
		this.delegateService = delegateService;
	}
	
}