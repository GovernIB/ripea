package es.caib.ripea.ejb;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
import es.caib.ripea.service.intf.model.ExpedientComentariResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientComentariResourceService;
import lombok.experimental.Delegate;

@Stateless
public class ExpedientComentariResourceServiceEjb extends AbstractServiceEjb<ExpedientComentariResourceService> implements ExpedientComentariResourceService {

	@Delegate private ExpedientComentariResourceService delegateService;

	protected void setDelegateService(ExpedientComentariResourceService delegateService) {
		this.delegateService = delegateService;
	}
	
	@Override
	public ExpedientComentariResource newResourceInstance() {
		return newResourceInstance();
	}

	@Override
	public ExpedientComentariResource create(ExpedientComentariResource resource, Map<String, AnswerValue> answers)
			throws ResourceAlreadyExistsException, ResourceNotCreatedException, AnswerRequiredException {
		return create(resource, answers);
	}

	@Override
	public ExpedientComentariResource update(Long id, ExpedientComentariResource resource,
			Map<String, AnswerValue> answers)
			throws ResourceNotFoundException, ResourceNotUpdatedException, AnswerRequiredException {
		return update(id, resource, answers);
	}

	@Override
	public void delete(Long id, Map<String, AnswerValue> answers)
			throws ResourceNotFoundException, ResourceNotDeletedException, AnswerRequiredException {
		delete(id, answers);
	}

	@Override
	public Map<String, Object> onChange(ExpedientComentariResource previous, String fieldName, Object fieldValue,
			Map<String, AnswerValue> answers) throws AnswerRequiredException {
		return onChange(previous, fieldName, fieldValue, answers);
	}

	@Override
	public <P extends Serializable> Serializable actionExec(String code, P params)
			throws ArtifactNotFoundException, ActionExecutionException {
		return actionExec(code, params);
	}

	@Override
	public ExpedientComentariResource getOne(Long id, String[] perspectives) throws ResourceNotFoundException {
		return getOne(id, perspectives);
	}

	@Override
	public Page<ExpedientComentariResource> findPage(String quickFilter, String filter, String[] namedQueries,
			String[] perspectives, Pageable pageable) {
		return findPage(quickFilter, filter, namedQueries, perspectives, pageable);
	}

	@Override
	public DownloadableFile fieldDownload(Long id, String fieldName, OutputStream out) throws ResourceNotFoundException,
			ResourceFieldNotFoundException, FieldArtifactNotFoundException, IOException {
		return fieldDownload(id, fieldName, out);
	}

	@Override
	public List<ResourceArtifact> artifactFindAll(ResourceArtifactType type) {
		return artifactFindAll(type);
	}

	@Override
	public ResourceArtifact artifactGetOne(ResourceArtifactType type, String code) throws ArtifactNotFoundException {
		return artifactGetOne(type, code);
	}

	@Override
	public <P extends Serializable> List<?> reportGenerate(String code, P params)
			throws ArtifactNotFoundException, ReportGenerationException {
		return reportGenerate(code, params);
	}
}