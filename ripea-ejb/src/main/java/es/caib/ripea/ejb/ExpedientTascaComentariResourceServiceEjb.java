package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.ExpedientTascaComentariResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class ExpedientTascaComentariResourceServiceEjb extends AbstractServiceEjb<ExpedientTascaComentariResourceService> implements ExpedientTascaComentariResourceService {

	@Delegate private ExpedientTascaComentariResourceService delegateService;

	protected void setDelegateService(ExpedientTascaComentariResourceService delegateService) {
		this.delegateService = delegateService;
	}

}