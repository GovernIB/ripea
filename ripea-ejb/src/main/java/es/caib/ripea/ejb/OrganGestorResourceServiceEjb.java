package es.caib.ripea.ejb;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.ArtifactNotFoundException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.base.exception.ResourceFieldNotFoundException;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.resourceservice.OrganGestorResourceService;
import lombok.experimental.Delegate;

@Stateless
public class OrganGestorResourceServiceEjb extends AbstractServiceEjb<OrganGestorResourceService> implements OrganGestorResourceService {

	@Delegate private OrganGestorResourceService delegateService;
	
	protected void setDelegateService(OrganGestorResourceService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public <P extends Serializable> List<?> reportGenerate(String code, P params) throws ArtifactNotFoundException, ReportGenerationException {
		return delegateService.reportGenerate(code, params);
	}

	@Override
	@RolesAllowed("**")
	public <P extends Serializable> Serializable actionExec(String code, P params) throws ArtifactNotFoundException, ActionExecutionException {
		return delegateService.actionExec(code, params);
	}

	@Override
	@RolesAllowed("**")
	public <P extends Serializable> Map<String, Object> artifactOnChange(ResourceArtifactType type, String code,
			P previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers)
			throws ArtifactNotFoundException, ResourceFieldNotFoundException, AnswerRequiredException {
		return delegateService.artifactOnChange(type, code, previous, fieldName, fieldValue, answers);
	}
}
