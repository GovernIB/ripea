package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.base.exception.*;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.model.*;
import es.caib.ripea.service.intf.model.MetaDadaResource;
import es.caib.ripea.service.intf.resourceservice.MetaDadaResourceService;
import lombok.experimental.Delegate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Stateless
public class MetaDadaResourceServiceEjb  extends AbstractServiceEjb<MetaDadaResourceService> implements MetaDadaResourceService {

	@Delegate private MetaDadaResourceService delegateService;
	
	protected void setDelegateService(MetaDadaResourceService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public MetaDadaResource newResourceInstance() {
		return delegateService.newResourceInstance();
	}

	@Override
	@RolesAllowed("**")
	public MetaDadaResource create(MetaDadaResource resource, Map<String, AnswerValue> answers)
			throws ResourceAlreadyExistsException, ResourceNotCreatedException, AnswerRequiredException {
		return delegateService.create(resource, answers);
	}

	@Override
	@RolesAllowed("**")
	public MetaDadaResource update(Long id, MetaDadaResource resource, Map<String, AnswerValue> answers)
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
	public Map<String, Object> onChange(MetaDadaResource previous, String fieldName, Object fieldValue,
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
	public MetaDadaResource getOne(Long id, String[] perspectives) throws ResourceNotFoundException {
		return delegateService.getOne(id, perspectives);
	}

	@Override
	@RolesAllowed("**")
	public Page<MetaDadaResource> findPage(String quickFilter, String filter, String[] namedQueries,
			String[] perspectives, Pageable pageable) {
		return delegateService.findPage(quickFilter, filter, namedQueries, perspectives, pageable);
	}

	@Override
	@RolesAllowed("**")
	public DownloadableFile export(String quickFilter, String filter, String[] namedQueries, String[] perspectives,
			Sort sort, ExportField[] fields, ExportFileType fileType, OutputStream out) {
		return delegateService.export(quickFilter, filter, namedQueries, perspectives, sort, fields, fileType, out);
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