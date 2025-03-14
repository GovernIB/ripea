package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.InteressatResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.InteressatResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.ResourceNotDeletedException;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.resourceservice.InteressatResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementació del servei de gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InteressatResourceServiceImpl extends BaseMutableResourceService<InteressatResource, Long, InteressatResourceEntity> implements InteressatResourceService {

    private final InteressatResourceRepository interessatResourceRepository;

    @PostConstruct
    public void init() {
        register("documentNum", new NumDocOnchangeLogicProcessor());
    }

    @Override
    protected void afterConversion(InteressatResourceEntity entity, InteressatResource resource) {
        resource.setHasRepresentats(!entity.getRepresentats().isEmpty());
    }

    @Override
    protected void beforeCreateSave(InteressatResourceEntity entity, InteressatResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        if(resource.getRepresentat()!=null){
            Optional<InteressatResourceEntity> interessatResourceEntity = interessatResourceRepository.findById(resource.getRepresentat().getId());
            interessatResourceEntity.ifPresent((interessat)->interessat.setRepresentant(entity));
        }
    }

    @Override
    protected void beforeDelete(InteressatResourceEntity entity, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotDeletedException {
        if (entity.isEsRepresentant()) {
            entity.getRepresentats().forEach((representat) -> {
                representat.setRepresentant(null);
            });
        }
    }

    @Override
    protected void afterDelete(InteressatResourceEntity entity, Map<String, AnswerRequiredException.AnswerValue> answers) {
        InteressatResourceEntity representant = entity.getRepresentant();
        if (representant!=null && representant.isEsRepresentant() && representant.getRepresentats().isEmpty()){
            interessatResourceRepository.delete(representant);
        }
    }

    private class NumDocOnchangeLogicProcessor implements OnChangeLogicProcessor<InteressatResource> {

        public static final String NOT_REPRESENT_HIMSELF = "NOT_REPRESENT_HIMSELF";

        @Override
        public void onChange(
                InteressatResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                InteressatResource target) {

            if (fieldValue!=null && fieldValue.toString().length()==9){
                Optional<InteressatResourceEntity> resource = interessatResourceRepository.findByExpedientIdAndDocumentNum(previous.getExpedient().getId(), fieldValue.toString());
                resource.ifPresent((interessatResourceEntity)-> {
                    if (
                            !answers.containsKey(NOT_REPRESENT_HIMSELF) &&
                            (previous.getRepresentat()!=null && Objects.equals(previous.getRepresentat().getId(), interessatResourceEntity.getId()))
                            || (previous.getRepresentant()!=null && Objects.equals(previous.getRepresentant().getId(), interessatResourceEntity.getId()))
                    ){
                        throw new AnswerRequiredException(InteressatResource.class, NOT_REPRESENT_HIMSELF, "Un interesado no puede representarse a si mismo");
                    }

//                    if (!Objects.equals(interessatResourceEntity.getId(), previous.getId())) {
//                        InteressatResource interessatResource = objectMappingHelper.newInstanceMap(interessatResourceEntity, InteressatResource.class);
//                        objectMappingHelper.map(interessatResource, target, "esRepresentant");
//                    }
                });
            }
        }
    }
}