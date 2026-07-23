package es.caib.ripea.service.resourceservice;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.resourceentity.DocumentNotificacioResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.DocumentNotificacioResourceRepository;
import es.caib.ripea.plugin.notificacio.RespostaJustificantEnviamentNotib;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.DocumentHelper;
import es.caib.ripea.service.helper.DocumentNotificacioHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.ExecucioMassivaHelper;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.helper.MetaExpedientHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.base.exception.ResourceNotDeletedException;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.base.model.ReportFileType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.service.intf.dto.ElementTipusEnumDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaTipusDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.model.ContingutResource;
import es.caib.ripea.service.intf.model.DocumentEnviamentResource;
import es.caib.ripea.service.intf.model.DocumentNotificacioResource;
import es.caib.ripea.service.intf.model.DocumentNotificacioResource.MassiveAction;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.resourceservice.DocumentNotificacioResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentNotificacioResourceServiceImpl extends BaseMutableResourceService<DocumentNotificacioResource, Long, DocumentNotificacioResourceEntity> implements DocumentNotificacioResourceService {

	private final ConfigHelper configHelper;
	private final PluginHelper pluginHelper;
	private final DocumentHelper documentHelper;
	private final ExcepcioLogHelper excepcioLogHelper;
	private final EntityComprovarHelper entityComprovarHelper;
	private final DocumentNotificacioHelper documentNotificacioHelper;
	private final ExecucioMassivaHelper execucioMassivaHelper;
	private final MessageHelper messageHelper;
	private final MetaExpedientHelper metaExpedientHelper;

	private final DocumentNotificacioResourceRepository documentNotificacioResourceRepository;
	
    @PostConstruct
    public void init() {
        register(DocumentNotificacioResource.ACTION_ACTUALITZAR_ESTAT_CODE, new ActualitzarEstatActionExecutor());
        register(DocumentNotificacioResource.ACTION_DESCARREGAR_JUSTIFICANT, new JustificantReportGenerator());
        register(DocumentNotificacioResource.ACTION_DESCARREGAR_DOC_ENVIAT, new DescarregarDocEnviatReportGenerator());
    }

    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
    	String entitatActualCodi = configHelper.getEntitatActualCodi();
    	String rolActual		 = configHelper.getRolActual();
    	
    	boolean isAdmin = "IPA_ADMIN".equals(rolActual);
    	
        Filter filtreBase = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(DocumentEnviamentResource.Fields.document + "." + ContingutResource.Fields.entitat + "." + EntitatResource.Fields.codi,
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
//              ,Filter.parse("exists(documentInteressats.id is not null)")
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
    protected void afterConversion(DocumentNotificacioResourceEntity entity, DocumentNotificacioResource resource) {
        resource.setFitxerNom(entity.getDocument().getFitxerNom());
        resource.setHasDocumentInteressats(!entity.getDocumentInteressats().isEmpty());
        resource.setProcediment(ResourceReference.toResourceReference(
                entity.getExpedient().getMetaExpedient().getId(),
                entity.getExpedient().getMetaExpedient().getNomClassificacio()));
        if (entity.getDocumentInteressats()!=null && !entity.getDocumentInteressats().isEmpty()) {
	        resource.setDestinatari(
	                objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getDocumentInteressats().iterator().next().getInteressat()), InteressatResource.class)
	        );
        }
    }

    @Override
    protected void beforeDelete(DocumentNotificacioResourceEntity entity, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotDeletedException {
    	try {
	    	EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
	    	DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(entitatEntity.getId(), entity.getDocument().getId(), false, true);
	    	if (document!=null) {
	    		if (!DocumentNotificacioTipusEnumDto.MANUAL.equals(entity.getTipus())) {
	    			throw new ResourceNotDeletedException(getResourceClass(), entity.getId().toString(), "Nomes es poden eliminar notificacions manuals.");
	    		}
	    	} else {
	    		throw new ResourceNotDeletedException(getResourceClass(), entity.getId().toString(), "Modificació del document de la notificació no permés.");
	    	}
    	} catch (Exception e) {
    		throw new ResourceNotDeletedException(getResourceClass(), entity.getId().toString(), "Error al eliminar la notificació: "+e.getMessage(), e);
    	}
    }
    
    private class ActualitzarEstatActionExecutor implements ActionExecutor<DocumentNotificacioResourceEntity, MassiveAction, Serializable> {

        @Override
        public Serializable exec(String code, DocumentNotificacioResourceEntity entity, MassiveAction params) throws ActionExecutionException {
        	try {

        		if (params.getIds()!=null) {
        			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);

        			//Comprovam l'accés a totes les notificacions seleccionades abans d'executar res
        			for (Long docId: params.getIds()) {
        				DocumentNotificacioResourceEntity notificacio = documentNotificacioResourceRepository.findById(docId).get();
                    	DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
                    			entitatEntity.getId(),
                    			notificacio.getDocument().getId(),
                    			false,
                    			true);
                    	if (document==null) {
                    		throw new ActionExecutionException(
                    				getResourceClass(),
                    				docId,
                    				code,
                    				messageHelper.getMessage("documentNotificacio.actualitzarEstat.reject.credential"));
                    	}
        			}

        			if (!params.isMassivo()) {
        				//Acció individual: execució síncrona
        				for (Long docId: params.getIds()) {
        					documentNotificacioHelper.actualitzarEstat(docId);
        				}
        			} else {
        				//Acció massiva: execució en segon pla
        				ExecucioMassivaDto dto = new ExecucioMassivaDto();
        				dto.setTipus(ExecucioMassivaTipusDto.ACTUALITZAR_ESTAT_NOTIFICACIONS);
        				dto.setContingutIds(params.getIds());
        				dto.setRolActual(configHelper.getRolActual());
        				execucioMassivaHelper.crearExecucioMassiva(entitatEntity.getId(), dto, ElementTipusEnumDto.NOTIFICACIO);
        			}
        		}

            	int numElem = params!=null && params.getIds()!=null?params.getIds().size():0;
            	return "{\"num\": \""+numElem+"\"}";
            	
			} catch (Exception e) {
				String docIdsStr = Utils.getIdsSeparatsComa(params.getIds());
				excepcioLogHelper.addExcepcio("/notificacio/ActualitzarEstatActionExecutor", e, docIdsStr, "massiu="+params.isMassivo());
				String message = messageHelper.getMessage("documentNotificacio.actualitzarEstat.reject")+": "+e.getMessage();
				throw new ActionExecutionException(getResourceClass(), docIdsStr, code, message);
            }
        }

        @Override
        public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}
    }

    private class JustificantReportGenerator implements ReportGenerator<DocumentNotificacioResourceEntity, DocumentNotificacioResource.MassiveAction, Serializable> {

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}

		@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {

			DownloadableFile resultat = null;
			Long documentNotificacioId = data.get(0)!=null?(Long)data.get(0):null;
			DocumentNotificacioResource.MassiveAction params = (DocumentNotificacioResource.MassiveAction)data.get(1);

			if (params.isMassivo()) {
                //La generació massiva de justificants de notificacions no esta implementat. No pareix probable que la demanin. Pero ho deixam obert a implementació.
				throw new ReportGenerationException(getResourceClass(), documentNotificacioId, code, "documentNotificacio.justificant.massive.reject");
			} else {
				DocumentNotificacioResourceEntity documentNotificacioResourceEntity = documentNotificacioResourceRepository.findById(documentNotificacioId).get();
				RespostaJustificantEnviamentNotib resposta = pluginHelper.notificacioDescarregarJustificantEnviamentNotib(documentNotificacioResourceEntity.getNotificacioIdentificador());
				if (resposta.isError()) {
					throw new ReportGenerationException(getResourceClass(), documentNotificacioId, code, messageHelper.getMessage("documentNotificacio.justificant.reject", new Object[]{resposta.getErrorDescripcio()}));
				} else {
	            	resultat = new DownloadableFile(
	            			"justificantEnviament_"+documentNotificacioId+".pdf",
	            			"application/pdf",
	            			resposta.getJustificant());
				}
			}
			return resultat;
		}
		
		@Override
		public List<Serializable> generateData(String code, DocumentNotificacioResourceEntity entity, MassiveAction params) throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
			parametres.add(!params.getIds().isEmpty() ?params.getIds().get(0) :0);
			parametres.add(params);
			return parametres;
		}
    }

    private class DescarregarDocEnviatReportGenerator implements ReportGenerator<DocumentNotificacioResourceEntity, DocumentNotificacioResource.MassiveAction, Serializable> {

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}

		@Override
		public List<Serializable> generateData(String code, DocumentNotificacioResourceEntity entity, MassiveAction params) throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
			parametres.add(!params.getIds().isEmpty() ?params.getIds().get(0) :0l);
			return parametres;
		}

		@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
			Long documentNotificacioId = data.get(0)!=null?(Long)data.get(0):null;
			try {
				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
				DocumentNotificacioResourceEntity documentNotificacioResourceEntity = documentNotificacioResourceRepository.findById(documentNotificacioId).get();
	        	DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
	        			entitatEntity.getId(),
	        			documentNotificacioResourceEntity.getDocument().getId(),
						true,
						false);

	        	FitxerDto fitxerDto = documentHelper.getFitxerAssociat(document, null);

            	return new DownloadableFile(
            			fitxerDto.getNom(),
            			fitxerDto.getContentType(),
            			fitxerDto.getContingut());
			} catch (Exception ex) {
				excepcioLogHelper.addExcepcio("/documentEnviament/"+documentNotificacioId+"/DescarregarDocEnviatReportGenerator", ex);
				throw new ReportGenerationException(
						getResourceClass(),
						documentNotificacioId,
						code,
                        messageHelper.getMessage("documentNotificacio.docEnviat.reject", new Object[]{ex.getMessage()}));
			}
		}
    }

}