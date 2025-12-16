package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.ConsultaPinbalResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.model.*;
import es.caib.ripea.service.intf.model.ConsultaPinbalResource;
import es.caib.ripea.service.intf.resourceservice.ConsultaPinbalResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Implementació del servei de gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultaPinbalResourceServiceImpl extends BaseMutableResourceService<ConsultaPinbalResource, Long, ConsultaPinbalResourceEntity> implements ConsultaPinbalResourceService {

    @PostConstruct
    public void init() {
        register(ConsultaPinbalResource.PERSPECTIVE_DOCUMENT_CODE, new DocumentPerspectiveApplicator());
    }

    @Override
    protected ConsultaPinbalResource entityToResource(ConsultaPinbalResourceEntity entity) {
        ConsultaPinbalResource resource = objectMappingHelper.newInstanceMap(entity, ConsultaPinbalResource.class, ConsultaPinbalResource.Fields.servei);
        resource.setServei(ResourceReference.toResourceReference(
                entity.getServei().getId(),
                entity.getServei().getNom()
        ));
        return resource;
    }

    private class DocumentPerspectiveApplicator implements PerspectiveApplicator<ConsultaPinbalResourceEntity, ConsultaPinbalResource> {
        @Override
        public void applySingle(String code, ConsultaPinbalResourceEntity entity, ConsultaPinbalResource resource) throws PerspectiveApplicationException {
            if (entity.getDocument() != null)
                resource.setDocumentInfo(objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getDocument()), DocumentResource.class));
        }
    }
}