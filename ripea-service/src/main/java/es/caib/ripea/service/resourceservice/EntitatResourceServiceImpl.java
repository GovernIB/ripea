package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.resourceentity.EntitatResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.PermisosHelper;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.resourceservice.EntitatResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntitatResourceServiceImpl extends BaseMutableResourceService<EntitatResource, Long, EntitatResourceEntity> implements EntitatResourceService {

	private final CacheHelper cacheHelper;
	private final ConfigHelper configHelper;
	private final PermisosHelper permisosHelper;
	
    @PostConstruct
    public void init() {
    	register(EntitatResource.PERSPECTIVE_PERMISOS_CODE,	new PermisosPerspectiveApplicator());
    	register(EntitatResource.Fields.logoImgFile, new EntitatImagesOnchangeLogicProcessor());
    	register(EntitatResource.Fields.faviconImgFile, new EntitatImagesOnchangeLogicProcessor());
    	register(EntitatResource.Fields.menuImgFile, new EntitatImagesOnchangeLogicProcessor());
    	register(EntitatResource.Fields.blackLogoImgFile, new EntitatImagesOnchangeLogicProcessor());
    	register(EntitatResource.Fields.blackFaviconImgFile, new EntitatImagesOnchangeLogicProcessor());
    	register(EntitatResource.Fields.blackMenuImgFile, new EntitatImagesOnchangeLogicProcessor());
    }
	
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
        List<Filter> filters = new ArrayList<>();

        Filter filtreBase = (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null;
        filters.add(filtreBase);

        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
    	if (!mapaNamedQueries.isEmpty()) {
    		/**
    		 * S'utilitza en el perfil del usuari, per nomes mostrar els que tenen permisos
    		 */
    		if (mapaNamedQueries.containsKey("BY_USUARI")) {
    			String codiUsuariActual = SecurityContextHolder.getContext().getAuthentication().getName();
    			List<Long> entitatsPermeses = cacheHelper.findEntitatsIdsAccessiblesUsuari(codiUsuariActual);

    	        Filter filtreEntitatsPermeses = null;
    	        List<String> grupsEntitatsIn = Utils.getIdsEnGruposMil(entitatsPermeses);
    	        if (grupsEntitatsIn!=null) {
    		        for (String aux: grupsEntitatsIn) {
    			        if (aux != null && !aux.isEmpty()) {
    			        	filtreEntitatsPermeses = FilterBuilder.or(filtreEntitatsPermeses, Filter.parse("id IN (" + aux + ")"));
    			        }
    		        }
    	        }
    	        
    	        //Si no te entitats assignades, no retornarà resultats
                filters.add(filtreEntitatsPermeses);
    		}
    	}

        List<Filter> result = filters.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return result.isEmpty() ? null : FilterBuilder.and(result).generate();
    }

    private FileReference getFileReferenceByBytes(String name, byte[] bytes) {
        if (bytes == null) return null;
        return new FileReference(name, bytes, null, null);
    }

    @Override
    protected void afterConversion(EntitatResourceEntity entity, EntitatResource resource) {
        resource.setLogoImgFile(
                this.getFileReferenceByBytes("logo",  resource.getLogoImgBytes()));
        resource.setFaviconImgFile(
                this.getFileReferenceByBytes("faviconLogo",  resource.getFaviconImgBytes()));
        resource.setMenuImgFile(
                this.getFileReferenceByBytes("menuLogo",  resource.getMenuImgBytes()));

        resource.setBlackLogoImgFile(
                this.getFileReferenceByBytes("logo",  resource.getBlackLogoImgBytes()));
        resource.setBlackFaviconImgFile(
                this.getFileReferenceByBytes("faviconLogo",  resource.getBlackFaviconImgBytes()));
        resource.setBlackMenuImgFile(
                this.getFileReferenceByBytes("menuLogo",  resource.getBlackMenuImgBytes()));
    }

    private class PermisosPerspectiveApplicator implements PerspectiveApplicator<EntitatResourceEntity, EntitatResource> {
		@Override
		public void applySingle(String code, EntitatResourceEntity entity, EntitatResource resource) throws PerspectiveApplicationException {
			List<PermisDto> permisosEntitat = permisosHelper.findPermisos(entity.getId(), EntitatEntity.class);
			resource.setNumPermisos(permisosEntitat!=null?permisosEntitat.size():0);
		}
    }
    
    private class EntitatImagesOnchangeLogicProcessor implements OnChangeLogicProcessor<EntitatResource> {

		@Override
		public void onChange(Serializable id, EntitatResource previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, EntitatResource target) {
			if (EntitatResource.Fields.logoImgFile.equals(fieldName)) {
				if (fieldValue != null) {
					target.setLogoImgBytes(((FileReference)fieldValue).getContent());
				} else {
					target.setLogoImgBytes(null);
				}
			}else if (EntitatResource.Fields.faviconImgFile.equals(fieldName)) {
				if (fieldValue != null) {
					target.setFaviconImgBytes(((FileReference)fieldValue).getContent());
				} else {
					target.setFaviconImgBytes(null);
				}
			}else if (EntitatResource.Fields.menuImgFile.equals(fieldName)) {
				if (fieldValue != null) {
					target.setMenuImgBytes(((FileReference)fieldValue).getContent());
				} else {
					target.setMenuImgBytes(null);
				}
			}else if (EntitatResource.Fields.blackLogoImgFile.equals(fieldName)) {
				if (fieldValue != null) {
					target.setBlackLogoImgBytes(((FileReference)fieldValue).getContent());
				} else {
					target.setBlackLogoImgBytes(null);
				}
			}else if (EntitatResource.Fields.blackFaviconImgFile.equals(fieldName)) {
				if (fieldValue != null) {
					target.setBlackFaviconImgBytes(((FileReference)fieldValue).getContent());
				} else {
					target.setBlackFaviconImgBytes(null);
				}
			}else if (EntitatResource.Fields.blackMenuImgFile.equals(fieldName)) {
				if (fieldValue != null) {
					target.setBlackMenuImgBytes(((FileReference)fieldValue).getContent());
				} else {
					target.setBlackMenuImgBytes(null);
				}
			}
		}
    }
    
    @Override
    protected void afterDelete(EntitatResourceEntity entitat, Map<String, AnswerRequiredException.AnswerValue> answers) {
    	configHelper.deleteConfigEntitat(entitat.getCodi());
    	permisosHelper.deleteAcl(entitat.getId(), EntitatEntity.class);
    }
    
    @Override
    protected void afterCreateSave(EntitatResourceEntity entitat, EntitatResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {
    	configHelper.crearConfigsEntitat(entitat.getCodi());
    }

}