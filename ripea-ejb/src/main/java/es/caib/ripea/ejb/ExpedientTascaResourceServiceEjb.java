package es.caib.ripea.ejb;

import java.io.Serializable;
import java.util.List;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.ArtifactNotFoundException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.resourceservice.ExpedientTascaResourceService;
import lombok.experimental.Delegate;

public class ExpedientTascaResourceServiceEjb extends AbstractServiceEjb<ExpedientTascaResourceService> implements ExpedientTascaResourceService {

	@Delegate private ExpedientTascaResourceService delegateService;

	protected void setDelegateService(ExpedientTascaResourceService delegateService) {
		this.delegateService = delegateService;
	}
	
	@Override
	public <P extends Serializable> List<?> reportGenerate(String code, P params) throws ArtifactNotFoundException, ReportGenerationException {
		return delegateService.reportGenerate(code, params);
	}

	@Override
	public <P extends Serializable> Serializable actionExec(String code, P params) throws ArtifactNotFoundException, ActionExecutionException {
		return delegateService.actionExec(code, params);
	}
}