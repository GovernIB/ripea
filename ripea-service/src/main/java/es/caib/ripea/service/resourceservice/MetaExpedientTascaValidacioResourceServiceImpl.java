package es.caib.ripea.service.resourceservice;

import javax.annotation.PostConstruct;

import es.caib.ripea.persistence.entity.resourcerepository.MetaDadaResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaDocumentResourceRepository;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ItemValidacioTascaEnum;
import es.caib.ripea.service.intf.dto.TipusValidacioTascaEnum;
import es.caib.ripea.service.intf.model.*;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientTascaValidacioResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientTascaValidacioResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaExpedientTascaValidacioResourceServiceImpl extends BaseMutableResourceService<MetaExpedientTascaValidacioResource, Long, MetaExpedientTascaValidacioResourceEntity> implements MetaExpedientTascaValidacioResourceService {

	private final MetaDadaResourceRepository metaDadaResourceRepository;
	private final MetaDocumentResourceRepository metaDocumentResourceRepository;
	private final ConfigHelper configHelper;

    @PostConstruct
    public void init() {
        register(MetaExpedientTascaValidacioResource.Fields.itemValidacio, new ItemValidacioOnchangeLogicProcessor());
    }
	
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
        String entitatActualCodi = configHelper.getEntitatActualCodi();
    	
        Filter filtreBase = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(MetaExpedientTascaValidacioResource.Fields.metaExpedientTasca + "." + MetaExpedientTascaResource.Fields.metaExpedient + "." + MetaNodeResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
        );
        
        return filtreBase.generate();
    }

    @Override
    protected void beforeCreateSave(MetaExpedientTascaValidacioResourceEntity entity, MetaExpedientTascaValidacioResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        switch (resource.getItemValidacio()) {
            case DADA:
                entity.setItemId(resource.getMetaDada().getId());
                break;
            case DOCUMENT:
                entity.setItemId(resource.getMetaDocument().getId());
                break;
        }
    }

    @Override
    protected void beforeUpdateSave(MetaExpedientTascaValidacioResourceEntity entity, MetaExpedientTascaValidacioResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        switch (resource.getItemValidacio()) {
            case DADA:
                entity.setItemId(resource.getMetaDada().getId());
                break;
            case DOCUMENT:
                entity.setItemId(resource.getMetaDocument().getId());
                break;
        }
    }

    @Override
    protected void afterConversion(MetaExpedientTascaValidacioResourceEntity entity, MetaExpedientTascaValidacioResource resource) {
        if (entity.getItemId()!=null && entity.getItemValidacio()!=null) {
            switch (entity.getItemValidacio()) {
                case DADA:
                    metaDadaResourceRepository.findById(entity.getItemId()).ifPresent(metaDada -> {
                        resource.setMetaDada(ResourceReference.toResourceReference(metaDada.getId(), metaDada.getNom()));
                    });
                    break;
                case DOCUMENT:
                    metaDocumentResourceRepository.findById(entity.getItemId()).ifPresent(metaDocument -> {
                        resource.setMetaDocument(ResourceReference.toResourceReference(metaDocument.getId(), metaDocument.getNom()));
                    });
                    break;
            }
        }
    }

    private static class ItemValidacioOnchangeLogicProcessor implements OnChangeLogicProcessor<MetaExpedientTascaValidacioResource> {
        @Override
        public void onChange(Serializable id, MetaExpedientTascaValidacioResource previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, MetaExpedientTascaValidacioResource target) {
            if (ItemValidacioTascaEnum.DADA.equals((ItemValidacioTascaEnum) fieldValue)) {
                target.setTipusValidacio(TipusValidacioTascaEnum.AP);
            }
        }
    }
}
