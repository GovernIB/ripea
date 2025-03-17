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
import es.caib.ripea.service.intf.model.MetaDocumentResource;
import es.caib.ripea.service.intf.resourceservice.MetaDocumentResourceService;
import lombok.experimental.Delegate;

@Stateless
public class MetaDocumentResourceServiceEjb extends AbstractServiceEjb<MetaDocumentResourceService> implements MetaDocumentResourceService {

	@Delegate private MetaDocumentResourceService delegateService;

	protected void setDelegateService(MetaDocumentResourceService delegateService) {
		this.delegateService = delegateService;
	}
	
	@Override
	@RolesAllowed("**")
	public MetaDocumentResource newResourceInstance() {
		return newResourceInstance();
	}

	@Override
	@RolesAllowed("**")
	public MetaDocumentResource create(MetaDocumentResource resource, Map<String, AnswerValue> answers)
			throws ResourceAlreadyExistsException, ResourceNotCreatedException, AnswerRequiredException {
		return create(resource, answers);
	}

	@Override
	@RolesAllowed("**")
	public MetaDocumentResource update(Long id, MetaDocumentResource resource, Map<String, AnswerValue> answers)
			throws ResourceNotFoundException, ResourceNotUpdatedException, AnswerRequiredException {
		return update(id, resource, answers);
	}

	@Override
	@RolesAllowed("**")
	public void delete(Long id, Map<String, AnswerValue> answers)
			throws ResourceNotFoundException, ResourceNotDeletedException, AnswerRequiredException {
		delete(id, answers);
	}

	@Override
	@RolesAllowed("**")
	public Map<String, Object> onChange(MetaDocumentResource previous, String fieldName, Object fieldValue,
			Map<String, AnswerValue> answers) throws AnswerRequiredException {
		return onChange(previous, fieldName, fieldValue, answers);
	}

	@Override
	@RolesAllowed("**")
	public <P extends Serializable> Serializable actionExec(String code, P params)
			throws ArtifactNotFoundException, ActionExecutionException {
		return actionExec(code, params);
	}

	@Override
	@RolesAllowed("**")
	public MetaDocumentResource getOne(Long id, String[] perspectives) throws ResourceNotFoundException {
		return getOne(id, perspectives);
	}

	@Override
	@RolesAllowed("**")
	public Page<MetaDocumentResource> findPage(String quickFilter, String filter, String[] namedQueries,
			String[] perspectives, Pageable pageable) {
		return findPage(quickFilter, filter, namedQueries, perspectives, pageable);
	}

	@Override
	@RolesAllowed("**")
	public DownloadableFile fieldDownload(Long id, String fieldName, OutputStream out) throws ResourceNotFoundException,
			ResourceFieldNotFoundException, FieldArtifactNotFoundException, IOException {
		return fieldDownload(id, fieldName, out);
	}

	@Override
	@RolesAllowed("**")
	public List<ResourceArtifact> artifactFindAll(ResourceArtifactType type) {
		return artifactFindAll(type);
	}

	@Override
	@RolesAllowed("**")
	public ResourceArtifact artifactGetOne(ResourceArtifactType type, String code) throws ArtifactNotFoundException {
		return artifactGetOne(type, code);
	}

	@Override
	@RolesAllowed("**")
	public <P extends Serializable> List<?> reportGenerate(String code, P params)
			throws ArtifactNotFoundException, ReportGenerationException {
		return reportGenerate(code, params);
	}
}