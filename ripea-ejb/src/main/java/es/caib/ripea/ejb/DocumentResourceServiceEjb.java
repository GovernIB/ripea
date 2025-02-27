package es.caib.ripea.ejb;

import javax.ejb.Stateless;

import es.caib.ripea.service.intf.resourceservice.DocumentResourceService;
import lombok.experimental.Delegate;

@Stateless
public class DocumentResourceServiceEjb implements DocumentResourceService {

	@Delegate private DocumentResourceService delegateService;
	
	protected void delegate(DocumentResourceService delegateService) {
		this.delegateService = delegateService;
	}
}
