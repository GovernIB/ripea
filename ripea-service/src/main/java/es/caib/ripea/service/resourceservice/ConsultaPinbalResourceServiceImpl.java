package es.caib.ripea.service.resourceservice;

import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.resourceentity.ConsultaPinbalResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.model.ConsultaPinbalResource;
import es.caib.ripea.service.intf.model.ContingutResource;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.resourceservice.ConsultaPinbalResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultaPinbalResourceServiceImpl extends BaseMutableResourceService<ConsultaPinbalResource, Long, ConsultaPinbalResourceEntity> implements ConsultaPinbalResourceService {
    
	private final ConfigHelper configHelper;
	private final UsuariResourceRepository usuariResourceRepository;
	
    @PostConstruct
    public void init() {
    	register(ConsultaPinbalResource.PERSPECTIVE_AUDIT_CODE, new AuditoriaPerspectiveApplicator());
    }
	
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
    	String entitatActualCodi = configHelper.getEntitatActualCodi();
//        String rolActual		 = configHelper.getRolActual();
//    	EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
    	
        Filter filtreBase = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(ConsultaPinbalResource.Fields.expedient + "." + ContingutResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
        );
        
        return filtreBase.generate();
    }
    
	@Override
    protected ConsultaPinbalResource entityToResource(ConsultaPinbalResourceEntity entity) {
        ConsultaPinbalResource resource = objectMappingHelper.newInstanceMap(entity, ConsultaPinbalResource.class, ConsultaPinbalResource.Fields.servei);
        resource.setServei(ResourceReference.toResourceReference(
                entity.getServei().getId(),
                entity.getServei().getNom()
        ));
        return resource;
    }
    
    private class AuditoriaPerspectiveApplicator implements PerspectiveApplicator<ConsultaPinbalResourceEntity, ConsultaPinbalResource> {
        @Override
        public void applySingle(String code, ConsultaPinbalResourceEntity entity, ConsultaPinbalResource resource) throws PerspectiveApplicationException {
        	if (entity.getCreatedBy()!=null) {
        		UsuariResourceEntity usuariResourceEntity = usuariResourceRepository.findById(entity.getCreatedBy()).orElse(null);
        		if (usuariResourceEntity!=null) {
        			resource.setCreatedByFullName(usuariResourceEntity.getNom() + " (" + usuariResourceEntity.getCodi() + ")");
        		}
        	}
        	if (entity.getLastModifiedBy()!=null) {
        		UsuariResourceEntity usuariResourceEntity = usuariResourceRepository.findById(entity.getLastModifiedBy()).orElse(null);
        		resource.setLastModifiedByFullName(usuariResourceEntity.getNom() + " (" + usuariResourceEntity.getCodi() + ")");
        	}
        }
    }
}