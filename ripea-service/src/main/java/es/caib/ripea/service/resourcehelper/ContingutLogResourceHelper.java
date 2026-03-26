package es.caib.ripea.service.resourcehelper;

import es.caib.ripea.persistence.entity.resourceentity.ContingutLogResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.ContingutLogResourceRepository;
import es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContingutLogResourceHelper {

    private final ContingutLogResourceRepository contingutLogResourceRepository;

    public ContingutLogResourceEntity crearRelacioExpedientLog(ExpedientResourceEntity entity, Long relacionatId) {
        ContingutLogResourceEntity log = new ContingutLogResourceEntity();
        log.setContingutId(entity.getId());
        log.setTipus(LogTipusEnumDto.MODIFICACIO);
        log.setObjecteId(entity.getId() + "#" + relacionatId);
        log.setObjecteTipus(LogObjecteTipusEnumDto.RELACIO);
        log.setObjecteLogTipus(LogTipusEnumDto.CREACIO);
        log.setParam1(String.valueOf(entity.getId()));
        log.setParam2(String.valueOf(relacionatId));

        return contingutLogResourceRepository.save(log);
    }
    public ContingutLogResourceEntity eliminarRelacioExpedientLog(ExpedientResourceEntity entity, Long relacionatId) {
        ContingutLogResourceEntity log = new ContingutLogResourceEntity();
        log.setContingutId(entity.getId());
        log.setTipus(LogTipusEnumDto.MODIFICACIO);
        log.setObjecteId(entity.getId() + "#" + relacionatId);
        log.setObjecteTipus(LogObjecteTipusEnumDto.RELACIO);
        log.setObjecteLogTipus(LogTipusEnumDto.ELIMINACIO);
        log.setParam1(String.valueOf(entity.getId()));
        log.setParam2(String.valueOf(relacionatId));

        return contingutLogResourceRepository.save(log);
    }
}
