package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientTascaResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class MetaExpedientTascaResourceServiceEjb extends AbstractServiceEjb<MetaExpedientTascaResourceService> implements MetaExpedientTascaResourceService {

	@Delegate private MetaExpedientTascaResourceService delegateService;

	protected void setDelegateService(MetaExpedientTascaResourceService delegateService) {
		this.delegateService = delegateService;
	}

}