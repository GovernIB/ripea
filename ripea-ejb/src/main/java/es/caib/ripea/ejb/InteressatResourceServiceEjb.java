package es.caib.ripea.ejb;

import javax.ejb.Stateless;

import es.caib.ripea.service.intf.resourceservice.InteressatResourceService;
import lombok.experimental.Delegate;

@Stateless
public class InteressatResourceServiceEjb implements InteressatResourceService {
	
	@Delegate private InteressatResourceService delegateService;
	
	protected void delegate(InteressatResourceService delegateService) {
		this.delegateService = delegateService;
	}
}
