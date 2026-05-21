package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.resourceentity.OrganGestorResourceEntity;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.helper.OrganGestorHelper;
import es.caib.ripea.service.helper.OrganismeHelper;
import es.caib.ripea.service.helper.PermisosHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.dto.OrganismeDto;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.OrganGestorResource;
import es.caib.ripea.service.intf.resourceservice.OrganGestorResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.permission.ExtendedPermission;
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
	private final ConfigHelper configHelper;
	private final OrganismeHelper organismeHelper;
	private final OrganGestorHelper organGestorHelper;
	private final PermisosHelper permisosHelper;
	private final ExcepcioLogHelper excepcioLogHelper;
	private final MessageHelper messageHelper;
	private final EntityComprovarHelper entityComprovarHelper;

    @PostConstruct
    public void init() {
        register(OrganGestorResource.PERSPECTIVE_PATH_CODE, new PathPerspectiveApplicator());
        register(OrganGestorResource.PERSPECTIVE_COUNT_PERMISOS, new CountPermisosPerspectiveApplicator());
        register(OrganGestorResource.DIR3_PREDICT_CODE, new PredictDir3ActionExecutor());
        register(OrganGestorResource.DIR3_UPDATE_CODE, new UpdateDir3ActionExecutor());
        register(OrganGestorResource.Fields.permetreEnviamentPostal, new PermetreEnviamentPostalOnchangeLogicProcessor());
    }
	
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
        List<Filter> filters = new ArrayList<>();
        filters.add((currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null);
    	
        String entitatActualCodi = configHelper.getEntitatActualCodi();
        String organActualCodi	 = configHelper.getOrganActualCodi();
        String rolActual		 = configHelper.getRolActual();
        
		boolean isAdmin 		= "IPA_ADMIN".equals(rolActual);
		boolean isAdminOrgan 	= "IPA_ORGAN_ADMIN".equals(rolActual);
		boolean isDissenyOrgan 	= "IPA_DISSENY".equals(rolActual);
		boolean isSuper 		= "IPA_SUPER".equals(rolActual);
		
		EntitatEntity entitat 		= entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
		OrganGestorEntity ogEntity	= null;
		
    	/**
    	 * Named querys exclusives
    	 */
        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
    	if (!mapaNamedQueries.isEmpty()) {
    		if (mapaNamedQueries.containsKey("ENTITY")) {
                filters.add(
                    FilterBuilder.and(
                            FilterBuilder.equal(OrganGestorResource.Fields.entitat + "." + EntitatResource.Fields.codi,
                                    entitatActualCodi != null?entitatActualCodi:"................................................................................")
                    )
                );
            }
    		if (mapaNamedQueries.containsKey("EXPEDIENT_FORM")) {
    			
    			Long procedimentId = Long.parseLong(mapaNamedQueries.get("EXPEDIENT_FORM"));
    			Filter filtreOrgansProcediment = null;
    			
    			if (procedimentId>0) {
    				
    				if (organActualCodi!=null) {
    					ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitat.getId(), organActualCodi);
    				}
   				
    				List<OrganismeDto> organismesPermesosCreacioExp = organismeHelper.findPermesosByEntitatAndExpedientTipusIdAndFiltre(
    						entitat,
    						procedimentId,
    						ExtendedPermission.CREATE,
    						null,
    						null,
    						rolActual,
    						ogEntity!=null?ogEntity.getId():null);
    				
    				List<Long> organsIds = new ArrayList<Long>();
					for (OrganismeDto ogCodi: organismesPermesosCreacioExp) {
						organsIds.add(ogCodi.getId());
					}
					
					List<String> grupsOrgansPermesosClausulesIn = Utils.getIdsEnGruposMil(organsIds);
			        if (grupsOrgansPermesosClausulesIn!=null) {
				        for (String aux: grupsOrgansPermesosClausulesIn) {
					        if (aux != null && !aux.isEmpty()) {
					        	filtreOrgansProcediment = FilterBuilder.or(filtreOrgansProcediment, Filter.parse("id" + " IN (" + aux + ")"));
					        }
				        }
			        }
    			}
    			
    			if (filtreOrgansProcediment!=null) {
    				return filtreOrgansProcediment.generate();
    			} else {
    				return FilterBuilder.equal("id", 0).generate();
    			}
    		}

            List<Filter> result = filters.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            return result.isEmpty() ? null : FilterBuilder.and(result).generate();
    	}
		
    	/**
    	 * Filtres ordinaris
    	 */
        Filter filtreBase = (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null;
        Filter filtreResultat = null;
        
        if (isSuper) {
        	//No s'aplica ni el filtre per entitat, perque superusuari no treballa amb entitat seleccionada.
        	return filtreBase!=null ?filtreBase.generate() :null;
        } else { 
        	
            Filter filtreEntitat = FilterBuilder.equal(
                    OrganGestorResource.Fields.entitat + "." + EntitatResource.Fields.codi,
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

    public class PathPerspectiveApplicator implements PerspectiveApplicator<OrganGestorResourceEntity, OrganGestorResource> {
        private List<String> getPathName(OrganGestorResourceEntity entity, List<String> path) {
            path.add(String.valueOf(entity.getId()));
            if (entity.getPare()!=null){
                return getPathName(entity.getPare(), path);
            }
            Collections.reverse(path);
            return path;
        }
        private List<OrganGestorResource> getPath(OrganGestorResourceEntity entity, List<OrganGestorResource> path) {
            OrganGestorResource resource = objectMappingHelper.newInstanceMap(entity, OrganGestorResource.class);
            resource.setPathName(getPathName(entity, new ArrayList<>()));
            path.add(resource);
            if (entity.getPare()!=null){
                return getPath(entity.getPare(), path);
            }
            Collections.reverse(path);
            return path;
        }

        @Override
        public void applySingle(String code, OrganGestorResourceEntity entity, OrganGestorResource resource) throws PerspectiveApplicationException {
            resource.setPathName(getPathName(entity, new ArrayList<>()));
            resource.setPath(getPath(entity, new ArrayList<>()));
        }
    }
    
    private class CountPermisosPerspectiveApplicator implements PerspectiveApplicator<OrganGestorResourceEntity, OrganGestorResource> {
		@Override
		public void applySingle(String code, OrganGestorResourceEntity entity, OrganGestorResource resource) throws PerspectiveApplicationException {
			List<PermisDto> permisosGrup = permisosHelper.findPermisos(entity.getId(), OrganGestorEntity.class); 
			resource.setNumPermisos(permisosGrup!=null?permisosGrup.size():0);
		}
    }
    
    private class PredictDir3ActionExecutor implements ActionExecutor<OrganGestorResourceEntity, Serializable, Serializable> {
		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {
		}
		@Override
		public Serializable exec(String code, OrganGestorResourceEntity entity, Serializable params) throws ActionExecutionException {
			try {
				EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, true, false, false, false);
				return organGestorHelper.predictSyncDir3OrgansGestors(entitat);
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/organGestor/PredictDir3ActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), null, code, messageHelper.getMessage("message.common.action.error")+": "+e.getMessage());
			}				
		}
    }

    private class PermetreEnviamentPostalOnchangeLogicProcessor implements OnChangeLogicProcessor<OrganGestorResource> {
        @Override
        public void onChange(Serializable id, OrganGestorResource previous, String fieldName, Object fieldValue,
                Map<String, AnswerValue> answers, String[] previousFieldNames, OrganGestorResource target) {
            if (Boolean.FALSE.equals(fieldValue)) {
                target.setPermetreEnviamentPostalDescendents(false);
            }
        }
    }

    private class UpdateDir3ActionExecutor implements ActionExecutor<OrganGestorResourceEntity, Serializable, Serializable> {
		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {
		}
		@Override
		public Serializable exec(String code, OrganGestorResourceEntity entity, Serializable params) throws ActionExecutionException {
			try {
				EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, true, false, false, false);
				Object[] resultat = organGestorHelper.syncDir3OrgansGestors(entitat.getId(), LocaleContextHolder.getLocale());
				return "{\"obsoleteUnitats\": \""+((ArrayList)resultat[0]).size()+"\"}";
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/organGestor/UpdateDir3ActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), null, code, messageHelper.getMessage("message.common.action.error")+": "+e.getMessage());
			}
		}
    }
}