package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.DadaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaDadaResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.DadaResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaDadaResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.model.DadaResource;
import es.caib.ripea.service.intf.resourceservice.DadaResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementació del servei de gestió de tasques.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DadaResourceServiceImpl extends BaseMutableResourceService<DadaResource, Long, DadaResourceEntity> implements DadaResourceService {

    private final DadaResourceRepository dadaResourceRepository;
    private final MetaDadaResourceRepository metaDadaResourceRepository;

    @Override
    protected void beforeCreateSave(DadaResourceEntity entity, DadaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        updateOrder(entity, entity.getOrdre());
        beforeSave(entity, resource, answers);
    }

    @Override
    protected void beforeUpdateSave(DadaResourceEntity entity, DadaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        beforeSave(entity, resource, answers);
    }

    private void beforeSave(DadaResourceEntity entity, DadaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        MetaDadaResourceEntity metaDadaResourceEntity = metaDadaResourceRepository.findById(resource.getMetaDada().getId()).get();

        String value = resource.getValueByFieldName(metaDadaResourceEntity.getTipus());
        entity.setValor(value);
    }

    @Override
    protected void afterDelete(DadaResourceEntity entity, Map<String, AnswerRequiredException.AnswerValue> answers) {
        updateOrder(entity, null);
    }

    @Override
    protected void afterConversion(DadaResourceEntity entity, DadaResource resource) {
        resource.setValueByFieldName(entity.getMetaDada().getTipus(), entity.getValor());
    }

    private void updateOrder(DadaResourceEntity entity, Integer position) {
        List<DadaResourceEntity> dadaResourceEntityList =
                dadaResourceRepository.findAllByNodeIdAndMetaDadaIdOrderByOrdreAsc(entity.getNode().getId(), entity.getMetaDada().getId())
                        .stream().filter(dada->dada!=entity)
                        .collect(Collectors.toList());

        int count = 0;
        for (DadaResourceEntity dadaResourceEntity : dadaResourceEntityList) {
            if (position != null && position == count) {
                entity.setOrdre(count);
                count++;
            }

            dadaResourceEntity.setOrdre(count);
            count++;
        }

        if (position == null) {
            entity.setOrdre(count);
        }

        dadaResourceRepository.saveAll(dadaResourceEntityList);
    }
}
