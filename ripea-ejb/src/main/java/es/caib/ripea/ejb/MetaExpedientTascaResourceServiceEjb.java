package es.caib.ripea.ejb;

import java.io.Serializable;
import java.util.List;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.base.exception.ArtifactNotFoundException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientTascaResourceService;
import lombok.experimental.Delegate;

public class MetaExpedientTascaResourceServiceEjb extends AbstractServiceEjb<MetaExpedientTascaResourceService> implements MetaExpedientTascaResourceService {

	@Delegate private MetaExpedientTascaResourceService delegateService;

	protected void setDelegateService(MetaExpedientTascaResourceService delegateService) {
		this.delegateService = delegateService;
	}
	
	@Override
	public <P extends Serializable> List<?> reportGenerate(String code, P params) throws ArtifactNotFoundException, ReportGenerationException {
		return delegateService.reportGenerate(code, params);
	}
}