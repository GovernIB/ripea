package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.MetaDocumentResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class MetaDocumentResourceServiceEjb extends AbstractServiceEjb<MetaDocumentResourceService> implements MetaDocumentResourceService {

	@Delegate private MetaDocumentResourceService delegateService;

	protected void setDelegateService(MetaDocumentResourceService delegateService) {
		this.delegateService = delegateService;
	}
	
}