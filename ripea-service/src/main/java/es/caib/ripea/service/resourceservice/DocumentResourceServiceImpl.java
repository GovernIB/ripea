package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.ContingutResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaNodeResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.MetaNodeResourceRepository;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ResourceNotUpdatedException;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.model.MetaDocumentResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.DocumentResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.DocumentResource;
import es.caib.ripea.service.intf.model.DocumentResource.ParentPath;
import es.caib.ripea.service.intf.resourceservice.DocumentResourceService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementació del servei de gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentResourceServiceImpl extends BaseMutableResourceService<DocumentResource, Long, DocumentResourceEntity> implements DocumentResourceService {

    private final MetaNodeResourceRepository metaNodeResourceRepository;

    @PostConstruct
    public void init() {
        register(DocumentResource.PERSPECTIVE_PATH_CODE, new PathPerspectiveApplicator());
    }

    @Override
    protected void beforeCreateSave(DocumentResourceEntity entity, DocumentResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        Optional<MetaNodeResourceEntity> optionalResource=metaNodeResourceRepository.findById(resource.getMetaDocument().getId());
        optionalResource.ifPresent(entity::setMetaNode);
    }

    @Override
    protected void beforeUpdateSave(DocumentResourceEntity entity, DocumentResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotUpdatedException {
        Optional<MetaNodeResourceEntity> optionalResource=metaNodeResourceRepository.findById(resource.getMetaDocument().getId());
        optionalResource.ifPresent(entity::setMetaNode);
    }

    @Override
    protected void afterConversion(DocumentResourceEntity entity, DocumentResource resource) {
        if(entity.getMetaNode()!=null) {
            ResourceReference<MetaDocumentResource, Long> metaDocument = new ResourceReference<>();
            metaDocument.setId(entity.getMetaNode().getId());
            metaDocument.setDescription(entity.getMetaNode().getNom());
            resource.setMetaDocument(metaDocument);
        }
    }

    private class PathPerspectiveApplicator implements PerspectiveApplicator<DocumentResource, DocumentResourceEntity> {
        @Override
        public void applySingle(String code, DocumentResourceEntity entity, DocumentResource resource) throws PerspectiveApplicationException {
            if (entity.getPare()!=null){
                List<ParentPath> parentPaths = getPath(entity).stream()
                        .filter((a)-> !Objects.equals(a.getId(), entity.getExpedient().getId()))
                        .collect(Collectors.toList());
                parentPaths.forEach(
                        (a)->setTreePath(a, parentPaths)
                );

                resource.setParentPath(parentPaths);
                resource.setTreePath(parentPaths.stream()
                        .map(ParentPath::getNom)
                        .collect(Collectors.toList()));
            }
        }

        public <E extends ContingutResourceEntity> List<ParentPath> getPath(E entity) {
            List<ParentPath> path = new ArrayList<ParentPath>();
            getPathPare(entity, path);
            Collections.reverse(path);
            return path;
        }
        public <E extends ContingutResourceEntity> void getPathPare(E entity, List<ParentPath> path) {
            if (entity != null) {
                ParentPath pathEntry = new ParentPath(entity.getId(), entity.getNom(), entity.getCreatedBy(), entity.getCreatedDate(), entity.getTipus());
                pathEntry.setId(entity.getId());
                path.add(pathEntry);
                getPathPare(entity.getPare(), path);
            }
        }
        public void setTreePath(ParentPath entity, List<ParentPath> path){
            Boolean notFound = true;
            int arrayIndex = 0;
            List<String> result = new ArrayList<>();
            while (notFound && path.size()>arrayIndex){
                result.add(path.get(arrayIndex).getNom());
                notFound = !Objects.equals(entity.getId(), path.get(arrayIndex).getId());
                arrayIndex++;
            }
            entity.setTreePath(result);
        }
    }
}
