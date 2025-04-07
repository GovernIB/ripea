package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.OrganGestorResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class OrganGestorResourceServiceEjb extends AbstractServiceEjb<OrganGestorResourceService> implements OrganGestorResourceService {

	@Delegate private OrganGestorResourceService delegateService;
	
	protected void setDelegateService(OrganGestorResourceService delegateService) {
		this.delegateService = delegateService;
	}

}