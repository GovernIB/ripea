package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.GrupResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class GrupResourceServiceEjb extends AbstractServiceEjb<GrupResourceService> implements GrupResourceService {

	@Delegate private GrupResourceService delegateService;

	protected void setDelegateService(GrupResourceService delegateService) {
		this.delegateService = delegateService;
	}

}