package es.caib.ripea.service.resourceservice;

import javax.annotation.PostConstruct;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.DocumentPortafirmesResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.model.DocumentPortafirmesResource;
import es.caib.ripea.service.intf.resourceservice.DocumentPortafirmesResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentPortafirmesResourceServiceImpl extends BaseMutableResourceService<DocumentPortafirmesResource, Long, DocumentPortafirmesResourceEntity> implements DocumentPortafirmesResourceService {

	private final UsuariResourceRepository usuariResourceRepository;
	private final PluginHelper pluginHelper;
	
    @PostConstruct
    public void init() {}
    
    @Override
    protected void afterConversion(DocumentPortafirmesResourceEntity entity, DocumentPortafirmesResource resource) {
    	try {
    		UsuariResourceEntity usuari = usuariResourceRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName()).get();
    		resource.setUrlFluxSeguiment(pluginHelper.portafirmesRecuperarUrlEstatFluxFirmes(entity.getId(), usuari.getIdioma()!=null?usuari.getIdioma():"ca"));
    	} catch (Exception ex) {
    		//No s'ha pogut carregar la URL del fluxe
    	}
    }
}