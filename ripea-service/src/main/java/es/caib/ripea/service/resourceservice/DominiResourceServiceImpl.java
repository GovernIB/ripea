package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.resourceentity.DominiResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.model.DominiResource;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.resourceservice.DominiResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DominiResourceServiceImpl extends BaseMutableResourceService<DominiResource, Long, DominiResourceEntity> implements DominiResourceService {

	private final ConfigHelper	configHelper;
	private final CacheHelper	cacheHelper;
	
    @PostConstruct
    public void init() {
    	register(DominiResource.ACTION_EMPTY_CACHE_CODE, new BuidarCacheDominisActionExecutor());
    }
	
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
    	String entitatActualCodi = configHelper.getEntitatActualCodi();
    	
        Filter filtreBase = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(DominiResource.Fields.entitat + "." + EntitatResource.Fields.codi,
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
        );
        
        return filtreBase.generate();
    }
    
    private class BuidarCacheDominisActionExecutor implements ActionExecutor<DominiResourceEntity, Serializable, Serializable> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

		@Override
		public Serializable exec(String code, DominiResourceEntity entity, Serializable params) throws ActionExecutionException {
			try {
				cacheHelper.evictFindDominisByConsulta();
				return "OK";
			} catch (Exception ex) {
				throw new ActionExecutionException(getResourceClass(), null, code, ex.getMessage());
			}
		}
    }
}
