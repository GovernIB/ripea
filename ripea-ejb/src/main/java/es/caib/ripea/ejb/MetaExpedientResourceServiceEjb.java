package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class MetaExpedientResourceServiceEjb extends AbstractServiceEjb<MetaExpedientResourceService> implements MetaExpedientResourceService {

	@Delegate private MetaExpedientResourceService delegateService;

	protected void setDelegateService(MetaExpedientResourceService delegateService) {
		this.delegateService = delegateService;
	}

}