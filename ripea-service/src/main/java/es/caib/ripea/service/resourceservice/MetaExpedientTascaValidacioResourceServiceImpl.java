package es.caib.ripea.service.resourceservice;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientTascaValidacioResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.MetaExpedientTascaResource;
import es.caib.ripea.service.intf.model.MetaExpedientTascaValidacioResource;
import es.caib.ripea.service.intf.model.MetaNodeResource;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientTascaValidacioResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaExpedientTascaValidacioResourceServiceImpl extends BaseMutableResourceService<MetaExpedientTascaValidacioResource, Long, MetaExpedientTascaValidacioResourceEntity> implements MetaExpedientTascaValidacioResourceService {

	private final ConfigHelper configHelper;
	
    @PostConstruct
    public void init() {}
	
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
        String entitatActualCodi = configHelper.getEntitatActualCodi();
    	
        Filter filtreBase = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(MetaExpedientTascaValidacioResource.Fields.metaExpedientTasca + "." + MetaExpedientTascaResource.Fields.metaExpedient + "." + MetaNodeResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
        );
        
        return filtreBase.generate();
    }
}
