package es.caib.ripea.service.resourceservice;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import es.caib.plugins.arxiu.api.Document;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.resourceentity.ContingutResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.DocumentResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.InteressatResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaDocumentResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.ContingutResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.DocumentResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.ExpedientResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.InteressatResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaDocumentResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.DocumentHelper;
import es.caib.ripea.service.helper.DocumentNotificacioHelper;
import es.caib.ripea.service.helper.EmailHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ResourceNotUpdatedException;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ArxiuDetallDto;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentPublicacioDto;
import es.caib.ripea.service.intf.dto.DocumentVersioDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.InteressatTipusEnum;
import es.caib.ripea.service.intf.dto.SignatureInfoDto;
import es.caib.ripea.service.intf.model.DocumentResource;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.model.DocumentResource.NotificarFormAction;
import es.caib.ripea.service.intf.model.DocumentResource.ParentPath;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.model.MetaDocumentResource;
import es.caib.ripea.service.intf.resourceservice.DocumentResourceService;
import es.caib.ripea.service.resourcehelper.ContingutResourceHelper;
import es.caib.ripea.service.resourcehelper.DocumentResourceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentResourceServiceImpl extends BaseMutableResourceService<DocumentResource, Long, DocumentResourceEntity> implements DocumentResourceService {

    private final DocumentResourceHelper documentResourceHelper;
    private final ContingutResourceHelper contingutResourceHelper;
    private final PluginHelper pluginHelper;
    private final ConfigHelper configHelper;
    private final EmailHelper emailHelper;
    private final DocumentHelper documentHelper;
    private final ExcepcioLogHelper excepcioLogHelper;
    private final DocumentNotificacioHelper documentNotificacioHelper;
    private final EntityComprovarHelper entityComprovarHelper;
    
    private final ExpedientResourceRepository expedientResourceRepository;
    private final ContingutResourceRepository contingutResourceRepository;
    private final DocumentResourceRepository documentResourceRepository;
    private final MetaDocumentResourceRepository metaDocumentResourceRepository;
    private final InteressatResourceRepository interessatResourceRepository;

    @PostConstruct
    public void init() {
        register(DocumentResource.PERSPECTIVE_COUNT_CODE, new CountPerspectiveApplicator());
        register(DocumentResource.PERSPECTIVE_VERSIONS_CODE, new ArxiuVersionsPerspectiveApplicator());
        register(DocumentResource.PERSPECTIVE_ARXIU_DOCUMENT_CODE, new ArxiuDocumentPerspectiveApplicator());
        register(DocumentResource.PERSPECTIVE_PATH_CODE, new PathPerspectiveApplicator());
        register(DocumentResource.Fields.adjunt, new AdjuntFieldDownloader());
        register(DocumentResource.Fields.firmaAdjunt, new FirmaFieldDownloader());
        register(DocumentResource.Fields.imprimible, new ImprimibleFieldDownloader());
        register(DocumentResource.Fields.original, new OriginalFieldDownloader());
        register(DocumentResource.Fields.metaDocument, new MetaDocumentOnchangeLogicProcessor());
        register(DocumentResource.Fields.adjunt, new AdjuntOnchangeLogicProcessor());
        register(DocumentResource.Fields.firmaAdjunt, new FirmaAdjuntOnchangeLogicProcessor());
        register(DocumentResource.Fields.hasFirma, new HasFirmaOnchangeLogicProcessor());
        register(DocumentResource.ACTION_ENVIAR_VIA_EMAIL_CODE, new EnviarViaEmailActionExecutor());
        register(DocumentResource.ACTION_MOURE_CODE, new MoureActionExecutor());
        register(DocumentResource.ACTION_PUBLICAR_CODE, new PublicarActionExecutor());
        register(DocumentResource.ACTION_NOTIFICAR_CODE, new NotificarActionExecutor());
        register(DocumentResource.ACTION_ENVIAR_PORTAFIRMES_CODE, new EnviarPortafirmesActionExecutor());
    }

    @Override
    protected void beforeCreateSave(DocumentResourceEntity entity, DocumentResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        beforeSave(entity, resource, answers);

        entity.setEstat(entity.getDocumentFirmaTipus() == DocumentFirmaTipusEnumDto.SENSE_FIRMA ? DocumentEstatEnumDto.REDACCIO : DocumentEstatEnumDto.FIRMAT);
        entity.setTipus(ContingutTipusEnumDto.DOCUMENT);
        entity.setData(new Date());
        // TODO: revisar
        entity.setEntitat(entity.getMetaNode().getEntitat());
        entity.setNtiIdentificador(Long.toString(System.currentTimeMillis()));
        entity.setNtiOrgano(entity.getExpedient().getNtiOrgano());
        entity.setExpedientEstatAdditional(entity.getExpedient().getEstatAdditional());
    }

    @Override
    protected void beforeUpdateSave(DocumentResourceEntity entity, DocumentResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotUpdatedException {
        beforeSave(entity, resource, answers);
    }

    private void beforeSave(DocumentResourceEntity entity, DocumentResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotUpdatedException {
        entity.setPare(entity.getExpedient());

        Optional<MetaDocumentResourceEntity> optionalDocumentResource = metaDocumentResourceRepository.findById(resource.getMetaDocument().getId());
        optionalDocumentResource.ifPresent((metaDocumentResourceEntity -> {
            entity.setMetaNode(metaDocumentResourceEntity);
            entity.setNtiTipoDocumental(metaDocumentResourceEntity.getNtiTipoDocumental());
        }));

        if (resource.getDocumentFirmaTipus() == DocumentFirmaTipusEnumDto.FIRMA_SEPARADA){
            /* TODO: (PluginHelper.gestioDocumentalCreate) */
        }

        entity.setFitxerNom(documentResourceHelper.getUniqueNameInPare(entity));
    }

    @Override
    protected void afterConversion(DocumentResourceEntity entity, DocumentResource resource) {
        if(entity.getMetaNode()!=null) {
            resource.setMetaDocument(ResourceReference.toResourceReference(entity.getMetaNode().getId(), entity.getMetaNode().getNom()));
        }
        resource.setAdjunt(new FileReference(
                entity.getFitxerNom(),
                entity.getFitxerContingut(),
                entity.getFitxerContentType(),
                entity.getFitxerTamany()
        ));
        resource.setFirmaAdjunt(new FileReference(
                entity.getNomFitxerFirmat(),
                null,
                null,
                null
        ));
        resource.setHasFirma(resource.getDocumentFirmaTipus()!=DocumentFirmaTipusEnumDto.SENSE_FIRMA);
    }

    // PerspectiveApplicator
    private class PathPerspectiveApplicator implements PerspectiveApplicator<DocumentResourceEntity, DocumentResource> {
        @Override
        public void applySingle(String code, DocumentResourceEntity entity, DocumentResource resource) throws PerspectiveApplicationException {
            if (entity.getPare()!=null){
                List<ParentPath> parentPaths = getPath(entity).stream()
                        .filter((a)-> !Objects.equals(a.getId(), entity.getExpedient().getId()))
                        .collect(Collectors.toList());
                parentPaths.forEach(
                        (a)->setTreePath(a, parentPaths)
                );

                resource.setParentPath(parentPaths);
                resource.setTreePath(parentPaths.stream()
                        .map(ParentPath::getNom)
                        .collect(Collectors.toList()));
            }
        }

        public <E extends ContingutResourceEntity> List<ParentPath> getPath(E entity) {
            List<ParentPath> path = new ArrayList<ParentPath>();
            getPathPare(entity, path);
            Collections.reverse(path);
            return path;
        }
        public <E extends ContingutResourceEntity> void getPathPare(E entity, List<ParentPath> path) {
            if (entity != null) {
                ParentPath pathEntry = new ParentPath(entity.getId(), entity.getNom(), entity.getCreatedBy(), entity.getCreatedDate(), entity.getTipus());
                pathEntry.setId(entity.getId());
                path.add(pathEntry);
                getPathPare(entity.getPare(), path);
            }
        }
        public void setTreePath(ParentPath entity, List<ParentPath> path){
            Boolean notFound = true;
            int arrayIndex = 0;
            List<String> result = new ArrayList<>();
            while (notFound && path.size()>arrayIndex){
                result.add(path.get(arrayIndex).getNom());
                notFound = !Objects.equals(entity.getId(), path.get(arrayIndex).getId());
                arrayIndex++;
            }
            entity.setTreePath(result);
        }
    }
    private class ArxiuDocumentPerspectiveApplicator implements PerspectiveApplicator<DocumentResourceEntity, DocumentResource> {
        @Override
        public void applySingle(String code, DocumentResourceEntity entity, DocumentResource resource) throws PerspectiveApplicationException {
            Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
                    entity.getId(), entity.getNom(), entity.getArxiuUuid(), entity.getEntitat().getCodi(),
                    null, null, true, false);
            ArxiuDetallDto arxiu = contingutResourceHelper.getArxiuDocumentDetall(arxiuDocument,entity.getEntitat().getId());
//            ArxiuDetallDto arxiu = contingutResourceHelper.getArxiuDetall(entity.getEntitat().getId(), entity.getId());
            resource.setArxiu(arxiu);
        }
    }
    private class ArxiuVersionsPerspectiveApplicator implements PerspectiveApplicator<DocumentResourceEntity, DocumentResource> {
        @Override
        public void applySingle(String code, DocumentResourceEntity entity, DocumentResource resource) throws PerspectiveApplicationException {
            List<DocumentVersioDto> versions = contingutResourceHelper.getVersions(entity);
            resource.setVersions(versions);
        }
    }
    private class CountPerspectiveApplicator implements PerspectiveApplicator<DocumentResourceEntity, DocumentResource> {
        @Override
        public void applySingle(String code, DocumentResourceEntity entity, DocumentResource resource) throws PerspectiveApplicationException {
            resource.setNumMetaDades(entity.getMetaNode().getMetaDades().size());
        }
    }

    // FieldDownloader
    private class AdjuntFieldDownloader implements FieldDownloader<DocumentResourceEntity> {
        @Override
        public DownloadableFile download(
                DocumentResourceEntity entity,
                String fieldName,
                OutputStream out) {
        	
        	DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
        			entity.getEntitat().getId(),
        			entity.getId(),
					true,
					false);

        	FitxerDto fitxerDto = documentHelper.getFitxerAssociat(document, null);
        	
            return new DownloadableFile(
            		fitxerDto.getNom(),
            		fitxerDto.getContentType(),
            		fitxerDto.getContingut()
            );
        }
    }
    
    private class FirmaFieldDownloader implements FieldDownloader<DocumentResourceEntity> {
        @Override
        public DownloadableFile download(
                DocumentResourceEntity entity,
                String fieldName,
                OutputStream out) {
        	
        	DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
        			entity.getEntitat().getId(),
        			entity.getId(),
					true,
					false);

        	FitxerDto fitxerDto = documentHelper.getFitxerFirmaSeparada(document);
        	
            return new DownloadableFile(
            		fitxerDto.getNom(),
            		fitxerDto.getContentType(),
            		fitxerDto.getContingut()
            );
        }
    }
    
    private class ImprimibleFieldDownloader implements FieldDownloader<DocumentResourceEntity> {
        @Override
        public DownloadableFile download(
                DocumentResourceEntity entity,
                String fieldName,
                OutputStream out) {
        	
        	DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
        			entity.getEntitat().getId(),
        			entity.getId(),
					true,
					false);

        	FitxerDto fitxerDto = pluginHelper.arxiuDocumentVersioImprimible(document);
        	
            return new DownloadableFile(
            		fitxerDto.getNom(),
            		fitxerDto.getContentType(),
            		fitxerDto.getContingut()
            );
        }
    }
    
    private class OriginalFieldDownloader implements FieldDownloader<DocumentResourceEntity> {
        @Override
        public DownloadableFile download(
                DocumentResourceEntity entity,
                String fieldName,
                OutputStream out) {
        	
        	DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
        			entity.getEntitat().getId(),
        			entity.getId(),
					true,
					false);

        	FitxerDto fitxerDto = documentHelper.getContingutOriginal(document);
        	
            return new DownloadableFile(
            		fitxerDto.getNom(),
            		fitxerDto.getContentType(),
            		fitxerDto.getContingut()
            );
        }
    }

    // OnChangeLogicProcessor
    private class MetaDocumentOnchangeLogicProcessor implements OnChangeLogicProcessor<DocumentResource> {
        @Override
        public void onChange(
                DocumentResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                DocumentResource target) {

            if (fieldValue != null) {
                ResourceReference<MetaDocumentResource, Long> resourceReference = (ResourceReference<MetaDocumentResource, Long>) fieldValue;
                Optional<MetaDocumentResourceEntity> optionalDocumentResource = metaDocumentResourceRepository.findById(resourceReference.getId());
                optionalDocumentResource.ifPresent(metaDocumentResourceEntity -> {
                    target.setNtiOrigen(metaDocumentResourceEntity.getNtiOrigen());
                    target.setNtiEstadoElaboracion(metaDocumentResourceEntity.getNtiEstadoElaboracion());
                });
            } else {
                target.setNtiOrigen(null);
                target.setNtiEstadoElaboracion(null);
            }
        }
    }
    private class AdjuntOnchangeLogicProcessor implements OnChangeLogicProcessor<DocumentResource> {

        private static final String ERROR_SIGNATURE_VALIDATION= "ERROR_SIGNATURE_VALIDATION";

        @Override
        public void onChange(
                DocumentResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                DocumentResource target) {

            if (fieldValue != null) {
                FileReference adjunt = (FileReference) fieldValue;

                target.setFitxerNom(adjunt.getName());
                target.setFitxerContingut(adjunt.getContent());
                target.setFitxerTamany(adjunt.getContentLength());
                target.setFitxerContentType(adjunt.getContentType());

                // TODO: cambiar (DocumentService.checkIfSignedAttached())
                SignatureInfoDto signatureInfoDto = new SignatureInfoDto(false);

                target.setHasFirma(signatureInfoDto.isSigned());
                target.setValidacioFirmaCorrecte(!signatureInfoDto.isError());
                target.setValidacioFirmaErrorMsg(signatureInfoDto.getErrorMsg());

                if (signatureInfoDto.isSigned()) {
//                    target.setNtiTipoFirma();
                    target.setDocumentFirmaTipus(DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA);

                    if (signatureInfoDto.isError() && !answers.containsKey(ERROR_SIGNATURE_VALIDATION)) {
                        throw new AnswerRequiredException(InteressatResource.class, ERROR_SIGNATURE_VALIDATION, signatureInfoDto.getErrorMsg());
                    }
                }
            } else {
                target.setFitxerNom(null);
                target.setFitxerContingut(null);
                target.setFitxerTamany(null);
                target.setFitxerContentType(null);
                target.setValidacioFirmaCorrecte(false);
                target.setValidacioFirmaErrorMsg("");
                target.setHasFirma(false);
            }
        }
    }
    private class FirmaAdjuntOnchangeLogicProcessor implements OnChangeLogicProcessor<DocumentResource> {
        @Override
        public void onChange(
                DocumentResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                DocumentResource target) {

            if (fieldValue != null) {
                FileReference adjunt = (FileReference) fieldValue;
                target.setNomFitxerFirmat(adjunt.getName());
            } else {
                target.setNomFitxerFirmat(null);
            }
        }
    }
    private class HasFirmaOnchangeLogicProcessor implements OnChangeLogicProcessor<DocumentResource> {
        @Override
        public void onChange(
                DocumentResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                DocumentResource target) {

            if (target.getDocumentFirmaTipus()!=DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA){
                target.setDocumentFirmaTipus((fieldValue != null && (Boolean) fieldValue)
                        ?DocumentFirmaTipusEnumDto.FIRMA_SEPARADA
                        :DocumentFirmaTipusEnumDto.SENSE_FIRMA);
            }
        }
    }

    // ActionExecutor
    private class EnviarViaEmailActionExecutor implements ActionExecutor<DocumentResourceEntity, DocumentResource.EnviarViaEmailFormAction, DocumentResource> {

        @Override
        public DocumentResource exec(String code, DocumentResourceEntity entity, DocumentResource.EnviarViaEmailFormAction params) throws ActionExecutionException {

            if (!params.getEmail().isEmpty() || !params.getResponsables().isEmpty()) {
                List<String> emails = Arrays.asList(params.getEmail().split(","));
                List<String> desinataris = params.getResponsables().stream()
                        .map(ResourceReference::getId)
                        .collect(Collectors.toList());

                emailHelper.enviarDocument(entity.getId(), emails, desinataris);
            }

            return objectMappingHelper.newInstanceMap(entity, DocumentResource.class);
        }

        @Override
        public void onChange(DocumentResource.EnviarViaEmailFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentResource.EnviarViaEmailFormAction target) {

        }
    }
    private class MoureActionExecutor implements ActionExecutor<DocumentResourceEntity, DocumentResource.MoureFormAction, DocumentResource> {

        @Override
        public DocumentResource exec(String code, DocumentResourceEntity entity, DocumentResource.MoureFormAction params) throws ActionExecutionException {

            if (!Objects.equals(params.getExpedient(), entity.getExpedient())){
                expedientResourceRepository.findById(params.getExpedient().getId())
                        .ifPresent(entity::setExpedient);
            }

//            if (params.getCarpeta()!=null){
//                ContingutResourceEntity contingut = contingutResourceRepository.findById(params.getCarpeta().getId()).orElse(null);
//                if (contingut!=null && !Objects.equals(entity.getPare(), contingut)){
//                    entity.setPare(contingut);
//                    documentResourceRepository.save(entity);
//                }
//            } else {
//                ContingutResourceEntity contingut = contingutResourceRepository.findById(params.getExpedient().getId()).orElse(null);
//                if (contingut!=null && !Objects.equals(entity.getPare(), contingut)){
//                    entity.setPare(contingut);
//                    documentResourceRepository.save(entity);
//                }
//            }

            return objectMappingHelper.newInstanceMap(entity, DocumentResource.class);
        }

        @Override
        public void onChange(DocumentResource.MoureFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentResource.MoureFormAction target) {}
    }
    
    private class PublicarActionExecutor implements ActionExecutor<DocumentResourceEntity, DocumentResource.PublicarFormAction, DocumentResource> {

        @Override
        public DocumentResource exec(String code, DocumentResourceEntity entity, DocumentResource.PublicarFormAction params) throws ActionExecutionException {
        	try {
        		documentHelper.publicarDocument(
        				entity.getEntitat().getId(),
        				entity.getId(),
        				objectMappingHelper.newInstanceMap(params, DocumentPublicacioDto.class));
        		return objectMappingHelper.newInstanceMap(entity, DocumentResource.class);
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/document/"+entity.getId()+"/publicar", e);
				return null;
			}
        }

        @Override
        public void onChange(DocumentResource.PublicarFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentResource.PublicarFormAction target) {}
    }
    
    private class NotificarActionExecutor implements ActionExecutor<DocumentResourceEntity, DocumentResource.NotificarFormAction, DocumentResource> {

		@Override
		public void onChange(NotificarFormAction previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, NotificarFormAction target) {
            switch (fieldName){
	            case DocumentResource.NotificarFormAction.Fields.duracio:
	                if (fieldValue != null) {
	                    Date dataLimit= DateUtils.addDays(new Date(), (Integer) fieldValue);
	                    if (previous.getDataCaducitat() == null || !DateUtils.isSameDay(previous.getDataCaducitat(), dataLimit)) {
	                        target.setDataCaducitat(dataLimit);
	                    }
	                } else {
	                    if (previous.getDataCaducitat()!=null) {
	                        target.setDataCaducitat(null);
	                    }
	                }
	                break;
	
	            case DocumentResource.NotificarFormAction.Fields.dataCaducitat:
	                if (fieldValue != null) {
	                    LocalDate start = LocalDate.now();
	                    LocalDate end = ((Date)fieldValue).toInstant()
	                            .atZone(ZoneId.systemDefault())
	                            .toLocalDate();
	                    int dias = (int) start.until(end, ChronoUnit.DAYS);
	
	                    if (!Objects.equals(previous.getDuracio(), dias)) {
	                        target.setDuracio(dias);
	                    }
	                } else {
	                    if (previous.getDuracio()!=null) {
	                        target.setDuracio(null);
	                    }
	                }
	                break;
	        }			
		}

		@Override
		public DocumentResource exec(String code, DocumentResourceEntity entity, NotificarFormAction params) throws ActionExecutionException {
        	try {
            	
	        	List<Long> interessatsIds = new ArrayList<Long>();
	        	boolean anyInteressatIsAdministracio = false;
	        	if (params.getInteressats()!=null) {
	        		for (ResourceReference<InteressatResource, Long> interessat: params.getInteressats()) {
	        			interessatsIds.add(interessat.getId());
	        			InteressatResourceEntity ie = interessatResourceRepository.findById(interessat.getId()).get();
	        			if (InteressatTipusEnum.InteressatAdministracioEntity.equals(ie.getTipus())) {
	        				anyInteressatIsAdministracio = true;
	        			}
	        			if (params.getEntregaPostal()!=null && params.getEntregaPostal().booleanValue() && !ie.adressaCompleta()) {
	        				throw new ActionExecutionException(ie.getClass(), ie.getId(), code, "notificacio.controller.reject.postal");
	        			}
	        		}
	        	}
	        	
	        	if (DocumentNotificacioTipusEnumDto.COMUNICACIO.equals(params.getTipus()) && 
	        		"application/zip".equals(entity.getFitxerContentType()) &&
	        		anyInteressatIsAdministracio) {
	        			throw new ActionExecutionException(entity.getClass(), entity.getId(), code, "notificacio.controller.reject.comunicacio.zip.administracio");
	        	}
	        	
	            String entitatActualCodi = configHelper.getEntitatActualCodi();
	            EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true, false);
	        	
	        	DocumentNotificacioDto notificacioDto = new DocumentNotificacioDto();
	        	notificacioDto.setTipus(params.getTipus());
	        	notificacioDto.setInteressatsIds(interessatsIds); //El helper notifica al representant si és necessari
	        	notificacioDto.setServeiTipusEnum(params.getServeiTipus());
	        	notificacioDto.setEntregaPostal(params.getEntregaPostal()!=null?params.getEntregaPostal().booleanValue():false);
	        	notificacioDto.setObservacions(params.getDescripcio());
	        	notificacioDto.setAssumpte(params.getConcepte());
				notificacioDto.setDataProgramada(params.getDataProgramada()); 
				notificacioDto.setRetard(params.getRetard());
				notificacioDto.setDataCaducitat(params.getDataCaducitat());
	        	
	        	documentNotificacioHelper.notificacioCreate(entitatEntity.getId(), entity.getId(), notificacioDto);

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/document/"+entity.getId()+"/notificar", e);
				throw new ActionExecutionException(DocumentResource.class, entity.getId(), code, e.getMessage());
			}
        	
        	return null;
		}
    }
    
    private class EnviarPortafirmesActionExecutor implements ActionExecutor<DocumentResourceEntity, DocumentResource.EnviarPortafirmesFormAction, DocumentResource> {

        @Override
        public DocumentResource exec(String code, DocumentResourceEntity entity, DocumentResource.EnviarPortafirmesFormAction params) throws ActionExecutionException {
        	/*if (permisosResourceHelper.comprovarPermisDocument(entity, ExtendedPermission.WRITE, true)) {
        		 documentResourceHelper.portafirmesEnviar(null, entity, code, null, null, code, null, null, null, null, code, false, false);
        	}*/
        	
        	Long entitatId  = entity.getEntitat().getId();
        	Long documentId = entity.getId();
        	String rolActual = ""; //TODO ha de arribar el controlador
        	
        	DocumentEntity document = documentHelper.comprovarDocument(
        			entitatId,
					documentId,
					false,
					true,
					false,
					false, 
					false, 
					rolActual);
        	
			/*firmaPortafirmesHelper.portafirmesEnviar(
					entitatId,
					document,
					params.getMotiu(),
					params.getPrioritat(),
					null,
					portafirmesFluxId,
					portafirmesResponsables,
					portafirmesSeqTipus,
					portafirmesFluxTipus,
					annexosIds,
					transaccioId,
					avisFirmaParcial,
					firmaParcial);*/
        	
        	return objectMappingHelper.newInstanceMap(entity, DocumentResource.class);
        }

        @Override
        public void onChange(DocumentResource.EnviarPortafirmesFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentResource.EnviarPortafirmesFormAction target) {
        	//S'està inicialitzant el formulari, posam els camps que corresponguin als seus valor per defecte 
        	if (fieldName==null) {
        	} else { //És un camp concret el que s'ha canviat
        	}
        }
    }
}