package es.caib.ripea.service.resourceservice;

import org.springframework.stereotype.Service;
import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;
import es.caib.ripea.persistence.entity.resourceentity.MetaNodeResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.MetaNodeResource;
import es.caib.ripea.service.intf.resourceservice.MetaNodeResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaNodeResourceServiceImpl 
extends BaseMutableResourceService<MetaNodeResource, Long, MetaNodeResourceEntity<MetaNodeResource>> 
implements MetaNodeResourceService {
	
	private final ConfigHelper configHelper;
//	private final EntityComprovarHelper entityComprovarHelper;
	
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
    	String entitatActualCodi = configHelper.getEntitatActualCodi();
//        String rolActual		 = configHelper.getRolActual();
//    	EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
    	
        Filter filtreBase = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(MetaNodeResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
        );
        
        return filtreBase.generate();
    }
}