package es.caib.ripea.ejb;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;

import es.caib.ripea.service.intf.base.exception.ArtifactNotFoundException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.resourceservice.OrganGestorResourceService;
import lombok.experimental.Delegate;

@Stateless
public class OrganGestorResourceServiceEjb implements OrganGestorResourceService {

	@Delegate private OrganGestorResourceService delegateService;
	
	protected void delegate(OrganGestorResourceService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	public <P extends Serializable> List<?> reportGenerate(String code, P params) throws ArtifactNotFoundException, ReportGenerationException {
		return delegateService.reportGenerate(code, params);
	}
}
