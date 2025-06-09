package es.caib.ripea.service.resourceservice;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.DocumentEnviamentInteressatResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.base.model.ReportFileType;
import es.caib.ripea.service.intf.dto.AmpliarPlazoForm;
import es.caib.ripea.service.intf.dto.DocumentEnviamentInteressatDto;
import es.caib.ripea.service.intf.model.DocumentEnviamentInteressatResource;
import es.caib.ripea.service.intf.model.DocumentEnviamentInteressatResource.MassiveAction;
import es.caib.ripea.service.intf.model.DocumentNotificacioResource;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.resourceservice.DocumentEnviamentInteressatResourceService;
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
public class DocumentEnviamentInteressatResourceServiceImpl extends BaseMutableResourceService<DocumentEnviamentInteressatResource, Long, DocumentEnviamentInteressatResourceEntity> implements DocumentEnviamentInteressatResourceService {

    private final UsuariResourceRepository usuariResourceRepository;
    private final ExcepcioLogHelper excepcioLogHelper;
    private final PluginHelper pluginHelper;

    @PostConstruct
    public void init() {
        register(DocumentEnviamentInteressatResource.PERSPECTIVE_DETAIL_CODE, new DetailPerspectiveApplicator());
        register(DocumentEnviamentInteressatResource.ACTION_AMPLIAR_PLAC_CODE, new AmpliarPlacActionExecutor());
        register(DocumentEnviamentInteressatResource.REPORT_DESCARREGAR_CERTIFICAT, new CertificatReportGenerator());
    }

    private class DetailPerspectiveApplicator implements PerspectiveApplicator<DocumentEnviamentInteressatResourceEntity, DocumentEnviamentInteressatResource> {

        @Override
        public void applySingle(String code, DocumentEnviamentInteressatResourceEntity entity, DocumentEnviamentInteressatResource resource) throws PerspectiveApplicationException {
            resource.setInteressatInfo(objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getInteressat()), InteressatResource.class));
            if (entity.getInteressat().getRepresentant()!=null){
                resource.setRepresentantInfo(objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getInteressat().getRepresentant()), InteressatResource.class));
            }
            resource.setNotificacioInfo(objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getNotificacio()), DocumentNotificacioResource.class));

            usuariResourceRepository.findById(entity.getNotificacio().getCreatedBy())
                    .ifPresent(usu->resource.setEntregaNif(usu.getNif()));
            resource.setClassificacio(entity.getNotificacio().getExpedient().getMetaExpedient().getClassificacio());
        }
    }

    private class AmpliarPlacActionExecutor implements ActionExecutor<DocumentEnviamentInteressatResourceEntity, DocumentEnviamentInteressatResource.AmpliarPalacFormAction, DocumentEnviamentInteressatResource> {

        @Override
        public DocumentEnviamentInteressatResource exec(String code, DocumentEnviamentInteressatResourceEntity entity, DocumentEnviamentInteressatResource.AmpliarPalacFormAction params) throws ActionExecutionException {
            try {
            	AmpliarPlazoForm documentNotificacioDto = new AmpliarPlazoForm();
            	List<DocumentEnviamentInteressatDto> documentEnviamentInteressats = new ArrayList<DocumentEnviamentInteressatDto>();
            	DocumentEnviamentInteressatDto documentEnviamentInteressatDto = new DocumentEnviamentInteressatDto();
            	documentEnviamentInteressatDto.setDiesAmpliacio(params.getDiesAmpliacio());
            	documentEnviamentInteressatDto.setMotiu(params.getMotiu());
            	documentEnviamentInteressatDto.setEnviamentReferencia(entity.getEnviamentReferencia());
            	documentEnviamentInteressats.add(documentEnviamentInteressatDto);
            	documentNotificacioDto.setDocumentEnviamentInteressats(documentEnviamentInteressats);
            	pluginHelper.ampliarPlazoEnviament(documentNotificacioDto);
            	return null;
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/notificacioInteressat/"+entity.getId()+"/AmpliarPlacActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, "S'ha produit un error al ampliar el plaç per l'enviament del interessat.");
            }
        }

        @Override
        public void onChange(Serializable id, DocumentEnviamentInteressatResource.AmpliarPalacFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentEnviamentInteressatResource.AmpliarPalacFormAction target) {}
    }
    
    private class CertificatReportGenerator implements ReportGenerator<DocumentEnviamentInteressatResourceEntity, DocumentEnviamentInteressatResource.MassiveAction, Serializable> {

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}

		@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
			
			DownloadableFile resultat = null;
			Long notificacioInteressatId = data.get(0)!=null?(Long)data.get(0):null;
			
			try {
				
				DocumentEnviamentInteressatResource.MassiveAction params = (DocumentEnviamentInteressatResource.MassiveAction)data.get(1);
				
				if (params.isMassivo()) {
					throw new ReportGenerationException(DocumentEnviamentInteressatResource.class, notificacioInteressatId, code, "La generació de certificats massiu per interessats de notificacions no esta implementat.");
				} else {
	            	resultat = new DownloadableFile(
	            			"certificacio_"+notificacioInteressatId+".pdf",
	            			"application/pdf",
	            			pluginHelper.notificacioConsultarIDescarregarCertificacio(notificacioInteressatId));
				}
				
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/notificacioInteressat/"+notificacioInteressatId+"/CertificatReportGenerator", e);
				throw new ReportGenerationException(getResourceClass(), notificacioInteressatId, code, "S'ha produit un error al descarregar certificat per l'enviament del interessat.");
			}				
				
			return resultat;
		}
		
		@Override
		public List<Serializable> generateData(String code, DocumentEnviamentInteressatResourceEntity entity, MassiveAction params) throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
			parametres.add(entity!=null?entity.getId():0l);
			parametres.add(params);
			return parametres;
		}
    }
}