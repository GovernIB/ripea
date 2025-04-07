package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.DadaResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class DadaResourceServiceEjb extends AbstractServiceEjb<DadaResourceService> implements DadaResourceService {

	@Delegate private DadaResourceService delegateService;
	
	protected void setDelegateService(DadaResourceService delegateService) {
		this.delegateService = delegateService;
	}

}