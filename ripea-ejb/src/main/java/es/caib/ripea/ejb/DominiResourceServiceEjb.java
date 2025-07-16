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
import es.caib.ripea.service.intf.resourceservice.DominiResourceService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class DominiResourceServiceEjb extends AbstractServiceEjb<DominiResourceService> implements DominiResourceService {

	@Delegate private DominiResourceService delegateService;

	protected void setDelegateService(DominiResourceService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	public <P extends Serializable> Serializable artifactActionExec(Long id, String code, P params)
			throws ArtifactNotFoundException, ActionExecutionException {
		return artifactActionExec(id, code, params);
	}

	@Override
	public <P extends Serializable> Map<String, Object> artifactOnChange(ResourceArtifactType type, String code,
			Long id, P previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers)
			throws ArtifactNotFoundException, ResourceFieldNotFoundException, AnswerRequiredException {
		return artifactOnChange(type, code, id, previous, fieldName, fieldValue, answers);
	}

	@Override
	public <P extends Serializable> List<?> artifactReportGenerateData(Long id, String code, P params)
			throws ArtifactNotFoundException, ReportGenerationException {
		return artifactReportGenerateData(id, code, params);
	}
}
