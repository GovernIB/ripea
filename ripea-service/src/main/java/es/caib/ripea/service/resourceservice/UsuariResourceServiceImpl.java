package es.caib.ripea.service.resourceservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.MetaExpedientHelper;
import es.caib.ripea.service.helper.RolHelper;
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.base.model.FieldOption;
import es.caib.ripea.service.intf.base.permission.UserPermissionInfo;
import es.caib.ripea.service.intf.base.permission.UserPermissionInfo.PermisosEntitat;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.model.UsuariResource;
import es.caib.ripea.service.intf.resourceservice.UsuariResourceService;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.resourcehelper.UsuariResourceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de gestió d'usuaris.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuariResourceServiceImpl extends BaseMutableResourceService<UsuariResource, String, UsuariResourceEntity> implements UsuariResourceService {

    private final UsuariResourceHelper usuariResourceHelper;
    private final AplicacioService aplicacioService;
    private final MetaExpedientHelper metaExpedientHelper;
    
    @PostConstruct
    public void init() {
    	register(UsuariResource.Fields.numElementsPagina, new ElementsPaginaOptionsProvider());
    }

    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	Filter filtreBase = (Utils.hasValue(currentSpringFilter))?Filter.parse(currentSpringFilter):null;
//    	Filter filtreNif = FilterBuilder.isNotNull(UsuariResource.Fields.nif);
    	Filter filtreNom1 = FilterBuilder.not(FilterBuilder.like(UsuariResource.Fields.codi, "%SYSTEM%"));
    	Filter filtreNom2 = FilterBuilder.not(FilterBuilder.like(UsuariResource.Fields.codi, "$%"));
    	Filter filtreCodis = null;
    	
    	Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
    	if (mapaNamedQueries.size()>0) {
    		String procedimentPermisQueryKey = "AMB_PERMIS_SOBRE_PROCEDIMENT";
    		
	    	if (mapaNamedQueries.containsKey(procedimentPermisQueryKey)) {
	    		String procedimentId = mapaNamedQueries.get(procedimentPermisQueryKey);
	    		
	    		List<String> codisPermisos = metaExpedientHelper.permisFind(Long.valueOf(procedimentId)).stream()
	    		        .map(PermisDto::getPrincipalNom)
	    		        .collect(Collectors.toList());

	    		for (String codi : codisPermisos) {
	    		    filtreCodis = FilterBuilder.or(
	    		            filtreCodis,
	    		            FilterBuilder.equal(UsuariResource.Fields.codi, codi)
	    		    );
	    		}
	    	}
    	
    	}
    	
    	Filter filtreResultat = FilterBuilder.and(
    			filtreBase, 
    			filtreNom1, 
    			filtreNom2,
    			filtreCodis);
    	
    	return filtreResultat.generate();
    }
    
    @Override
	public Page<UsuariResource> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {
		
    	Page<UsuariResource> usuarisBBDD = super.findPage(quickFilter, filter, namedQueries, perspectives, pageable);
    			
    	if (usuarisBBDD==null || usuarisBBDD.isEmpty()) {
    		Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
    		if (mapaNamedQueries.size()>0 && mapaNamedQueries.containsKey("ADD_PLUGIN_USERS") && quickFilter!=null) {
    			List<UsuariDto> usuarisAddicionals = aplicacioService.findUsuariAmbTextDades(quickFilter);
    			List<UsuariResource> usuarisResources = new ArrayList<UsuariResource>();
    			if (usuarisAddicionals!=null) {
    				for (UsuariDto userExt: usuarisAddicionals) {
    					UsuariResource ur = new UsuariResource();
    					ur.setNif(userExt.getNif());
    					ur.setNom(userExt.getNom() + "("+userExt.getCodi()+")");
    					ur.setCodi(userExt.getCodi());
    					usuarisResources.add(ur);
    				}
    				//No es pot modificar la "Page" inicial: java.util.Collections$UnmodifiableCollection.add(Collections.java:1058)
    				return new PageImpl<>(usuarisResources, usuarisBBDD.getPageable(), usuarisBBDD.getTotalElements() + usuarisAddicionals.size());
    			}
    		}
    	}
    	
    	return usuarisBBDD;
	}
    
    public class ElementsPaginaOptionsProvider implements FieldOptionsProvider {
		public List<FieldOption> getOptions(String fieldName, Map<String,String[]> requestParameterMap) {
			List<FieldOption> resultat = new ArrayList<FieldOption>();
			resultat.add(new FieldOption(null, "Automàtic"));
			resultat.add(new FieldOption("10", "10"));
			resultat.add(new FieldOption("20", "20"));
			resultat.add(new FieldOption("50", "50"));
			resultat.add(new FieldOption("100", "100"));
			resultat.add(new FieldOption("250", "250"));
			return resultat;
		}
    }
    
    @Transactional(readOnly = true)
    @Override
    public UserPermissionInfo getCurrentUserPermissionInfo() {

        String usuariCodi = SecurityContextHolder.getContext().getAuthentication().getName();
        UsuariResourceEntity usuari = getEntity(usuariCodi, null);
        if (usuari == null) {
            throw new ResourceNotFoundException(UsuariResource.class, usuariCodi);
        }

        String usuariNom = usuari.getNom();
        boolean superusuari = RolHelper.doesCurrentUserHasRol(BaseConfig.ROLE_SUPER);
        Map<Long, PermisosEntitat> permisosEntitat = usuariResourceHelper.getPermisosEntitat(usuariCodi);

        return UserPermissionInfo.builder()
                .codi(usuariCodi)
                .nom(usuariNom)
                .conf(objectMappingHelper.newInstanceMap(usuari, UsuariResource.class))
                .superusuari(superusuari)
                .permisosEntitat(permisosEntitat)
                .build();
    }

    @Override
    protected UsuariResourceEntity getEntity(String id, String[] perspectives) throws ResourceNotFoundException {
        Optional<UsuariResourceEntity> result;
        Specification<UsuariResourceEntity> pkSpec = hasCodi(id);
        String additionalSpringFilter = additionalSpringFilter(null, null);
        if (additionalSpringFilter != null && !additionalSpringFilter.trim().isEmpty()) {
            result = entityRepository.findOne(pkSpec.and(getSpringFilterSpecification(additionalSpringFilter)));
        } else {
            result = entityRepository.findOne(pkSpec);
        }
        if (result.isPresent()) {
            return result.get();
        } else {
            String idToString = id != null ? id.toString() : "<null>";
            String idMessage = idToString;
            if (additionalSpringFilter != null && !additionalSpringFilter.trim().isEmpty()) {
                idMessage = "{id=" + idToString + ", springFilter=" + additionalSpringFilter + "}";
            }
            throw new ResourceNotFoundException(UsuariResource.class, idMessage);
        }
    }

    public static Specification<UsuariResourceEntity> hasCodi(String id) {
        return (Root<UsuariResourceEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            return cb.equal(root.get("codi"), id);
        };
    }

}
