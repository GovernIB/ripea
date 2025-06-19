package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.ContingutLogResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ContingutResourceEntity;
import es.caib.ripea.persistence.repository.ContingutRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.ContingutLogResource;
import es.caib.ripea.service.intf.model.ContingutMovimentResource;
import es.caib.ripea.service.intf.resourceservice.ContingutLogResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContingutLogResourceServiceImpl extends BaseMutableResourceService<ContingutLogResource, Long, ContingutLogResourceEntity> implements ContingutLogResourceService {

    private final ContingutRepository contingutRepository;

    @Override
    protected void afterConversion(ContingutLogResourceEntity entity, ContingutLogResource resource) {
        if(entity.getObjecteId()!=null && !entity.getObjecteId().contains("#")) {
            contingutRepository.findById(Long.valueOf(entity.getObjecteId()))
                    .ifPresent(contingut -> resource.setObjecteNom(contingut.getNom()));
        }

        if (entity.getMoviment() != null) {
            resource.setMoviment(
                    objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getMoviment()), ContingutMovimentResource.class)
            );
        }
        if (entity.getPare() != null) {
            resource.setPare(
                    objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getPare()), ContingutLogResource.class)
            );
        }
    }
}