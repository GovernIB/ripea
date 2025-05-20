package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.resourceentity.DocumentPortafirmesResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.firma.DocumentFirmaPortafirmesHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.DocumentHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
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
	private final ExcepcioLogHelper excepcioLogHelper;
	private final DocumentHelper documentHelper;
	private final ConfigHelper configHelper;
	private final EntityComprovarHelper entityComprovarHelper;
	private final DocumentFirmaPortafirmesHelper firmaPortafirmesHelper;
	
    @PostConstruct
    public void init() {
    	register(DocumentPortafirmesResource.ACTION_CANCEL_FIRMA, new CancelFirmaActionExecutor());
    }
    
    @Override
    protected void afterConversion(DocumentPortafirmesResourceEntity entity, DocumentPortafirmesResource resource) {
    	try {
    		UsuariResourceEntity usuari = usuariResourceRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName()).get();
    		resource.setUrlFluxSeguiment(pluginHelper.portafirmesRecuperarUrlEstatFluxFirmes(entity.getId(), usuari.getIdioma()!=null?usuari.getIdioma():"ca"));
    	} catch (Exception ex) {
    		//No s'ha pogut carregar la URL del fluxe
    	}
    }
    
    private class CancelFirmaActionExecutor implements ActionExecutor<DocumentPortafirmesResourceEntity, Serializable, String> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

		@Override
		public String exec(String code, DocumentPortafirmesResourceEntity entity, Serializable params) throws ActionExecutionException {
			try {
				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
				String rolActual = configHelper.getRolActual();
				DocumentEntity document = documentHelper.comprovarDocument(entitatEntity.getId(), entity.getDocument().getId(), false, true, false, false, false, rolActual);
				firmaPortafirmesHelper.portafirmesCancelar(entitatEntity.getId(), document, rolActual);
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/documentPortafirmes/CancelFirmaActionExecutor", e);
			}
			return null;
		}
    }
}