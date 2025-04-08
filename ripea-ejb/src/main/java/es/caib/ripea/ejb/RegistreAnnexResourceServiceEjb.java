package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.RegistreAnnexResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class RegistreAnnexResourceServiceEjb extends AbstractServiceEjb<RegistreAnnexResourceService> implements RegistreAnnexResourceService {

	@Delegate private RegistreAnnexResourceService delegateService;
	
	protected void setDelegateService(RegistreAnnexResourceService delegateService) {
		this.delegateService = delegateService;
	}
	
}