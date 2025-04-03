package es.caib.ripea.ejb;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.ArtifactNotFoundException;
import es.caib.ripea.service.intf.base.exception.FieldArtifactNotFoundException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.base.exception.ResourceAlreadyExistsException;
import es.caib.ripea.service.intf.base.exception.ResourceFieldNotFoundException;
import es.caib.ripea.service.intf.base.exception.ResourceNotCreatedException;
import es.caib.ripea.service.intf.base.exception.ResourceNotDeletedException;
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.base.exception.ResourceNotUpdatedException;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.base.model.ResourceArtifact;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.model.ExpedientTascaComentariResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientTascaComentariResourceService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class ExpedientTascaComentariResourceServiceEjb extends AbstractServiceEjb<ExpedientTascaComentariResourceService> implements ExpedientTascaComentariResourceService {

	@Delegate private ExpedientTascaComentariResourceService delegateService;
	
	@Override
	public ExpedientTascaComentariResource newResourceInstance() {
		return delegateService.newResourceInstance();
	}

	@Override
	public ExpedientTascaComentariResource create(ExpedientTascaComentariResource resource,
			Map<String, AnswerValue> answers)
			throws ResourceAlreadyExistsException, ResourceNotCreatedException, AnswerRequiredException {
		return delegateService.create(resource, answers);
	}

	@Override
	public ExpedientTascaComentariResource update(Long id, ExpedientTascaComentariResource resource,
			Map<String, AnswerValue> answers)
			throws ResourceNotFoundException, ResourceNotUpdatedException, AnswerRequiredException {
		return delegateService.update(id, resource, answers);
	}

	@Override
	public void delete(Long id, Map<String, AnswerValue> answers)
			throws ResourceNotFoundException, ResourceNotDeletedException, AnswerRequiredException {
		delegateService.delete(id, answers);
	}

	@Override
	public Map<String, Object> onChange(ExpedientTascaComentariResource previous, String fieldName, Object fieldValue,
			Map<String, AnswerValue> answers) throws ResourceFieldNotFoundException, AnswerRequiredException {
		return delegateService.onChange(previous, fieldName, fieldValue, answers);
	}

	@Override
	public ExpedientTascaComentariResource getOne(Long id, String[] perspectives) throws ResourceNotFoundException {
		return delegateService.getOne(id, perspectives);
	}

	@Override
	public Page<ExpedientTascaComentariResource> findPage(String quickFilter, String filter, String[] namedQueries,
			String[] perspectives, Pageable pageable) {
		return delegateService.findPage(quickFilter, filter, namedQueries, perspectives, pageable);
	}

	@Override
	public DownloadableFile fieldDownload(Long id, String fieldName, OutputStream out) throws ResourceNotFoundException,
			ResourceFieldNotFoundException, FieldArtifactNotFoundException, IOException {
		return delegateService.fieldDownload(id, fieldName, out);
	}

	@Override
	public List<ResourceArtifact> artifactFindAll(ResourceArtifactType type) {
		return delegateService.artifactFindAll(type);
	}

	@Override
	public ResourceArtifact artifactGetOne(ResourceArtifactType type, String code) throws ArtifactNotFoundException {
		return delegateService.artifactGetOne(type, code);
	}

	@Override
	public <P extends Serializable> Map<String, Object> artifactOnChange(ResourceArtifactType type, String code,
			P previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers)
			throws ArtifactNotFoundException, ResourceFieldNotFoundException, AnswerRequiredException {
		return delegateService.artifactOnChange(type, code, previous, fieldName, fieldValue, answers);
	}

	@Override
	protected void setDelegateService(ExpedientTascaComentariResourceService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	public <P extends Serializable> Serializable artifactActionExec(Long id, String code, P params)
			throws ArtifactNotFoundException, ActionExecutionException {
		return delegateService.artifactActionExec(id, code, params);
	}

	@Override
	public <P extends Serializable> List<?> artifactReportGenerateData(Long id, String code, P params)
			throws ArtifactNotFoundException, ReportGenerationException {
		return delegateService.artifactReportGenerateData(id, code, params);
	}
}