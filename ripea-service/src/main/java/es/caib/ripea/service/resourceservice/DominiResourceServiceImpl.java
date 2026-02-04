package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.resourceentity.DominiResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.EntitatResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.EntitatResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.DominiHelper;
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
	private final DominiHelper	dominiHelper;
	private final EntitatResourceRepository entitatResourceRepository;

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

    @Override
    protected void afterConversion(DominiResourceEntity entity, DominiResource resource) {
    	resource.setContrasenya(dominiHelper.desxifrarContrasenya(entity.getContrasenya()));
    }
    
    @Override
    protected void beforeCreateSave(DominiResourceEntity entity, DominiResource resource, Map<String, AnswerValue> answers) {
    	xifraPassord(entity, resource);
    }
    
    @Override
    protected void beforeUpdateSave(DominiResourceEntity entity, DominiResource resource, Map<String, AnswerValue> answers) {
    	xifraPassord(entity, resource);
    }
    
    private void xifraPassord(DominiResourceEntity entity, DominiResource resource) {
        String entitatActualCodi = configHelper.getEntitatActualCodi();
        EntitatResourceEntity entitat = entitatResourceRepository.findByCodi(entitatActualCodi);
        entity.setEntitat(entitat);
        entity.setContrasenya(dominiHelper.xifrarContrasenya(resource.getContrasenya()));
    }
    
    private class BuidarCacheDominisActionExecutor implements ActionExecutor<DominiResourceEntity, Serializable, Serializable> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

		@Override
		public Serializable exec(String code, DominiResourceEntity entity, Serializable params) throws ActionExecutionException {
			try {
				cacheHelper.evictFindDominisByConsulta();
                Map<String, String> response = new HashMap<String,String>();
                response.put("status", "OK");
                return (Serializable) response;
			} catch (Exception ex) {
				throw new ActionExecutionException(getResourceClass(), null, code, ex.getMessage());
			}
		}
    }
}
