package es.caib.ripea.ejb;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.model.ExpedientEstatResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientEstatResourceService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class ExpedientEstatResourceServiceEjb extends AbstractServiceEjb<ExpedientEstatResourceService> implements ExpedientEstatResourceService {

	@Delegate private ExpedientEstatResourceService delegateService;

	protected void setDelegateService(ExpedientEstatResourceService delegateService) {
		this.delegateService = delegateService;
	}
}