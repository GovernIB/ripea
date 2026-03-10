package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientEstatResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientEstatResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExpedientEstatHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.MetaExpedientEstatResource;
import es.caib.ripea.service.intf.model.MetaNodeResource;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientEstatResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaExpedientEstatResourceServiceImpl extends BaseMutableResourceService<MetaExpedientEstatResource, Long, MetaExpedientEstatResourceEntity> implements MetaExpedientEstatResourceService {

	private final ConfigHelper configHelper;
	private final ExpedientEstatHelper expedientEstatHelper;
	private final EntityComprovarHelper entityComprovarHelper;
	private final MetaExpedientEstatResourceRepository metaExpedientEstatResourceRepository;
	
    @PostConstruct
    public void init() {
    	register(MetaExpedientEstatResource.ACTION_REORDENAR_CODE,		new ReordenarActionExecutor());
    }
	
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
        String entitatActualCodi = configHelper.getEntitatActualCodi();
    	
        Filter filtreBase = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(MetaExpedientEstatResource.Fields.metaExpedient + "." + MetaNodeResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
        );
        
        return filtreBase.generate();
    }

    protected void afterCreateSave(MetaExpedientEstatResourceEntity entity, MetaExpedientEstatResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {
    	updateInicialNomesUnActiu(entity);
    }
    
    protected void afterUpdateSave(MetaExpedientEstatResourceEntity entity, MetaExpedientEstatResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {
    	updateInicialNomesUnActiu(entity);
    }
    
    private void updateInicialNomesUnActiu(MetaExpedientEstatResourceEntity entity) {
    	//Si el que s'acaba de guardar es inicial, els inicials anteriors, s'han de desactivar
    	if (entity.isInicial()) {
    		metaExpedientEstatResourceRepository.updateInicialFalseForSameMetaExpedientExcludingId(
    				entity.getMetaExpedient().getId(),
    				entity.getId());
    	}
    }

    private class ReordenarActionExecutor implements ActionExecutor<MetaExpedientEstatResourceEntity, Integer, Serializable> {
		@Override
		public void onChange(Serializable id, Integer previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, Integer target) {
		}
		@Override
		public Serializable exec(String code, MetaExpedientEstatResourceEntity entity, Integer params) throws ActionExecutionException {
			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
			expedientEstatHelper.moveTo(entitatEntity.getId(), entity.getMetaExpedient().getId(), entity.getId(), params, configHelper.getRolActual());
			return "{\"resultado\": \"OK\"}";
		}
    }
}