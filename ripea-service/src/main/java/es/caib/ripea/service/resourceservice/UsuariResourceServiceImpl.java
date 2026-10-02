package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
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

import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.helper.MetaExpedientHelper;
import es.caib.ripea.service.helper.RolHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
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
import es.caib.ripea.service.resourcehelper.UsuariPermisosResourceHelper;
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
    private final UsuariPermisosResourceHelper usuariPermisosResourceHelper;
    private final UsuariRepository usuariRepository;
    private final ConfigHelper configHelper;
    private final MessageHelper messageHelper;

    @PostConstruct
    public void init() {
    	register(UsuariResource.Fields.numElementsPagina, new ElementsPaginaOptionsProvider());
    	register(UsuariResource.ACTION_BAIXA, new BaixaActionExecutor());
    	register(UsuariResource.ACTION_ALTA, new AltaActionExecutor());
    	register(UsuariResource.ACTION_REVOCAR_PERMIS, new RevocarPermisActionExecutor());
    	register(UsuariResource.REPORT_PERMISOS_RESUM, new PermisosResumReportGenerator());
    	register(UsuariResource.REPORT_PERMISOS_DETALL, new PermisosDetallReportGenerator());
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

    /**
     * El manteniment d'usuaris i el visor de permisos són exclusius del superusuari. El superusuari no
     * treballa amb entitat actual: l'entitat, quan cal, arriba per paràmetre.
     */
    private void comprovarSuperusuari(String code, String usuariCodi) {
    	if (!BaseConfig.ROLE_SUPER.equals(configHelper.getRolActual())) {
    		throw new ActionExecutionException(
    				getResourceClass(),
    				usuariCodi,
    				code,
    				messageHelper.getMessage("usuari.manteniment.error.superusuari"));
    	}
    }

    private class BaixaActionExecutor implements ActionExecutor<UsuariResourceEntity, UsuariResource.BaixaFormAction, UsuariResource> {
		@Override
		public void onChange(Serializable id, UsuariResource.BaixaFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, UsuariResource.BaixaFormAction target) {}

		@Override
		public UsuariResource exec(String code, UsuariResourceEntity entity, UsuariResource.BaixaFormAction params) throws ActionExecutionException {
			comprovarSuperusuari(code, entity.getCodi());
			String usuariActualCodi = SecurityContextHolder.getContext().getAuthentication().getName();
			if (entity.getCodi().equalsIgnoreCase(usuariActualCodi)) {
				throw new ActionExecutionException(getResourceClass(), entity.getCodi(), code, messageHelper.getMessage("usuari.manteniment.baixa.error.propia"));
			}
			UsuariEntity usuari = usuariRepository.findById(entity.getCodi())
					.orElseThrow(() -> new ResourceNotFoundException(UsuariResource.class, entity.getCodi()));
			usuari.updateBaixa(usuariActualCodi, params.getMotiu());
			log.info("Usuari donat de baixa (usuari={}, baixaUsuari={})", usuari.getCodi(), usuariActualCodi);
			// L'entitat del recurs no veu el canvi (columnes de només lectura): es retorna l'estat nou
			UsuariResource resource = objectMappingHelper.newInstanceMap(entity, UsuariResource.class);
			resource.setActiu(usuari.isActiu());
			resource.setBaixaData(usuari.getBaixaData());
			resource.setBaixaUsuari(usuari.getBaixaUsuari());
			resource.setBaixaMotiu(usuari.getBaixaMotiu());
			return resource;
		}
    }

    private class AltaActionExecutor implements ActionExecutor<UsuariResourceEntity, Serializable, UsuariResource> {
		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

		@Override
		public UsuariResource exec(String code, UsuariResourceEntity entity, Serializable params) throws ActionExecutionException {
			comprovarSuperusuari(code, entity.getCodi());
			UsuariEntity usuari = usuariRepository.findById(entity.getCodi())
					.orElseThrow(() -> new ResourceNotFoundException(UsuariResource.class, entity.getCodi()));
			usuari.updateAlta();
			log.info("Usuari donat d'alta (usuari={}, altaUsuari={})", usuari.getCodi(), SecurityContextHolder.getContext().getAuthentication().getName());
			UsuariResource resource = objectMappingHelper.newInstanceMap(entity, UsuariResource.class);
			resource.setActiu(true);
			return resource;
		}
    }

    private class RevocarPermisActionExecutor implements ActionExecutor<UsuariResourceEntity, UsuariResource.RevocarPermisFormAction, Serializable> {
		@Override
		public void onChange(Serializable id, UsuariResource.RevocarPermisFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, UsuariResource.RevocarPermisFormAction target) {}

		@Override
		public Serializable exec(String code, UsuariResourceEntity entity, UsuariResource.RevocarPermisFormAction params) throws ActionExecutionException {
			comprovarSuperusuari(code, entity.getCodi());
			try {
				usuariPermisosResourceHelper.revocarPermis(entity.getCodi(), params.getTipus(), params.getObjectId(), params.getSidId());
			} catch (IllegalArgumentException ex) {
				throw new ActionExecutionException(getResourceClass(), entity.getCodi(), code, ex.getMessage());
			}
			return params;
		}
    }

    private class PermisosResumReportGenerator implements ReportGenerator<UsuariResourceEntity, Serializable, UsuariResource.PermisosResum> {
		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

		@Override
		public List<UsuariResource.PermisosResum> generateData(String code, UsuariResourceEntity entity, Serializable params) throws ReportGenerationException {
			comprovarSuperusuari(code, entity.getCodi());
			return List.of(usuariPermisosResourceHelper.getPermisosResum(entity.getCodi()));
		}
    }

    private class PermisosDetallReportGenerator implements ReportGenerator<UsuariResourceEntity, UsuariResource.PermisosDetallForm, UsuariResource.PermisDetall> {
		@Override
		public void onChange(Serializable id, UsuariResource.PermisosDetallForm previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, UsuariResource.PermisosDetallForm target) {}

		@Override
		public List<UsuariResource.PermisDetall> generateData(String code, UsuariResourceEntity entity, UsuariResource.PermisosDetallForm params) throws ReportGenerationException {
			comprovarSuperusuari(code, entity.getCodi());
			boolean orfes = params != null && params.isOrfes();
			Long entitatId = params != null ? params.getEntitatId() : null;
			if (!orfes && entitatId == null) {
				return new ArrayList<>();
			}
			return usuariPermisosResourceHelper.getPermisosDetall(entity.getCodi(), entitatId, orfes);
		}
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
        UsuariResourceEntity usuari = getEntity(usuariCodi);
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
    protected UsuariResourceEntity getEntity(String id) throws ResourceNotFoundException {
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
