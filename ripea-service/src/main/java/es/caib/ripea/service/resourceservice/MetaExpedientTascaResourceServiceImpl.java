package es.caib.ripea.service.resourceservice;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientTascaResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.MetaExpedientTascaResource;
import es.caib.ripea.service.intf.model.MetaNodeResource;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientTascaResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaExpedientTascaResourceServiceImpl extends BaseMutableResourceService<MetaExpedientTascaResource, Long, MetaExpedientTascaResourceEntity> implements MetaExpedientTascaResourceService {

	private final ConfigHelper configHelper;
	
    @PostConstruct
    public void init() {}
	
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
        String entitatActualCodi = configHelper.getEntitatActualCodi();
    	
        Filter filtreBase = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(MetaExpedientTascaResource.Fields.metaExpedient + "." + MetaNodeResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
        );
        
        return filtreBase.generate();
    }

    @Override
    protected void afterConversion(MetaExpedientTascaResourceEntity entity, MetaExpedientTascaResource resource) {
        if (entity.getEstatCrearTasca()!=null) {
            resource.setEstatColorCrearTasca(entity.getEstatCrearTasca().getColor());
        }
        if (entity.getEstatFinalitzarTasca()!=null) {
            resource.setEstatColorFinalitzarTasca(entity.getEstatFinalitzarTasca().getColor());
        }
    }

}