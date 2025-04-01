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
import es.caib.ripea.service.intf.resourceservice.RegistreInteressatResourceService;
import lombok.experimental.Delegate;

@Stateless
public class RegistreInteressatResourceServiceEjb extends AbstractServiceEjb<RegistreInteressatResourceService> implements RegistreInteressatResourceService {

	@Delegate private RegistreInteressatResourceService delegateService;
	
	protected void setDelegateService(RegistreInteressatResourceService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public <P extends Serializable> Serializable artifactActionExec(Long id, String code, P params)
			throws ArtifactNotFoundException, ActionExecutionException {
		return delegateService.artifactActionExec(id, code, params);
	}

	@Override
	@RolesAllowed("**")
	public <P extends Serializable> Map<String, Object> artifactOnChange(ResourceArtifactType type, String code,
			P previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers)
			throws ArtifactNotFoundException, ResourceFieldNotFoundException, AnswerRequiredException {
		return delegateService.artifactOnChange(type, code, previous, fieldName, fieldValue, answers);
	}

	@Override
	@RolesAllowed("**")
	public <P extends Serializable> List<?> artifactReportGenerate(Long id, String code, P params)
			throws ArtifactNotFoundException, ReportGenerationException {
		return delegateService.artifactReportGenerate(id, code, params);
	}
}