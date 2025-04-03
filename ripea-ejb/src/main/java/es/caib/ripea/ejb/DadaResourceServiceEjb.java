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
import es.caib.ripea.service.intf.model.DadaResource;
import es.caib.ripea.service.intf.resourceservice.DadaResourceService;
import lombok.experimental.Delegate;

@Stateless
public class DadaResourceServiceEjb extends AbstractServiceEjb<DadaResourceService> implements DadaResourceService {

	@Delegate private DadaResourceService delegateService;
	
	protected void setDelegateService(DadaResourceService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public DadaResource newResourceInstance() {
		return delegateService.newResourceInstance();
	}

	@Override
	@RolesAllowed("**")
	public DadaResource create(DadaResource resource, Map<String, AnswerValue> answers)
			throws ResourceAlreadyExistsException, ResourceNotCreatedException, AnswerRequiredException {
		return delegateService.create(resource, answers);
	}

	@Override
	@RolesAllowed("**")
	public DadaResource update(Long id, DadaResource resource, Map<String, AnswerValue> answers)
			throws ResourceNotFoundException, ResourceNotUpdatedException, AnswerRequiredException {
		return delegateService.update(id, resource, answers);
	}

	@Override
	@RolesAllowed("**")
	public void delete(Long id, Map<String, AnswerValue> answers)
			throws ResourceNotFoundException, ResourceNotDeletedException, AnswerRequiredException {
		delegateService.delete(id, answers);
	}

	@Override
	@RolesAllowed("**")
	public Map<String, Object> onChange(DadaResource previous, String fieldName, Object fieldValue,
			Map<String, AnswerValue> answers) throws ResourceFieldNotFoundException, AnswerRequiredException {
		return delegateService.onChange(previous, fieldName, fieldValue, answers);
	}

	@Override
	@RolesAllowed("**")
	public <P extends Serializable> Serializable artifactActionExec(Long id, String code, P params)
			throws ArtifactNotFoundException, ActionExecutionException {
		return delegateService.artifactActionExec(id, code, params);
	}

	@Override
	@RolesAllowed("**")
	public DadaResource getOne(Long id, String[] perspectives) throws ResourceNotFoundException {
		return delegateService.getOne(id, perspectives);
	}

	@Override
	@RolesAllowed("**")
	public Page<DadaResource> findPage(String quickFilter, String filter, String[] namedQueries, String[] perspectives,
			Pageable pageable) {
		return delegateService.findPage(quickFilter, filter, namedQueries, perspectives, pageable);
	}

	@Override
	@RolesAllowed("**")
	public DownloadableFile fieldDownload(Long id, String fieldName, OutputStream out) throws ResourceNotFoundException,
			ResourceFieldNotFoundException, FieldArtifactNotFoundException, IOException {
		return delegateService.fieldDownload(id, fieldName, out);
	}

	@Override
	@RolesAllowed("**")
	public List<ResourceArtifact> artifactFindAll(ResourceArtifactType type) {
		return delegateService.artifactFindAll(type);
	}

	@Override
	@RolesAllowed("**")
	public ResourceArtifact artifactGetOne(ResourceArtifactType type, String code) throws ArtifactNotFoundException {
		return delegateService.artifactGetOne(type, code);
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