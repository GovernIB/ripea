package es.caib.ripea.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.ExecucioMassivaContingutResourceService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class ExecucioMassivaContingutResourceServiceEjb extends AbstractServiceEjb<ExecucioMassivaContingutResourceService> implements ExecucioMassivaContingutResourceService {

	@Delegate private ExecucioMassivaContingutResourceService delegateService;
	
	protected void setDelegateService(ExecucioMassivaContingutResourceService delegateService) {
		this.delegateService = delegateService;
	}

}