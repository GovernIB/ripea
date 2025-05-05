package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.ExecucioMassivaContingutResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExecucioMassivaResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.dto.ExecucioMassivaEstatDto;
import es.caib.ripea.service.intf.model.ExecucioMassivaResource;
import es.caib.ripea.service.intf.resourceservice.ExecucioMassivaResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecucioMassivaResourceServiceImpl extends BaseMutableResourceService<ExecucioMassivaResource, Long, ExecucioMassivaResourceEntity> implements ExecucioMassivaResourceService {
    @Override
    protected void afterConversion(ExecucioMassivaResourceEntity entity, ExecucioMassivaResource resource) {
        List<ExecucioMassivaContingutResourceEntity> continguts = entity.getContinguts();
        Map<ExecucioMassivaEstatDto, List<ExecucioMassivaContingutResourceEntity>> contingutMap = continguts.stream()
                        .collect(Collectors.groupingBy(ExecucioMassivaContingutResourceEntity::getEstat));

        resource.setFinalitzades( contingutMap.get(ExecucioMassivaEstatDto.ESTAT_FINALITZAT)!=null ?contingutMap.get(ExecucioMassivaEstatDto.ESTAT_FINALITZAT).size() :0);
        resource.setErrors( contingutMap.get(ExecucioMassivaEstatDto.ESTAT_ERROR)!=null ?contingutMap.get(ExecucioMassivaEstatDto.ESTAT_ERROR).size() :0);
        resource.setPendents( contingutMap.get(ExecucioMassivaEstatDto.ESTAT_PENDENT)!=null ?contingutMap.get(ExecucioMassivaEstatDto.ESTAT_PENDENT).size() :0);
        resource.setCancelats( contingutMap.get(ExecucioMassivaEstatDto.ESTAT_CANCELAT)!=null ?contingutMap.get(ExecucioMassivaEstatDto.ESTAT_CANCELAT).size() :0);

        resource.setExecutades(continguts.size());
    }
}