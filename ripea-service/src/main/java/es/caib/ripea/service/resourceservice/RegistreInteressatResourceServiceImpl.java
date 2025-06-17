package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.RegistreInteressatResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.model.RegistreInteressatResource;
import es.caib.ripea.service.intf.resourceservice.RegistreInteressatResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Implementació del servei de gestió de peticions d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegistreInteressatResourceServiceImpl extends BaseMutableResourceService<RegistreInteressatResource, Long, RegistreInteressatResourceEntity> implements RegistreInteressatResourceService {
    @PostConstruct
    public void init() {
        register(RegistreInteressatResource.PERSPECTIVE_REPRESENTANT_CODE, new RespresentantPerspectiveApplicator());
    }

    // PerspectiveApplicator
    private class RespresentantPerspectiveApplicator implements PerspectiveApplicator<RegistreInteressatResourceEntity, RegistreInteressatResource> {
        @Override
        public void applySingle(String code, RegistreInteressatResourceEntity entity, RegistreInteressatResource resource) throws PerspectiveApplicationException {
            if (entity.getRepresentant() != null) {
                resource.setRepresentantInfo(objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getRepresentant()), RegistreInteressatResource.class));
            }
        }
    }
}
