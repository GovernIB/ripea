package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.*;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.*;
import es.caib.ripea.service.intf.resourceservice.ExpedientEstatResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.parser.Filter;

/**
 * Implementació del servei de gestió de estats d'expedient.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientEstatResourceServiceImpl extends BaseMutableResourceService<ExpedientEstatResource, Long, ExpedientEstatResourceEntity> implements ExpedientEstatResourceService {

    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	return Filter.parse(currentSpringFilter).generate(); 
    }
}
