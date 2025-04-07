package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.MetaDadaResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class MetaDadaResourceServiceEjb  extends AbstractServiceEjb<MetaDadaResourceService> implements MetaDadaResourceService {

	@Delegate private MetaDadaResourceService delegateService;
	
	protected void setDelegateService(MetaDadaResourceService delegateService) {
		this.delegateService = delegateService;
	}

}