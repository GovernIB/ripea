package es.caib.ripea.service.resourceservice;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.resourceentity.EntitatResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.resourceservice.EntitatResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntitatResourceServiceImpl extends BaseMutableResourceService<EntitatResource, Long, EntitatResourceEntity> implements EntitatResourceService {

	private final CacheHelper cacheHelper;
	
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
        Filter filtreBase = (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null;
        
        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
    	if (mapaNamedQueries.size()>0) {
    		/**
    		 * S'utilitza en el perfil del usuari, per nomes mostrar els que tenen permisos
    		 */
    		if (mapaNamedQueries.containsKey("BY_USUARI")) {
    			String codiUsuariActual = SecurityContextHolder.getContext().getAuthentication().getName();
    			List<Long> entitatsPermeses = cacheHelper.findEntitatsIdsAccessiblesUsuari(codiUsuariActual);

    	        Filter filtreEntitatsPermeses = null; 
    	        List<String> grupsEntitatsIn = Utils.getIdsEnGruposMil(entitatsPermeses);
    	        if (grupsEntitatsIn!=null) {
    		        for (String aux: grupsEntitatsIn) {
    			        if (aux != null && !aux.isEmpty()) {
    			        	filtreEntitatsPermeses = FilterBuilder.or(filtreEntitatsPermeses, Filter.parse("id IN (" + aux + ")"));
    			        }
    		        }
    	        }
    	        
    	        //Si no te entitats assignades, no retornarà resultats
    	        return filtreEntitatsPermeses.generate();
    		}
    	}
        
        return filtreBase.generate();
    }
}