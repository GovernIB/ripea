package es.caib.ripea.ejb;

import javax.ejb.Stateless;

import es.caib.ripea.service.intf.resourceservice.ExpedientResourceService;
import lombok.experimental.Delegate;

@Stateless
public class ExpedientResourceServiceEjb implements ExpedientResourceService {

	@Delegate private ExpedientResourceService delegateService;
	
	protected void delegate(ExpedientResourceService delegateService) {
		this.delegateService = delegateService;
	}
}
