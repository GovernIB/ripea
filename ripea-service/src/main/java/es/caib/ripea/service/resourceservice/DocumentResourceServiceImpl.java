package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.ContingutResourceEntity;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.DocumentResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.DocumentResource;
import es.caib.ripea.service.intf.model.DocumentResource.ParentPath;
import es.caib.ripea.service.intf.resourceservice.DocumentResourceService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementació del servei de gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class DocumentResourceServiceImpl extends BaseMutableResourceService<DocumentResource, Long, DocumentResourceEntity> implements DocumentResourceService {

    @PostConstruct
    public void init() {
        register(DocumentResource.PERSPECTIVE_PATH_CODE, new PathPerspectiveApplicator());
    }

    private class PathPerspectiveApplicator implements PerspectiveApplicator<DocumentResource, DocumentResourceEntity> {
        @Override
        public void applySingle(String code, DocumentResourceEntity entity, DocumentResource resource) throws PerspectiveApplicationException {
            if (entity.getPare()!=null){
                resource.setParentPath(getPath(entity));
                resource.setTreePath(resource.getParentPath().stream().map(ParentPath::getNom).collect(Collectors.toList()));
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
    }
}
