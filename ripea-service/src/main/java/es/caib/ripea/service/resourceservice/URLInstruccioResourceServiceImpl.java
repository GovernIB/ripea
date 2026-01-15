package es.caib.ripea.service.resourceservice;

import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.resourceentity.URLInstruccioResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.intf.model.ContingutResource;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.URLInstruccioResource;
import es.caib.ripea.service.intf.resourceservice.URLInstruccioResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class URLInstruccioResourceServiceImpl extends BaseMutableResourceService<URLInstruccioResource, Long, URLInstruccioResourceEntity> implements URLInstruccioResourceService {

	private final ConfigHelper configHelper;
	
	protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
		
        String entitatActualCodi = configHelper.getEntitatActualCodi();
    	
        Filter filtreBase = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(ContingutResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
//                ,FilterBuilder.equal(ExpedientResource.Fields.organGestor + ".codi", organActualCodi)
        );
        
        return filtreBase.generate();
	}
}
