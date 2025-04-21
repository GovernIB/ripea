package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.EntitatResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.resourceservice.EntitatResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Implementació del servei de gestió d'entitats.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntitatResourceServiceImpl extends BaseMutableResourceService<EntitatResource, Long, EntitatResourceEntity> implements EntitatResourceService {

//    @PostConstruct
//    public void init() {
//        register(EntitatResource.PERSPECTIVE_IMAGE, new ImagePerspectiveApplicator());
//    }
//
//    // PerspectiveApplicator
//    private class ImagePerspectiveApplicator implements PerspectiveApplicator<EntitatResourceEntity, EntitatResource> {
//
//        @Override
//        public void applySingle(String code, EntitatResourceEntity entity, EntitatResource resource) throws PerspectiveApplicationException {
//            resource.setImagen(new FileReference(
//                    null,
//                    entity.getLogoImgBytes(),
//                    null,
//                    null
//            ));
//        }
//    }
}