package es.caib.ripea.service.resourceservice;

import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.resourceentity.MetaDocumentResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.MetaDocumentResource;
import es.caib.ripea.service.intf.resourceservice.MetaDocumentResourceService;
import lombok.extern.slf4j.Slf4j;


/**
 * Implementació del servei de gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class MetaDocumentResourceServiceImpl extends BaseMutableResourceService<MetaDocumentResource, Long, MetaDocumentResourceEntity> implements MetaDocumentResourceService {

    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
    	Filter filtreResultat = Filter.parse(currentSpringFilter);
    	
    	if (Stream.of(namedQueries).anyMatch("CREATE_NEW_DOC"::equals)) {
    		
    	} else if (Stream.of(namedQueries).anyMatch("UPDATE_DOC"::equals)) {
    		
    	}
    	
    	return filtreResultat.generate();
    }
}
