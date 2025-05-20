package es.caib.ripea.service.resourceservice;

import java.io.OutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import es.caib.plugins.arxiu.api.Document;
import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.resourceentity.ContingutResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.DocumentResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.InteressatResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaDocumentResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.DocumentResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.InteressatResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaDocumentResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.persistence.repository.ContingutRepository;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ContingutHelper;
import es.caib.ripea.service.helper.DocumentHelper;
import es.caib.ripea.service.helper.DocumentNotificacioHelper;
import es.caib.ripea.service.helper.EmailHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.helper.RolHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.base.model.FieldOption;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.ReportFileType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.ArxiuDetallDto;
import es.caib.ripea.service.intf.dto.CodiValorDto;
import es.caib.ripea.service.intf.dto.DigitalitzacioPerfilDto;
import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentPublicacioDto;
import es.caib.ripea.service.intf.dto.DocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentVersioDto;
import es.caib.ripea.service.intf.dto.FirmaResultatDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.InteressatTipusEnum;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaNodeDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.service.intf.dto.Resum;
import es.caib.ripea.service.intf.dto.SignatureInfoDto;
import es.caib.ripea.service.intf.dto.StatusEnumDto;
import es.caib.ripea.service.intf.model.DocumentResource;
import es.caib.ripea.service.intf.model.DocumentResource.FinalitzarFirmaSimple;
import es.caib.ripea.service.intf.model.DocumentResource.IniciarFirmaSimple;
import es.caib.ripea.service.intf.model.DocumentResource.NotificarDocumentsZipFormAction;
import es.caib.ripea.service.intf.model.DocumentResource.NotificarFormAction;
import es.caib.ripea.service.intf.model.DocumentResource.ParentPath;
import es.caib.ripea.service.intf.model.DocumentResource.UpdateTipusDocumentFormAction;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.model.MetaDocumentResource;
import es.caib.ripea.service.intf.model.NodeResource.MassiveAction;
import es.caib.ripea.service.intf.model.UsuariResource;
import es.caib.ripea.service.intf.resourceservice.DocumentResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.resourcehelper.CacheResourceHelper;
import es.caib.ripea.service.resourcehelper.ContingutResourceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentResourceServiceImpl extends BaseMutableResourceService<DocumentResource, Long, DocumentResourceEntity> implements DocumentResourceService {

    private final ContingutResourceHelper contingutResourceHelper;
    private final PluginHelper pluginHelper;
    private final ConfigHelper configHelper;
    private final EmailHelper emailHelper;
    private final DocumentHelper documentHelper;
    private final ContingutHelper contingutHelper;
    private final ExcepcioLogHelper excepcioLogHelper;
    private final DocumentNotificacioHelper documentNotificacioHelper;
    private final EntityComprovarHelper entityComprovarHelper;
    private final CacheResourceHelper cacheResourceHelper;
    private final RolHelper rolHelper;

    private final UsuariResourceRepository usuariResourceRepository;
    private final DocumentResourceRepository documentResourceRepository;
    private final MetaDocumentResourceRepository metaDocumentResourceRepository;
    private final InteressatResourceRepository interessatResourceRepository;
    private final ContingutRepository contingutRepository;
    private final DocumentRepository documentRepository;

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
        register(DocumentResource.ACTION_MOURE_CODE, new MoureCopiarVincularActionExecutor());
        register(DocumentResource.ACTION_PUBLICAR_CODE, new PublicarActionExecutor());
        register(DocumentResource.ACTION_NOTIFICAR_CODE, new NotificarActionExecutor());
        register(DocumentResource.ACTION_ENVIAR_PORTAFIRMES_CODE, new EnviarPortafirmesActionExecutor());
        register(DocumentResource.ACTION_RESUM_IA, new ResumIaActionExecutor());
        //Accions massives desde la pipella de contingut
        register(DocumentResource.ACTION_DESCARREGAR_MASSIU, new DescarregarDocumentsMassiuZipGenerator());
        register(DocumentResource.ACTION_MASSIVE_NOTIFICAR_ZIP_CODE, new NotificarDocumentsZipActionExecutor());
        register(DocumentResource.ACTION_MASSIVE_CANVI_TIPUS_CODE, new CanviTipusDocumentsActionExecutor());
        register(DocumentResource.ACTION_GET_CSV_LINK, new CsvLinkActionExecutor());
        register(DocumentResource.ACTION_FIRMA_WEB_INI, new IniciarFirmaWebActionExecutor());
        register(DocumentResource.ACTION_FIRMA_WEB_FIN, new FinalitzarFirmaWebActionExecutor());
        //Dades externes
        register(DocumentResource.EnviarPortafirmesFormAction.Fields.portafirmesEnviarFluxId, new FluxosFirmaFieldOptionsProvider());
        register(DocumentResource.Fields.digitalitzacioPerfil, new PerfilsDigitalitzacioOptionsProvider());
        register(DocumentResource.Fields.digitalitzacioPerfil, new DigitalitzacioPerfilOnchangeLogicProcessor());
        register(null, new InitialOnChangeDocumentResourceLogicProcessor());
    }
    
    public class InitialOnChangeDocumentResourceLogicProcessor implements OnChangeLogicProcessor<DocumentResource> {
		@Override
		public void onChange(Serializable id, DocumentResource previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, DocumentResource target) {
			//Camps transient per inicialitzar al carregar el formulari
	        target.setPluginSummarizeActiu(Utils.hasValue(configHelper.getConfig(PropertyConfig.SUMMARIZE_PLUGIN_CLASS)));
	        target.setFuncionariHabilitatDigitalib(rolHelper.doesCurrentUserHasRol("DIB_USER"));
		}
    }
    
    public class PerfilsDigitalitzacioOptionsProvider implements FieldOptionsProvider {
		public List<FieldOption> getOptions(String fieldName, Map<String,String[]> requestParameterMap) {
			List<DigitalitzacioPerfilDto> fluxosDto = pluginHelper.digitalitzacioPerfilsDisponibles();
			List<FieldOption> resultat = new ArrayList<FieldOption>();
			if (fluxosDto!=null) {
				for (DigitalitzacioPerfilDto flx: fluxosDto) {
					resultat.add(new FieldOption(flx.getCodi(), flx.getNom()));
				}
			}
			return resultat;
		}
	}
    
    public class FluxosFirmaFieldOptionsProvider implements FieldOptionsProvider {
		public List<FieldOption> getOptions(String fieldName, Map<String,String[]> requestParameterMap) {
			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), true, false, false, false, false);
			List<PortafirmesFluxRespostaDto> fluxosDto = pluginHelper.portafirmesRecuperarPlantillesDisponibles(entitatEntity.getId(), false);
			List<FieldOption> resultat = new ArrayList<FieldOption>();
			if (fluxosDto!=null) {
				for (PortafirmesFluxRespostaDto flx: fluxosDto) {
					resultat.add(new FieldOption(flx.getFluxId(), flx.getNom()));
				}
			}
			return resultat;
		}
	}
    
    @Override
    public DocumentResource create(DocumentResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
    	try {
    		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
    		//TODO: el padre no siempre es el expediente, puede ser una carpeta
    		ContingutEntity pare = contingutRepository.findById(resource.getExpedient().getId()).get();
    		DocumentDto documentCreat = documentHelper.crearDocument(
    				entitatEntity.getId(),
                    resource.toDocumentDto(),
    				pare,
    				true,
    				false);
    		resource.setId(documentCreat.getId());
    		return resource;
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/document/"+resource.getId()+"/create", ex);
    	}
    	return null;
    }
    
    @Override
	public DocumentResource update(Long id, DocumentResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
    	try {
    		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
    		DocumentEntity documentActual = documentRepository.findById(resource.getId()).get();
    		DocumentDto documentCreat = documentHelper.updateDocument(
    				entitatEntity.getId(),
    				documentActual,
                    resource.toDocumentDto(),
    				true);
    		resource.setId(documentCreat.getId());
    		return resource;
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/document/"+resource.getId()+"/create", ex);
    	}
    	return null;
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
        resource.setErrors(cacheResourceHelper.findErrorsValidacioPerNode(entity));
        resource.setValid(resource.getErrors().isEmpty());

        resource.setAmbNotificacions(!entity.getNotificacions().isEmpty());
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
                ParentPath pathEntry = new ParentPath(
                        entity.getId(),
                        entity.getNom(),
                        entity.getCreatedBy(),
                        entity.getCreatedDate(),
                        entity.getTipus(),
                        new ArrayList<>()
                );
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
        public DownloadableFile download(DocumentResourceEntity entity, String fieldName, OutputStream out) {
        	
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
        public DownloadableFile download(DocumentResourceEntity entity, String fieldName, OutputStream out) {
        	
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
        public DownloadableFile download(DocumentResourceEntity entity, String fieldName, OutputStream out) {
        	
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
        public DownloadableFile download(DocumentResourceEntity entity, String fieldName, OutputStream out) {
        	
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

    private class DigitalitzacioPerfilOnchangeLogicProcessor implements OnChangeLogicProcessor<DocumentResource> {
        @Override
        public void onChange(Serializable id, DocumentResource previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentResource target) {
            if (fieldValue != null) {
                target.setFuncionariHabilitatDigitalib(rolHelper.doesCurrentUserHasRol("DIB_USER"));
            } else {
            	//TODO iniciar procés de escaneig
            }
        }
    }
    
    private class MetaDocumentOnchangeLogicProcessor implements OnChangeLogicProcessor<DocumentResource> {
        @Override
        public void onChange(Serializable id, DocumentResource previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentResource target) {
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
        public void onChange(Serializable id, DocumentResource previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentResource target) {

            if (fieldValue != null) {
                FileReference adjunt = (FileReference) fieldValue;

                target.setFitxerNom(adjunt.getName());
                target.setFitxerContingut(adjunt.getContent());
                target.setFitxerTamany(adjunt.getContentLength());
                target.setFitxerContentType(adjunt.getContentType());

                if (Boolean.parseBoolean(configHelper.getConfig(PropertyConfig.DETECCIO_FIRMA_AUTOMATICA))) {
                	
                	SignatureInfoDto signatureInfoDto = pluginHelper.detectaFirmaDocument(adjunt.getContent(), adjunt.getContentType());

                    target.setHasFirma(signatureInfoDto.isSigned());
                    target.setValidacioFirmaCorrecte(!signatureInfoDto.isError());
                    target.setValidacioFirmaErrorMsg(signatureInfoDto.getErrorMsg());

                    if (signatureInfoDto.isSigned()) {
                        target.setDocumentFirmaTipus(DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA);
                        if (signatureInfoDto.isError() && !answers.containsKey(ERROR_SIGNATURE_VALIDATION)) {
                            throw new AnswerRequiredException(DocumentResource.class, ERROR_SIGNATURE_VALIDATION, signatureInfoDto.getErrorMsg());
                        }
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
        public void onChange(Serializable id, DocumentResource previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentResource target) {

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
        public void onChange(Serializable id, DocumentResource previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentResource target) {

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
        public void onChange(Serializable id, DocumentResource.EnviarViaEmailFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentResource.EnviarViaEmailFormAction target) {

        }
    }
    private class CanviTipusDocumentsActionExecutor implements ActionExecutor<DocumentResourceEntity, DocumentResource.UpdateTipusDocumentFormAction, DocumentResource> {

		@Override
		public void onChange(Serializable id, UpdateTipusDocumentFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, UpdateTipusDocumentFormAction target) {}

		@Override
		public DocumentResource exec(String code, DocumentResourceEntity entity, UpdateTipusDocumentFormAction params) throws ActionExecutionException {
    		try {	    		
	    		
    			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
    			
                if (params!=null) {
                	for (Long id: params.getIds()) {
                		DocumentEntity  document = documentHelper.comprovarDocument(entitatEntity.getId(), id, false, true, false, false, false, configHelper.getRolActual());
            			documentHelper.updateTipusDocumentDocument(
            					entitatEntity.getId(),
            					document,
            					params.getMetaDocument().getId(),
            					false);
                	}
                }
    			return null;
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/CanviTipusDocumentsActionExecutor", e);
				throw new ReportGenerationException(DocumentResource.class, null, code, "S'ha produit un error al canviar el tipus dels documents seleccionats.");
			}
		}
    }
    private class DescarregarDocumentsMassiuZipGenerator implements ReportGenerator<DocumentResourceEntity, DocumentResource.MassiveAction, Serializable> {

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}
		
		@Override
		public List<Serializable> generateData(String code, DocumentResourceEntity entity, MassiveAction params) throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
			parametres.add(entity!=null?entity.getId():0l);
			parametres.add(params);
			return parametres;
		}

		@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {

    		DownloadableFile resultat = null;
    		Long expedientId = data.get(0)!=null?(Long)data.get(0):null;

    		try {

	    		ExpedientResource.MassiveAction params = (ExpedientResource.MassiveAction)data.get(1);
	    		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
        		FitxerDto fitxerDto = documentHelper.getZipFromDocumentsIds(entitatEntity.getId(), params.getIds());
            	resultat = new DownloadableFile(
            			fitxerDto.getNom(),
            			fitxerDto.getContentType(),
	            		fitxerDto.getContingut());

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/"+expedientId+"/descarregarDocumentsMassiuZip", e);
				throw new ReportGenerationException(ExpedientResource.class, expedientId, code, "S'ha produit un error al descarregar els documents seleccionats.");
			}

            return resultat;
		}



    }
    private class NotificarDocumentsZipActionExecutor implements ActionExecutor<DocumentResourceEntity, DocumentResource.NotificarDocumentsZipFormAction, DocumentResource> {

		@Override
		public void onChange(Serializable id, NotificarDocumentsZipFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, NotificarDocumentsZipFormAction target) {
            if (NotificarDocumentsZipFormAction.Fields.metaDocument.equals(fieldName)) {
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

		@Override
		public DocumentResource exec(String code, DocumentResourceEntity entity, NotificarDocumentsZipFormAction params) throws ActionExecutionException {
    		try {	    		
	    		
	    		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
        		FitxerDto fitxerDto = documentHelper.getZipFromDocumentsIds(entitatEntity.getId(), params.getIds());
        		DocumentResourceEntity newZipFile = new DocumentResourceEntity();
        		//TODO
//        		ContingutEntity pare = contingutRepository.findById(resource.getExpedient().getId()).get();        		
        		DocumentDto documentDto = new DocumentDto();
            	MetaNodeDto metaNode = new MetaNodeDto();
            	metaNode.setId(params.getMetaDocument().getId());
            	documentDto.setMetaNode(metaNode);
            	documentDto.setPareId(null); //TODO
            	documentDto.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
            	documentDto.setNom(fitxerDto.getNom());
            	documentDto.setData(Calendar.getInstance().getTime());
            	documentDto.setNtiOrigen(params.getNtiOrigen());
            	documentDto.setNtiEstadoElaboracion(params.getNtiEstadoElaboracion());
            	documentDto.setFitxerContingut(fitxerDto.getContingut());
            	documentDto.setFitxerContentType(fitxerDto.getContentType());
            	documentDto.setFitxerTamany((long)fitxerDto.getContentType().length());
            	documentDto.setAmbFirma(false);
            	documentDto.setData(Calendar.getInstance().getTime());
            	//TODO falta passar-li el pare
            	documentDto = documentHelper.crearDocument(entitatEntity.getId(), documentDto, null, true, false);        		
        		newZipFile.setId(documentDto.getId());
        		return objectMappingHelper.newInstanceMap(newZipFile, DocumentResource.class);
        		
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/NotificarDocumentsZipActionExecutor", e);
				throw new ReportGenerationException(DocumentResource.class, null, code, "S'ha produit un error al guardar el ZIP per notificar per els documents seleccionats.");
			}
		}
    }
    private class MoureCopiarVincularActionExecutor implements ActionExecutor<DocumentResourceEntity, DocumentResource.MoureFormAction, DocumentResource> {

        @Override
        public DocumentResource exec(String code, DocumentResourceEntity entity, DocumentResource.MoureFormAction params) throws ActionExecutionException {
        	
        	if (params!=null && params.getIds()!=null && params.getIds().size()>0) {
        		try {
	        		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
	    			for (Long contingutOrigenId: params.getIds()) {
	    				Long contingutDestiId = params.getCarpeta()!=null?params.getCarpeta().getId():params.getExpedient().getId();
	    				switch (params.getAction()) {
						case MOURE:
							contingutHelper.move(entitatEntity.getId(), contingutOrigenId, contingutDestiId,configHelper.getRolActual());							
							break;
						case COPIAR:
							contingutHelper.copy(entitatEntity.getId(), contingutOrigenId, contingutDestiId, false); //No recursiu
							break;
						case VINCULAR:
							//Recursiu igual que a ContingutController.vincular (POST)
							contingutHelper.link(entitatEntity.getId(), contingutOrigenId, contingutDestiId, true);
							break;
						default:
							break;
						}
	    			}
    			} catch (Exception e) {
    				excepcioLogHelper.addExcepcio("/expedient/MoureActionExecutor", e);
    				throw new ReportGenerationException(DocumentResource.class, null, code, "S'ha produit un error al mourer o copiar els documents seleccionats.");
    			}	    			
        	} else {
        		throw new ActionExecutionException(getResourceClass(), null, code, "No s'ha indicat cap element per realitzar l'acció.");
        	}

            return null;
        }

        @Override
        public void onChange(Serializable id, DocumentResource.MoureFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentResource.MoureFormAction target) {}
    }
    
    private class CsvLinkActionExecutor implements ActionExecutor<DocumentResourceEntity, Serializable, String> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

		@Override
		public String exec(String code, DocumentResourceEntity entity, Serializable params) throws ActionExecutionException {
			try {
				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
				return documentHelper.getEnllacCsv(entitatEntity.getId(), entity.getId());
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/document/CsvLinkActionExecutor", e);
				return "";
			}
		}
    }

    private class IniciarFirmaWebActionExecutor implements ActionExecutor<DocumentResourceEntity, DocumentResource.IniciarFirmaSimple, String> {

		@Override
		public void onChange(Serializable id, IniciarFirmaSimple previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, IniciarFirmaSimple target) {
			//initialOnChange --> Carregar un valor per defecte per el motiu
			if (fieldName==null) {
				String expNom = documentResourceRepository.findById(((Integer)id).longValue()).get().getExpedient().getNom();
				target.setMotiu("Tramitació del expedient RIPEA: "+expNom);
			}
		}

		@Override
		public String exec(String code, DocumentResourceEntity entity, IniciarFirmaSimple params) throws ActionExecutionException {
			try {
				String urlReturnToRipea = configHelper.getConfig(PropertyConfig.BASE_URL) + "/document/" + entity.getId() + "/firmaSimpleWebEnd";
				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
				FitxerDto fitxerDto = documentHelper.convertirPdfPerFirmaClient(entitatEntity.getId(), entity.getId());
				return pluginHelper.firmaSimpleWebStart(Arrays.asList(fitxerDto), params.getMotiu(), urlReturnToRipea);
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/document/"+entity.getId()+"/IniciarFirmaWebActionExecutor", e);
				return null;
			}
		}
    }    
    
    private class FinalitzarFirmaWebActionExecutor implements ActionExecutor<DocumentResourceEntity, DocumentResource.FinalitzarFirmaSimple, CodiValorDto> {

		@Override
		public void onChange(Serializable id, FinalitzarFirmaSimple previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, FinalitzarFirmaSimple target) {}

		@Override
		public CodiValorDto exec(String code, DocumentResourceEntity entity, FinalitzarFirmaSimple params) throws ActionExecutionException {
			try {
				FirmaResultatDto firmaResultat = pluginHelper.firmaSimpleWebEnd(params.getTransactionId());
				if (StatusEnumDto.OK.equals(firmaResultat.getStatus())) {

					if (StatusEnumDto.OK.equals(firmaResultat.getSignatures().get(0).getStatus())) {
						EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
						documentHelper.processarFirmaClient(
								entitatEntity.getId(),
								entity.getId(),
								firmaResultat.getSignatures().get(0).getFitxerFirmatNom(),
								firmaResultat.getSignatures().get(0).getFitxerFirmatContingut(),
								configHelper.getRolActual(),
								null);
						return new CodiValorDto("OK", "document.controller.firma.passarela.final.ok");
					} else {
						return new CodiValorDto("ERROR", firmaResultat.getSignatures().get(0).getMsg());
					}
				} else if (firmaResultat.getStatus() == StatusEnumDto.WARNING) {
					return new CodiValorDto("WARNING", firmaResultat.getMsg());
				} else if (firmaResultat.getStatus() == StatusEnumDto.ERROR) {
					return new CodiValorDto("ERROR", firmaResultat.getMsg());
				}
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/document/"+entity.getId()+"/FinalitzarFirmaWebActionExecutor", e);
			}
			return null;
		}
    }
    
    private class ResumIaActionExecutor implements ActionExecutor<DocumentResourceEntity, DocumentResource.ResumIaFormAction, Resum> {

        @Override
        public Resum exec(String code, DocumentResourceEntity entity, DocumentResource.ResumIaFormAction params) throws ActionExecutionException {
        	try {
        		return pluginHelper.getSummarize(params.getAdjunt().getContent(), params.getAdjunt().getContentType());
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/document/ResumIaActionExecutor", e);
				return new Resum();
			}
        }

        @Override
        public void onChange(Serializable id, DocumentResource.ResumIaFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentResource.ResumIaFormAction target) {}
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
        public void onChange(Serializable id, DocumentResource.PublicarFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentResource.PublicarFormAction target) {}
    }
    
    private class NotificarActionExecutor implements ActionExecutor<DocumentResourceEntity, DocumentResource.NotificarFormAction, DocumentResource> {

		@Override
		public void onChange(Serializable id, NotificarFormAction previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, NotificarFormAction target) {
            if (fieldName==null){
                target.setPermetreEnviamentPostal(ConfigHelper.getEntitat().get().isPermetreEnviamentPostal());
            }

            if (fieldName!=null) {
                switch (fieldName) {
                    case DocumentResource.NotificarFormAction.Fields.duracio:
                        if (fieldValue != null) {
                            Date dataLimit = DateUtils.addDays(new Date(), (Integer) fieldValue);
                            if (previous.getDataCaducitat() == null || !DateUtils.isSameDay(previous.getDataCaducitat(), dataLimit)) {
                                target.setDataCaducitat(dataLimit);
                            }
                        } else {
                            if (previous.getDataCaducitat() != null) {
                                target.setDataCaducitat(null);
                            }
                        }
                        break;

                    case DocumentResource.NotificarFormAction.Fields.dataCaducitat:
                        if (fieldValue != null) {
                            LocalDate start = LocalDate.now();
                            LocalDate end = ((Date) fieldValue).toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            int dias = (int) start.until(end, ChronoUnit.DAYS);

                            if (!Objects.equals(previous.getDuracio(), dias)) {
                                target.setDuracio(dias);
                            }
                        } else {
                            if (previous.getDuracio() != null) {
                                target.setDuracio(null);
                            }
                        }
                        break;
                }
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
        	String rolActual = configHelper.getRolActual();
        	
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
        public void onChange(Serializable id, DocumentResource.EnviarPortafirmesFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentResource.EnviarPortafirmesFormAction target) {
        	
        	//S'està inicialitzant el formulari, posam els camps que corresponguin als seus valor per defecte 
        	if (fieldName==null) {
        		target.setMostrarFirmaParcial(configHelper.getAsBoolean(PropertyConfig.FIRMA_PARCIAL));
        		target.setMostrarAvisFirmaParcial(configHelper.getAsBoolean(PropertyConfig.AVIS_FIRMA_PARCIAL));
        		
        		MetaDocumentResourceEntity metaDocumentResourceEntity = documentResourceRepository.findById(((Integer)id).longValue()).get().getMetaDocument();
        		target.setPortafirmesFluxTipus(metaDocumentResourceEntity.getPortafirmesFluxTipus());
        		
        		if (MetaDocumentFirmaFluxTipusEnumDto.SIMPLE.equals(metaDocumentResourceEntity.getPortafirmesFluxTipus())) {
        			List<ResourceReference<UsuariResource, String>> responsables = new ArrayList<>();
        			if (metaDocumentResourceEntity.getPortafirmesResponsables()!=null) {
        				String[] pfResponsables = metaDocumentResourceEntity.getPortafirmesResponsables().split(",");
                        for (String codi : pfResponsables) {
                            UsuariResourceEntity usuariEntity = usuariResourceRepository.findById(codi).orElse(null);
                            if (usuariEntity != null) {
                                responsables.add(ResourceReference.toResourceReference(usuariEntity.getCodi(), usuariEntity.getNom()));
                            } else {
                                responsables.add(ResourceReference.toResourceReference(codi, codi));
                            }
                        }
                    }
        			target.setResponsables(responsables);
        		} else {
        			target.setPortafirmesEnviarFluxId(metaDocumentResourceEntity.getPortafirmesFluxId());
        		}
        		
        	} else { //És un camp concret el que s'ha canviat
        		if (DocumentResource.EnviarPortafirmesFormAction.Fields.portafirmesEnviarFluxId.equals(fieldName)) {
        			String idiomaUsuari = usuariResourceRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName()).get().getIdioma();
        			target.setPortafirmesFluxUrl(pluginHelper.portafirmesRecuperarUrlPlantilla(
        					fieldValue.toString(), 
        					idiomaUsuari!=null?idiomaUsuari:"ca",
        					null,
        					false));
        		}
        	}
        }
    }
}