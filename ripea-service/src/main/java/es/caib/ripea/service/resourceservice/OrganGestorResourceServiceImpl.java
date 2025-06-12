package es.caib.ripea.service.resourceservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.resourceentity.OrganGestorResourceEntity;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.OrganGestorHelper;
import es.caib.ripea.service.intf.dto.OrganismeDto;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
import es.caib.ripea.service.intf.model.OrganGestorResource;
import es.caib.ripea.service.intf.resourceservice.OrganGestorResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de gestió d'òrgans gestors.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganGestorResourceServiceImpl extends BaseMutableResourceService<OrganGestorResource, Long, OrganGestorResourceEntity> implements OrganGestorResourceService {
	
	private final OrganGestorRepository organGestorRepository;
	private final MetaExpedientRepository metaExpedientRepository;
	
	private final ConfigHelper configHelper;
	private final OrganGestorHelper organGestorHelper;
	private final EntityComprovarHelper entityComprovarHelper;
	
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
        String entitatActualCodi = configHelper.getEntitatActualCodi();
        String organActualCodi	 = configHelper.getOrganActualCodi();
        String rolActual		 = configHelper.getRolActual();
        
		boolean isAdmin 		= "IPA_ADMIN".equals(rolActual);
		boolean isAdminOrgan 	= "IPA_ORGAN_ADMIN".equals(rolActual);
		boolean isDissenyOrgan 	= "IPA_DISSENY".equals(rolActual);
		boolean isSuper 		= "IPA_SUPER".equals(rolActual);
		
    	/**
    	 * Named querys exclusives
    	 */
        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
    	if (mapaNamedQueries.size()>0) {
    		if (mapaNamedQueries.containsKey("EXPEDIENT_FORM")) {
    			Long procedimentId = Long.parseLong(mapaNamedQueries.get("EXPEDIENT_FORM"));
    			Filter filtreOrgansProcediment = null;
    			if (procedimentId>0) {
	    			MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.findById(procedimentId).get();
	    			if (!metaExpedientEntity.isComu()) {
	    				List<OrganismeDto> organsGestorsProcediment = organGestorHelper.findDescendents(
	    						configHelper.getEntitatActualCodi(),
	    						metaExpedientEntity.getOrganGestor().getId(),
	    						null);
	    				List<Long> organsIds = new ArrayList<Long>();
						for (OrganismeDto og: organsGestorsProcediment) {
							organsIds.add(og.getId());
						}
	    				List<String> grupsOrgansProcedimentIn = Utils.getIdsEnGruposMil(organsIds);
	    				
	    		        if (grupsOrgansProcedimentIn!=null) {
	    			        for (String aux: grupsOrgansProcedimentIn) {
	    				        if (aux != null && !aux.isEmpty()) {
	    				        	filtreOrgansProcediment = FilterBuilder.or(filtreOrgansProcediment, Filter.parse("id IN (" + aux + ")"));
	    				        }
	    			        }
	    		        }
	    			}
    			} else {
    				return FilterBuilder.equal("id", 0).generate();
    				// ----------------> return
    			}
				return filtreOrgansProcediment.generate();
				// ----------------> return
    		}
    	}
		
    	/**
    	 * Filtres ordinaris
    	 */
    	
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
        Filter filtreBase = (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null;
        Filter filtreResultat = null;
        
        if (isSuper) {
        	//No s'aplica ni el filtre per entitat, perque superusuari no treballa amb entitat seleccionada.
        	return filtreBase.generate();
        } else { 
        	
            Filter filtreEntitat = FilterBuilder.equal(
            		MetaExpedientResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
            		entitatActualCodi != null?entitatActualCodi:"................................................................................");

            filtreResultat = FilterBuilder.and(filtreBase, filtreEntitat);
            
            //Ja s'ha filtrat per entitat actual, al admin no se li aplica cap altre filtre
        	if (!isAdmin) {
	        	
        		//Organs amb permisos que es calculen quant no ets ni admin ni super
        		List<Long> organsIds = new ArrayList<Long>();
        		
		        if (isAdminOrgan || isDissenyOrgan) {
		        	
		        	if (organActualCodi!=null) {
			        	//Organs sobre els quals té permisos de amdinistrador o disseny
			        	List<OrganGestorEntity> organsGestorsAdminDisseny = entityComprovarHelper.findAccessiblesUsuariActualRolAdminOrDisseny(
			        			entitat.getId(),
			        			organGestorRepository.findByEntitatIdAndCodi(entitat.getId(), organActualCodi).getId(),
			        			null);
						for (OrganGestorEntity ogCodi: organsGestorsAdminDisseny) {
							organsIds.add(ogCodi.getId());
						}
		        	}
		        } else {
		        	//Organs amb permisos de lectura
		        	List<OrganGestorEntity> organsGestorsUsuari = entityComprovarHelper.findAccessiblesUsuariActualRolUsuari(entitat.getId(), null, false);
					for (OrganGestorEntity ogCodi: organsGestorsUsuari) {
						organsIds.add(ogCodi.getId());
					}
		        }
		        
		        /**
		         * Organs gestors amb permisos
		         */
		        Filter filtreOrgansPermesos = null;
		        if (organsIds.size()>0) {
			        List<String> grupsOrgansPermesosClausulesIn = Utils.getIdsEnGruposMil(organsIds);
			        if (grupsOrgansPermesosClausulesIn!=null) {
				        for (String aux: grupsOrgansPermesosClausulesIn) {
					        if (aux != null && !aux.isEmpty()) {
				        		filtreOrgansPermesos = FilterBuilder.or(filtreOrgansPermesos, Filter.parse("id" + " IN (" + aux + ")"));
					        }
				        }
			        }
		        } else {
		        	//Forçam a que no apareguin resultats
		        	return FilterBuilder.equal("id", 0).generate();
		        }
		        
		        filtreResultat = FilterBuilder.and(filtreResultat, filtreOrgansPermesos);
        	}
        }

        return filtreResultat.generate();
    }
}