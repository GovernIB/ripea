package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

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
import es.caib.ripea.service.helper.MetaExpedientHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.dto.PortafirmesDocumentTipusDto;
import es.caib.ripea.service.intf.model.ContingutResource;
import es.caib.ripea.service.intf.model.DocumentEnviamentResource;
import es.caib.ripea.service.intf.model.DocumentPortafirmesResource;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.resourceservice.DocumentPortafirmesResourceService;
import es.caib.ripea.service.intf.utils.Utils;
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
	private final MetaExpedientHelper metaExpedientHelper;
	
    @PostConstruct
    public void init() {
    	register(DocumentPortafirmesResource.ACTION_CANCEL_FIRMA, new CancelFirmaActionExecutor());
    }
    
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
    	String entitatActualCodi = configHelper.getEntitatActualCodi();
    	String rolActual		 = configHelper.getRolActual();
    	
    	boolean isAdmin = "IPA_ADMIN".equals(rolActual);
    	
        Filter filtreBase = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(DocumentEnviamentResource.Fields.document + "." + ContingutResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
                		entitatActualCodi != null?entitatActualCodi:"................................................................................"),
                FilterBuilder.equal(DocumentEnviamentResource.Fields.document + "." + ContingutResource.Fields.esborrat, 0)
        );
        
        Filter filtrePermisos = null;
        if (!isAdmin) {
        	EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, true, false, false, false ,false);
	        List<Long> idMetaExpedientPermesos = metaExpedientHelper.getIdsReadPermesos(entitat.getId());
	        List<String> permesosClausulesIn = Utils.getIdsEnGruposMil(idMetaExpedientPermesos);
	        if (permesosClausulesIn!=null) {
	        	String campProcedimentId = DocumentEnviamentResource.Fields.expedient + "." + ExpedientResource.Fields.metaExpedient +".id";
		        for (String aux: permesosClausulesIn) {
			        if (aux != null && !aux.isEmpty()) {
			        	filtrePermisos = FilterBuilder.or(filtrePermisos, Filter.parse(campProcedimentId+" IN (" + aux + ")"));
			        }
		        }
	        }
        }
        
        Filter filtreResultat = FilterBuilder.and(filtreBase, filtrePermisos);
        
        return filtreResultat.generate();
    }
    
    @Override
    protected void afterConversion(DocumentPortafirmesResourceEntity entity, DocumentPortafirmesResource resource) {
    	try {
    		UsuariResourceEntity usuari = usuariResourceRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName()).get();
    		resource.setUrlFluxSeguiment(pluginHelper.portafirmesRecuperarUrlEstatFluxFirmes(
    				Long.valueOf(entity.getPortafirmesId()),
    				usuari.getIdioma()!=null?usuari.getIdioma().toString():"ca"));
    		List<PortafirmesDocumentTipusDto> list = pluginHelper.portafirmesFindDocumentTipus();
    		for (PortafirmesDocumentTipusDto doctipus : list) {
    			if (Long.toString(doctipus.getId()).equals(entity.getDocumentTipus())) {
    				resource.setDocumentTipusNom(doctipus.getNom());
    				break;
    			}
    		}
    	} catch (Exception ex) {
    		//No s'ha pogut carregar la URL del fluxe
    	}
    }
    
    private class CancelFirmaActionExecutor implements ActionExecutor<DocumentPortafirmesResourceEntity, Serializable, DocumentPortafirmesResource> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

		@Override
		public DocumentPortafirmesResource exec(String code, DocumentPortafirmesResourceEntity entity, Serializable params) throws ActionExecutionException {
			try {
				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
				String rolActual = configHelper.getRolActual();
				DocumentEntity document = documentHelper.comprovarDocument(entitatEntity.getId(), entity.getDocument().getId(), false, true, false, false, false, rolActual);
				firmaPortafirmesHelper.portafirmesCancelar(entitatEntity.getId(), document, rolActual);
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/documentPortafirmes/CancelFirmaActionExecutor", e);
			}
			return objectMappingHelper.newInstanceMap(entity, DocumentPortafirmesResource.class);
		}
    }
}