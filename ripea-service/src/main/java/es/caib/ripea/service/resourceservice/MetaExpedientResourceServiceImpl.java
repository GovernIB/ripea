package es.caib.ripea.service.resourceservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.MetaExpedientHelper;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.permission.ExtendedPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de gestió de tasques.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetaExpedientResourceServiceImpl extends BaseMutableResourceService<MetaExpedientResource, Long, MetaExpedientResourceEntity> implements MetaExpedientResourceService {

	private final ConfigHelper configHelper;
	private final MetaExpedientHelper metaExpedientHelper;
	private final EntityComprovarHelper entityComprovarHelper;

	@Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {

        String entitatActualCodi = configHelper.getEntitatActualCodi();
        String organActualCodi	 = configHelper.getOrganActualCodi();
        String rolActual		 = configHelper.getRolActual();
        String organGestorFiltre = Utils.getValorCampFiltre("organGestor.id", currentSpringFilter);
        
		boolean isAdmin = "IPA_ADMIN".equals(rolActual);
		boolean isAdminOrgan = "IPA_ORGAN_ADMIN".equals(rolActual);
		boolean isDissenyador = "IPA_DISSENY".equals(rolActual);
		boolean usuariFiltreOrgan = isAdminOrgan || isDissenyador;
        
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
    	Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
		
		Filter filtreBase = null;
		//Si ja ve un filtre definit per entitat, no aplicarem el filtre de entitat actual.
		if (currentSpringFilter==null || !currentSpringFilter.contains("entitat.id")) {
	        filtreBase = FilterBuilder.and(
	                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
	                FilterBuilder.equal(MetaExpedientResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
	                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
	        );
		}
        
//        if (organActualCodi!=null && usuariFiltreOrgan) {
//        	Filter filtreOrganGestor = FilterBuilder.equal(MetaExpedientResource.Fields.organGestor+"."+OrganGestorResource.Fields.codi, organActualCodi);
//        	filtreBase = FilterBuilder.and(filtreBase, filtreOrganGestor);
//        }
        
        List<Long> procsPermesosIds = new ArrayList<Long>();
        List<MetaExpedientEntity> metaExpPermesos = null;
        if (mapaNamedQueries.size()>0 && mapaNamedQueries.containsKey("EXPEDIENT_CREATE")) {
            metaExpPermesos = metaExpedientHelper.findAmbPermis(
            		entitat.getId(),
            		ExtendedPermission.CREATE,
            		true, //nomesActius
            		null, //filtreNomOrCodiSia
            		isAdmin,
            		isAdminOrgan,
            		null, //organId
            		false); //comú
        } else if (mapaNamedQueries.size()>0 && mapaNamedQueries.containsKey("EXPEDIENT_UPDATE")) {
            metaExpPermesos = metaExpedientHelper.findAmbPermis(
            		entitat.getId(),
            		ExtendedPermission.WRITE,
            		false, //nomesActius
            		null, //filtreNomOrCodiSia
            		isAdmin,
            		isAdminOrgan,
            		null, //organId
            		false); //comú
        } else { //Llistat de procediments
            metaExpPermesos = metaExpedientHelper.findAmbPermis(
            		entitat.getId(),
            		ExtendedPermission.READ,
            		false, //nomesActius
            		null, //filtreNomOrCodiSia
            		isAdmin,
            		isAdminOrgan,
            		null, //organId
            		false); //comú
        }
        
		if (metaExpPermesos==null || metaExpPermesos.size()==0) {
			return FilterBuilder.equal("id", 0).generate();
		} else {
			for (MetaExpedientEntity mee: metaExpPermesos) {
				procsPermesosIds.add(mee.getId());
			}
		}
		
		Filter filtrePermisos = null;
        List<String> permesosClausulesIn = Utils.getIdsEnGruposMil(procsPermesosIds);
        if (permesosClausulesIn!=null) {
	        for (String aux: permesosClausulesIn) {
		        if (aux != null && !aux.isEmpty()) {
		        	filtrePermisos = FilterBuilder.or(filtrePermisos, Filter.parse("id IN (" + aux + ")"));
		        }
	        }
        }
        
		Filter filtreResultat = FilterBuilder.and(filtreBase, filtrePermisos);
        
        return filtreResultat.generate();
        
//        if (!isAdmin) {
//        
//	        List<Long> metaExpedientIds = permisosHelper.getObjectsIdsWithPermission(MetaNodeEntity.class, permis);
//			List<Long> organPermisosIds = permisosHelper.getObjectsIdsWithPermission(OrganGestorEntity.class, isAdminOrgan ? ExtendedPermission.ADMINISTRATION : permis);
//			List<String> organIdsVigents = organGestorRepository.findCodisByEntitatAndVigentIds(entitat, Utils.getNullIfEmpty(organPermisosIds));
//			List<Long> organsIds = new ArrayList<Long>();
//			for (String ogCodi: organIdsVigents) {
//				organsIds.addAll(organGestorCacheHelper.getIdsOrgansFills(entitatActualCodi, ogCodi));
//			}
//			List<Long> metaExpedientOrganIds = permisosHelper.getObjectsIdsWithPermission(MetaExpedientOrganGestorEntity.class,	permis);
//			List<Long> organProcedimentsComunsIds = permisosHelper.getObjectsIdsWithTwoPermissions(OrganGestorEntity.class, ExtendedPermission.COMU, permis);
//			List<Long> organAdmIds = permisosHelper.getObjectsIdsWithPermission(OrganGestorEntity.class, ExtendedPermission.ADM_COMU);
//			boolean accessAllComu = false;
//			if (Utils.isNotEmpty(organProcedimentsComunsIds) || Utils.isNotEmpty(organAdmIds)) {
//				accessAllComu = true;
//			}
//			
//	        /**
//	         * Procediment (meta-expedients) amb permisos
//	         */
//	        String procedimentId = "id";
//	        Filter filtreProcedimentsPermesos = null;
//	        List<String> grupsClausulesIn = Utils.getIdsEnGruposMil(metaExpedientIds);
//	        if (grupsClausulesIn!=null) {
//		        for (String aux: grupsClausulesIn) {
//			        if (aux != null && !aux.isEmpty()) {
//			        	filtreProcedimentsPermesos = FilterBuilder.or(filtreProcedimentsPermesos, Filter.parse(procedimentId + " IN (" + aux + ")"));
//			        }
//		        }
//	        }
//			
//	        /**
//	         * Organs gestors amb permisos
//	         */
//	        String ogCodi = MetaExpedientResource.Fields.organGestor + ".id";
//	        Filter filtreOrgansPermesos = null;
//	        List<String> grupsOrgansPermesosClausulesIn = Utils.getIdsEnGruposMil(organsIds);
//	        if (grupsOrgansPermesosClausulesIn!=null) {
//		        for (String aux: grupsOrgansPermesosClausulesIn) {
//			        if (aux != null && !aux.isEmpty()) {
//		        		filtreOrgansPermesos = FilterBuilder.or(filtreOrgansPermesos, Filter.parse(ogCodi + " IN (" + aux + ")"));
//			        }
//		        }
//	        }
//	        
//	        /**
//	         * Meta expedient - Organs gestors amb permisos
//	         */
//	        String meOgCodi = MetaExpedientResource.Fields.metaExpedientOrganGestors + ".id";
//	        Filter filtreMetaExpedientOrgansPermesos = null;
//	        List<String> metaExpedientOrgansPermesosClausulesIn = Utils.getIdsEnGruposMil(metaExpedientOrganIds);
//	        if (metaExpedientOrgansPermesosClausulesIn!=null) {
//		        for (String aux: metaExpedientOrgansPermesosClausulesIn) {
//			        if (aux != null && !aux.isEmpty()) {
//			        	filtreMetaExpedientOrgansPermesos = FilterBuilder.or(filtreMetaExpedientOrgansPermesos, Filter.parse(meOgCodi + " IN (" + aux + ")"));
//			        }
//		        }
//	        }
//	        
//	      //Combinam els 3 filtres anteriors
//	        Filter combinedFilterProcedimentsOr = FilterBuilder.or(
//	        		filtreProcedimentsPermesos,
//	        		filtreOrgansPermesos,
//	        		filtreMetaExpedientOrgansPermesos);
//        
//        	filtreResultat = FilterBuilder.and(filtreBase, combinedFilterProcedimentsOr);
//        	
//        } else {
//        	
//        	filtreResultat = filtreBase;
//        	
//        }
    }

    @Override
    protected void afterConversion(MetaExpedientResourceEntity entity, MetaExpedientResource resource) {
        resource.setNumComentaris(entity.getComentaris().size());
    }
}