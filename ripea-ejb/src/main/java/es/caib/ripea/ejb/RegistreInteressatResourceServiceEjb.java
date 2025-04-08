package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.RegistreInteressatResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class RegistreInteressatResourceServiceEjb extends AbstractServiceEjb<RegistreInteressatResourceService> implements RegistreInteressatResourceService {

	@Delegate private RegistreInteressatResourceService delegateService;
	
	protected void setDelegateService(RegistreInteressatResourceService delegateService) {
		this.delegateService = delegateService;
	}

}