package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.base.entity.ResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ConsultaPinbalResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.PinbalServeiResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.ConsultaPinbalResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.PinbalServeiResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.model.ConsultaPinbalResource;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.resourceservice.ConsultaPinbalResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Implementació del servei de gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultaPinbalResourceServiceImpl extends BaseMutableResourceService<ConsultaPinbalResource, Long, ConsultaPinbalResourceEntity> implements ConsultaPinbalResourceService {
    @Override
    protected ConsultaPinbalResource entityToResource(ConsultaPinbalResourceEntity entity) {
        ConsultaPinbalResource resource = objectMappingHelper.newInstanceMap(entity, ConsultaPinbalResource.class, ConsultaPinbalResource.Fields.servei);
        resource.setServei(ResourceReference.toResourceReference(
                entity.getServei().getId(),
                entity.getServei().getNom()
        ));
        return resource;
    }
}