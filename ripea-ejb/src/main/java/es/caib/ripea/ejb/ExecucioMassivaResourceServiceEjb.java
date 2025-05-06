package es.caib.ripea.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.ExecucioMassivaResourceService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class ExecucioMassivaResourceServiceEjb extends AbstractServiceEjb<ExecucioMassivaResourceService> implements ExecucioMassivaResourceService {

	@Delegate private ExecucioMassivaResourceService delegateService;
	
	protected void setDelegateService(ExecucioMassivaResourceService delegateService) {
		this.delegateService = delegateService;
	}

}