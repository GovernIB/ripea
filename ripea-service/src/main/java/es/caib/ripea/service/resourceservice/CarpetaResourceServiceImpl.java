package es.caib.ripea.service.resourceservice;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.resourceentity.CarpetaResourceEntity;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.CarpetaHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.intf.model.CarpetaResource;
import es.caib.ripea.service.intf.model.ContingutResource;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.resourceservice.CarpetaResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarpetaResourceServiceImpl extends BaseMutableResourceService<CarpetaResource, Long, CarpetaResourceEntity> implements CarpetaResourceService {

	private final EntitatRepository entitatRepository;
	
	private final ExcepcioLogHelper excepcioLogHelper;
	private final CarpetaHelper carpetaHelper;
	private final ConfigHelper configHelper;
	
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	String entitatActualCodi = configHelper.getEntitatActualCodi();
        Filter filtreResultat = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(ContingutResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
//                ,FilterBuilder.equal(ExpedientResource.Fields.organGestor + ".codi", organActualCodi)
        );
        String resultat = filtreResultat.generate();
        return resultat;
    }
	
    @Override
    public CarpetaResource create(CarpetaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
    	try {
			//La entitat ja es comprova a pinbalHelper
			EntitatEntity entitatEntity = entitatRepository.findByCodi(configHelper.getEntitatActualCodi());
			carpetaHelper.create(
					entitatEntity.getId(),
					resource.getExpedient().getId(),
					resource.getNom(),
					false,
					null,
					false,
					null, 
					false, 
					null, 
					true);
    	} catch (ValidationException ex) {
    		throw ex;
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/carpeta/"+resource.getId()+"/create", ex);
    	}
    	return null;
    }
}