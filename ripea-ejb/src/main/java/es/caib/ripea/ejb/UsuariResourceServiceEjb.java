package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.UsuariResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class UsuariResourceServiceEjb extends AbstractServiceEjb<UsuariResourceService> implements UsuariResourceService {

	@Delegate private UsuariResourceService delegateService;

	protected void setDelegateService(UsuariResourceService delegateService) {
		this.delegateService = delegateService;
	}

}