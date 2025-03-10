package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.InteressatResourceRepository;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ResourceNotDeletedException;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.InteressatResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.resourceservice.InteressatResourceService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    protected void beforeCreateSave(InteressatResourceEntity entity, InteressatResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        if(resource.getRepresentat()!=null){
            Optional<InteressatResourceEntity> interessatResourceEntity = interessatResourceRepository.findById(resource.getRepresentat().getId());
            interessatResourceEntity.ifPresent((interessat)->interessat.setRepresentant(entity));
        }
    }

    @Override
    protected void afterDelete(InteressatResourceEntity entity, Map<String, AnswerRequiredException.AnswerValue> answers) {
        if (entity.getRepresentant()!=null && entity.getRepresentant().getRepresentats().isEmpty()){
            interessatResourceRepository.delete(entity.getRepresentant());
        }
    }

    private class NumDocOnchangeLogicProcessor implements OnChangeLogicProcessor<InteressatResource> {
        @Override
        public void processOnChangeLogic(
                InteressatResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                InteressatResource target) {

            if (fieldValue!=null && fieldValue.toString().length()==9){
                Optional<InteressatResourceEntity> resource = interessatResourceRepository.findByDocumentNum(fieldValue.toString());
                resource.ifPresent((interessatResourceEntity)-> {
                    InteressatResource interessatResource = objectMappingHelper.newInstanceMap(interessatResourceEntity, InteressatResource.class);
                    objectMappingHelper.map(interessatResource, target);
                });
            }
        }
    }
}