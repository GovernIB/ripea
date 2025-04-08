package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.DocumentResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class DocumentResourceServiceEjb extends AbstractServiceEjb<DocumentResourceService> implements DocumentResourceService {

	@Delegate private DocumentResourceService delegateService;
	
	protected void setDelegateService(DocumentResourceService delegateService) {
		this.delegateService = delegateService;
	}

}
