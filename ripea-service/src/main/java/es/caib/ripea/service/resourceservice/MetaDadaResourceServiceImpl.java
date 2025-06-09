package es.caib.ripea.service.resourceservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import es.caib.ripea.service.intf.base.model.FieldOption;
import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.DadaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaDadaResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.DadaResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.model.DadaResource;
import es.caib.ripea.service.intf.model.MetaDadaResource;
import es.caib.ripea.service.intf.resourceservice.MetaDadaResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de gestió de tasques.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetaDadaResourceServiceImpl extends BaseMutableResourceService<MetaDadaResource, Long, MetaDadaResourceEntity> implements MetaDadaResourceService {

    @PostConstruct
    public void init() {
        register(MetaDadaResource.PERSPECTIVE_DADES, new CountPerspectiveApplicator());
    }

    // PerspectiveApplicator
    private class CountPerspectiveApplicator implements PerspectiveApplicator<MetaDadaResourceEntity, MetaDadaResource> {

        @Override
        public void applySingle(String code, MetaDadaResourceEntity entity, MetaDadaResource resource) throws PerspectiveApplicationException {
            List<DadaResource> dadaResourceList = entity.getDades().stream()
                    .map(dadaResource->objectMappingHelper.newInstanceMap(dadaResource, DadaResource.class))
                    .collect(Collectors.toList());
            resource.setDades(dadaResourceList);
        }
    }
}
