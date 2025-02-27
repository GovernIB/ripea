package es.caib.ripea.ejb;

import javax.ejb.Stateless;

import es.caib.ripea.service.intf.resourceservice.OrganGestorResourceService;
import lombok.experimental.Delegate;

@Stateless
public class OrganGestorResourceServiceEjb implements OrganGestorResourceService {

	@Delegate private OrganGestorResourceService delegateService;
	
	protected void delegate(OrganGestorResourceService delegateService) {
		this.delegateService = delegateService;
	}
}
