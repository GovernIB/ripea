package es.caib.ripea.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.ContingutMovimentResourceService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class ContingutMovimentResourceServiceEjb extends AbstractServiceEjb<ContingutMovimentResourceService> implements ContingutMovimentResourceService {

	@Delegate private ContingutMovimentResourceService delegateService;
	
	protected void setDelegateService(ContingutMovimentResourceService delegateService) {
		this.delegateService = delegateService;
	}

}