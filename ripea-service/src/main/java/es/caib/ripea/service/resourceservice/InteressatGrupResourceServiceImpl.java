package es.caib.ripea.service.resourceservice;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.InteressatEntity;
import es.caib.ripea.persistence.entity.InteressatGrupEntity;
import es.caib.ripea.persistence.entity.resourceentity.InteressatGrupResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.InteressatGrupResourceRepository;
import es.caib.ripea.persistence.repository.InteressatGrupRepository;
import es.caib.ripea.persistence.repository.InteressatRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.base.springfilter.FilterSpecification;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.ExpedientInteressatHelper;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.model.InteressatGrupResource;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.resourceservice.InteressatGrupResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de gestió dels grups d'interessat.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InteressatGrupResourceServiceImpl extends BaseMutableResourceService<InteressatGrupResource, Long, InteressatGrupResourceEntity> implements InteressatGrupResourceService {
    
    private final InteressatGrupResourceRepository interessatGrupResourceRepository;
    @Autowired private InteressatGrupRepository interessatGrupRepository;
    private final InteressatRepository interessatRepository;
    private final ExpedientInteressatHelper expedientInteressatHelper;
    private final EntityComprovarHelper entityComprovarHelper;
    private final ExcepcioLogHelper excepcioLogHelper;
    
    @PostConstruct
    public void init() {
    	register(InteressatGrupResource.PERSPECTIVE_INTERESSATS_CODE, new InteressatsPerspectiveApplicator());
    }
    
    @Override
    public List<InteressatGrupResource> findBySpringFilter(String springFilter) {
        FilterSpecification<InteressatGrupResourceEntity> spec = new FilterSpecification<>(springFilter);
        return interessatGrupResourceRepository.findAll(spec).stream()
                   .map(interesatEntity -> objectMappingHelper.newInstanceMap(interesatEntity, InteressatGrupResource.class))
                   .collect(Collectors.toList());
    }

    public class InteressatsPerspectiveApplicator implements PerspectiveApplicator<InteressatGrupResourceEntity, InteressatGrupResource> {
    	
        @Override
        public void applySingle(String code, InteressatGrupResourceEntity entity, InteressatGrupResource resource)
                throws PerspectiveApplicationException {

            // Si el interesado pertenece a grupos, los mapeamos
            if (entity.getInteressats() != null && !entity.getInteressats().isEmpty()) {
                List<ResourceReference<InteressatResource, Long>> interessats = entity.getInteressats().stream()
                        .map(interessatEntity -> {
                            InteressatResource interessatResource = objectMappingHelper.newInstanceMap(interessatEntity, InteressatResource.class);
                            return ResourceReference.<InteressatResource, Long>toResourceReference(
                            		interessatResource.getId(),
                                    interessatResource.getCodiNom()
                            );
                        })
                        .collect(Collectors.toList());
                resource.setInteressats(interessats);
            } else {
                // Si el grupo no tiene ningun interesado, lo dejamos vacío
                resource.setInteressats(Collections.emptyList());
            }
        }
        
    }
    
    @Override
    @Transactional
    public InteressatGrupResource create(InteressatGrupResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
    	try {
	    	ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(resource.getExpedient().getId(), true, true, true, false, false, null);
	    	List<Long> interessatsIds = resource.getInteressats().stream().map(ResourceReference<InteressatResource,Long>::getId).collect(Collectors.toList());
	    	List<InteressatEntity> interessats = interessatRepository.findAllById(interessatsIds);
	    	InteressatGrupEntity grup = expedientInteressatHelper.createGrup(
	    			expedient,
	    			interessats,
	    			resource.getNom(), 
	    			resource.getDescripcio());
	    	
	    	InteressatGrupResource resultat = new InteressatGrupResource();
	    	resultat.setId(grup.getId());
	    	resultat.setNom(grup.getNom());
	    	
	        return resultat;
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/interessatGrup/create", ex);
    		throw ex;
    	}
    }
    
    @Override
    @Transactional
    public InteressatGrupResource update(Long id, InteressatGrupResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
    	try {
	    	entityComprovarHelper.comprovarExpedient(resource.getExpedient().getId(), true, true, true, false, false, null);
			InteressatGrupEntity grup = interessatGrupRepository.findById(id).get();
	    	List<Long> interessatsIds = resource.getInteressats().stream().map(ResourceReference<InteressatResource,Long>::getId).collect(Collectors.toList());
	    	List<InteressatEntity> interessats = interessatRepository.findAllById(interessatsIds);
	    	grup.update(resource.getNom(), resource.getDescripcio(), interessats);
	    	
	    	InteressatGrupResource resultat = new InteressatGrupResource();
	    	resultat.setId(grup.getId());
	    	resultat.setNom(grup.getNom());
	    	
	        return resultat;
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/interessatGrup/update", ex);
    		throw ex;
    	}
    }
    
    @Override
    @Transactional
    public void delete(Long id, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
    	try {
    		InteressatGrupEntity grup = interessatGrupRepository.findById(id).get();
    		interessatGrupRepository.delete(grup);
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/intressatGrup/"+id+"/delete", ex);
    		throw new ResourceNotFoundException(getResourceClass(), ex.getMessage());
    	}
    }


}