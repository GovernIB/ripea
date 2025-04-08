package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.ExpedientComentariResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class ExpedientComentariResourceServiceEjb extends AbstractServiceEjb<ExpedientComentariResourceService> implements ExpedientComentariResourceService {

	@Delegate private ExpedientComentariResourceService delegateService;

	protected void setDelegateService(ExpedientComentariResourceService delegateService) {
		this.delegateService = delegateService;
	}

}