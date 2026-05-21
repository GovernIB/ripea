package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.service.helper.MessageHelper;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.ContingutLogResourceEntity;
import es.caib.ripea.persistence.repository.ContingutRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.ContingutLogResource;
import es.caib.ripea.service.intf.model.ContingutMovimentResource;
import es.caib.ripea.service.intf.resourceservice.ContingutLogResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContingutLogResourceServiceImpl extends BaseMutableResourceService<ContingutLogResource, Long, ContingutLogResourceEntity> implements ContingutLogResourceService {

    private final ContingutRepository contingutRepository;
    private final UsuariResourceRepository usuariResourceRepository;
    private final MessageHelper messageHelper;

    @Override
    protected void afterConversion(ContingutLogResourceEntity entity, ContingutLogResource resource) {
        if (entity.getObjecteId() != null && !entity.getObjecteId().contains("#")) {
            contingutRepository.findById(Long.valueOf(entity.getObjecteId()))
                    .ifPresent(contingut -> resource.setObjecteNom(contingut.getNom()));
        }

        if (entity.getCreatedBy() != null) {
            UsuariResourceEntity usuariResourceEntity = usuariResourceRepository.findById(entity.getCreatedBy()).orElse(null);
            if (usuariResourceEntity != null) {
                resource.setCreatedByFullName(usuariResourceEntity.getCodiAndNom());
            }
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

        if (resource.getTipus() != null) {
            String tipus = messageHelper.getMessage("es.caib.ripea.service.intf.dto.LogTipusEnumDto." + resource.getTipus());
            resource.setTipusString(tipus);
        }
        if (resource.getObjecteId() != null) {
            String objTipus = messageHelper.getMessage("es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto." + resource.getObjecteTipus());
            String logObjTipus = messageHelper.getMessage("es.caib.ripea.service.intf.dto.LogTipusEnumDto." + resource.getObjecteLogTipus());
            resource.setMssg((resource.getObjecteLogTipus() != null ? logObjTipus : "") + "[" + objTipus + "#" + resource.getObjecteId() + "]");
        }

        if (entity.getPare() != null) {
            afterConversion(entity.getPare(), resource.getPare());
        }
    }
}