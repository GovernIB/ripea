package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.GrupEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.resourceentity.GrupResourceEntity;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.OrganGestorCacheHelper;
import es.caib.ripea.service.helper.PermisosHelper;
import es.caib.ripea.service.intf.dto.OrganismeDto;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.GrupResource;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
import es.caib.ripea.service.intf.resourceservice.GrupResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.permission.ExtendedPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

/**
 * Implementació del servei de gestió de grups.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GrupResourceServiceImpl extends BaseMutableResourceService<GrupResource, Long, GrupResourceEntity> implements GrupResourceService {

	private final OrganGestorRepository organGestorRepository;
	
	private final ConfigHelper configHelper;
	private final PermisosHelper permisosHelper;
	private final EntityComprovarHelper entityComprovarHelper;
	private final OrganGestorCacheHelper organGestorCacheHelper;
	private final MetaExpedientRepository metaExpedientRepository;
	
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
    	if (mapaNamedQueries.size()>0) {
    		if (mapaNamedQueries.containsKey("BY_PROCEDIMENT")) {
    			Long procedimentId = Long.parseLong(mapaNamedQueries.get("BY_PROCEDIMENT"));
    			Filter filtreGrupsProcediment = null;
    			if (procedimentId>0) {
	    			List<GrupEntity> grupsProcs = metaExpedientRepository.findById(procedimentId).get().getGrups();
	    			List<Long> grupsIds = new ArrayList<Long>();
	    			if (grupsProcs!=null) {
						for (GrupEntity ge: grupsProcs) {
							grupsIds.add(ge.getId());
						}
	    			}
	    			if (grupsIds.size()>0) {
	    				List<String> grupsOrgansProcedimentIn = Utils.getIdsEnGruposMil(grupsIds);
	    				
	    		        if (grupsOrgansProcedimentIn!=null) {
	    			        for (String aux: grupsOrgansProcedimentIn) {
	    				        if (aux != null && !aux.isEmpty()) {
	    				        	filtreGrupsProcediment = FilterBuilder.or(filtreGrupsProcediment, Filter.parse("id IN (" + aux + ")"));
	    				        }
	    			        }
	    		        }
	    			} else {
	    				return FilterBuilder.equal("id", 0).generate();
	    				// ----------------> return sense resultats
	    			}
    			} else {
    				return FilterBuilder.equal("id", 0).generate();
    				// ----------------> return sense resultats
    			}
    			
				return filtreGrupsProcediment.generate();
				// ----------------> return amb resultats
    		}
    	}
    	
        String entitatActualCodi = configHelper.getEntitatActualCodi();
        String organActualCodi	 = configHelper.getOrganActualCodi();
        String rolActual		 = configHelper.getRolActual();
        Permission permis = ExtendedPermission.READ;
		boolean isAdmin = "IPA_ADMIN".equals(rolActual);
		boolean isAdminOrgan = "IPA_ORGAN_ADMIN".equals(rolActual);
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
		
        Filter filtreBase = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(MetaExpedientResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
        );
        
        Filter filtreResultat = null;
        
		/*List<Long> organPermisosIds = permisosHelper.getObjectsIdsWithPermission(OrganGestorEntity.class, isAdminOrgan ? ExtendedPermission.ADMINISTRATION : permis);
		List<String> organIdsVigents = organGestorRepository.findCodisByEntitatAndVigentIds(entitat, Utils.getNullIfEmpty(organPermisosIds));
		List<Long> organsIds = new ArrayList<Long>();
		for (String ogCodi: organIdsVigents) {
			organsIds.addAll(organGestorCacheHelper.getIdsOrgansFills(entitatActualCodi, ogCodi));
		}
        
		//Organs gestors amb permisos
        String ogCodi = GrupResource.Fields.organGestor + ".id";
        Filter filtreOrgansPermesos = null;
        List<String> grupsOrgansPermesosClausulesIn = Utils.getIdsEnGruposMil(organsIds);
        if (grupsOrgansPermesosClausulesIn!=null) {
	        for (String aux: grupsOrgansPermesosClausulesIn) {
		        if (aux != null && !aux.isEmpty()) {
	        		filtreOrgansPermesos = FilterBuilder.or(filtreOrgansPermesos, Filter.parse(ogCodi + " IN (" + aux + ")"));
		        }
	        }
        }*/
		
        List<GrupEntity> grupsPermesos = entityComprovarHelper.findGrupsPermesosProcedimentsGestioActiva(entitat.getId(), rolActual, null);
		List<Long> grupsIds = new ArrayList<Long>();
		if (grupsPermesos.size()>0) {
			for (GrupEntity grup: grupsPermesos) {
				grupsIds.add(grup.getId());
			}
		} else {
			grupsIds.add(0l); //No te permisos sobre cap grup, forçam a que no apareixi cap 
		}
		
		Filter filtreGrupsPermesos = null;
		List<String> grupsPermesosClausulesIn = Utils.getIdsEnGruposMil(grupsIds);
		if (grupsPermesosClausulesIn!=null) {
	        for (String aux: grupsPermesosClausulesIn) {
		        if (aux != null && !aux.isEmpty()) {
	        		filtreGrupsPermesos = FilterBuilder.or(filtreGrupsPermesos, Filter.parse("id IN (" + aux + ")"));
		        }
	        }
        }
        
        filtreResultat = FilterBuilder.and(filtreBase, filtreGrupsPermesos);
        
        return filtreResultat.generate();
    }
}