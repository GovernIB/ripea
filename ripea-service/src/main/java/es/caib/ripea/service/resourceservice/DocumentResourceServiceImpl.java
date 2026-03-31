package es.caib.ripea.service.resourceservice;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleStartTransactionRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.plugins.arxiu.api.Document;
import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.ViaFirmaUsuariEntity;
import es.caib.ripea.persistence.entity.resourceentity.DocumentResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.InteressatGrupResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.InteressatResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaDocumentResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.RegistreAnnexResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.ContingutResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.DocumentResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.InteressatGrupResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.InteressatResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaDocumentResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.RegistreAnnexResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.persistence.repository.ContingutMovimentRepository;
import es.caib.ripea.persistence.repository.ContingutRepository;
import es.caib.ripea.persistence.repository.DocumentNotificacioRepository;
import es.caib.ripea.persistence.repository.DocumentPortafirmesRepository;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.firma.DocumentFirmaPortafirmesHelper;
import es.caib.ripea.service.firma.DocumentFirmaViaFirmaHelper;
import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ContingutHelper;
import es.caib.ripea.service.helper.DocumentHelper;
import es.caib.ripea.service.helper.DocumentNotificacioHelper;
import es.caib.ripea.service.helper.EmailHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.EventHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.ExecucioMassivaHelper;
import es.caib.ripea.service.helper.ExpedientHelper;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.helper.MetaExpedientHelper;
import es.caib.ripea.service.helper.PinbalHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.helper.RolHelper;
import es.caib.ripea.service.helper.UsuariHelper;
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
import es.caib.ripea.service.intf.dto.ArxiuEstatEnumDto;
import es.caib.ripea.service.intf.dto.ArxiuFirmaDto;
import es.caib.ripea.service.intf.dto.DigitalitzacioPerfilDto;
import es.caib.ripea.service.intf.dto.DigitalitzacioTransaccioRespostaDto;
import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.DocumentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentPublicacioDto;
import es.caib.ripea.service.intf.dto.DocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentVersioDto;
import es.caib.ripea.service.intf.dto.ElementTipusEnumDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaContingutDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaTipusDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.InteressatTipusEnum;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaNodeDto;
import es.caib.ripea.service.intf.dto.MunicipiDto;
import es.caib.ripea.service.intf.dto.PaisDto;
import es.caib.ripea.service.intf.dto.PinbalConsultaDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.service.intf.dto.PortafirmesIniciFluxRespostaDto;
import es.caib.ripea.service.intf.dto.Resum;
import es.caib.ripea.service.intf.dto.SignatureInfoDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.dto.VersioDocumentEnum;
import es.caib.ripea.service.intf.dto.ViaFirmaDispositiuDto;
import es.caib.ripea.service.intf.dto.ViaFirmaEnviarDto;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.intf.model.ContingutResource;
import es.caib.ripea.service.intf.model.DocumentResource;
import es.caib.ripea.service.intf.model.DocumentResource.IniciarFirmaNavegador;
import es.caib.ripea.service.intf.model.DocumentResource.NewDocPinbalForm;
import es.caib.ripea.service.intf.model.DocumentResource.NotificarDocumentsZipFormAction;
import es.caib.ripea.service.intf.model.DocumentResource.NotificarFormAction;
import es.caib.ripea.service.intf.model.DocumentResource.UpdateTipusDocumentFormAction;
import es.caib.ripea.service.intf.model.DocumentResource.ViaFirmaForm;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.model.InteressatGrupResource;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.model.MetaDocumentResource;
import es.caib.ripea.service.intf.model.NodeResource.MassiveAction;
import es.caib.ripea.service.intf.model.UsuariResource;
import es.caib.ripea.service.intf.model.sse.ErrorsValidacioChangedEvent;
import es.caib.ripea.service.intf.resourceservice.DocumentResourceService;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.utils.Utils;
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
    private final PinbalHelper pinbalHelper;
    private final EmailHelper emailHelper;
    private final ExpedientHelper expedientHelper;
    private final CacheHelper cacheHelper;
    private final EventHelper eventHelper;
    private final DocumentHelper documentHelper;
    private final ContingutHelper contingutHelper;
    private final ExcepcioLogHelper excepcioLogHelper;
    private final DocumentNotificacioHelper documentNotificacioHelper;
    private final EntityComprovarHelper entityComprovarHelper;
	private final DocumentFirmaPortafirmesHelper firmaPortafirmesHelper;
	private final DocumentFirmaViaFirmaHelper firmaViaFirmaHelper;
	private final UsuariHelper usuariHelper;
	private final MessageHelper messageHelper;
	private final MetaExpedientHelper metaExpedientHelper;
	private final ExecucioMassivaHelper execucioMassivaHelper;
    private final AplicacioService aplicacioService;

    private final UsuariResourceRepository usuariResourceRepository;
	private final ContingutResourceRepository contingutResourceRepository;
    private final DocumentResourceRepository documentResourceRepository;
    private final MetaDocumentResourceRepository metaDocumentResourceRepository;
    private final InteressatResourceRepository interessatResourceRepository;
    private final RegistreAnnexResourceRepository registreAnnexResourceRepository;
    private final ContingutMovimentRepository contingutMovimentRepository;
    private final DocumentNotificacioRepository documentNotificacioRepository;
    private final DocumentPortafirmesRepository documentPortafirmesRepository;
    private final ContingutRepository contingutRepository;
    private final DocumentRepository documentRepository;
    private final EntitatRepository entitatRepository;
    private final InteressatGrupResourceRepository interessatGrupResourceRepository;

    @PostConstruct
    public void init() {
        register(DocumentResource.PERSPECTIVE_COUNT_CODE, new CountPerspectiveApplicator());
        register(DocumentResource.PERSPECTIVE_VERSIONS_CODE, new ArxiuVersionsPerspectiveApplicator());
        register(DocumentResource.PERSPECTIVE_ARXIU_DOCUMENT_CODE, new ArxiuDocumentPerspectiveApplicator());
        register(DocumentResource.PERSPECTIVE_PATH_CODE, new PathPerspectiveApplicator());
        register(DocumentResource.PERSPECTIVE_FIRMES_CODE, new FirmesPerspectiveApplicator());
        register(DocumentResource.PERSPECTIVE_PROCEDIMENT_CODE, new ProcedimentPerspectiveApplicator());
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
        register(DocumentResource.REPORT_DESCARREGAR_MASSIU, new DescarregarDocumentsMassiuZipGenerator());
        register(DocumentResource.ACTION_MASSIVE_NOTIFICAR_ZIP_CODE, new NotificarDocumentsZipActionExecutor());
        register(DocumentResource.ACTION_MASSIVE_CANVI_TIPUS_CODE, new CanviTipusDocumentsActionExecutor());
        register(DocumentResource.ACTION_GET_CSV_LINK, new CsvLinkActionExecutor());
        register(DocumentResource.ACTION_CONVERTIR_DEFINITIU, new ConvertirDefinitiuActionExecutor());
        register(DocumentResource.ACTION_GUARDAR_ARXIU, new GuardarArxiuActionExecutor());
        //Flux de firma, firma en navegador, document PINBAL, viaFirma (formularis modals)
        register(DocumentResource.ACTION_FIRMA_WEB_INI, new IniciarFirmaWebActionExecutor());
        register(DocumentResource.ACTION_NEW_DOC_PINBAL, new NouDocumentPinbalActionExecutor());
        register(DocumentResource.ACTION_VIA_FIRMA, new ViaFirmaActionExecutor());
        register(DocumentResource.REPORT_DESCARREGAR_VERSIO_CODE, new DescarregarVersionReportGenerator());
        //Dades externes
        register(DocumentResource.Fields.digitalitzacioPerfil, new PerfilsDigitalitzacioOptionsProvider());
        register(DocumentResource.Fields.digitalitzacioPerfil, new DigitalitzacioPerfilOnchangeLogicProcessor());
        register(DocumentResource.ViaFirmaForm.Fields.viaFirmaDispositiuCodi, new ViaFirmaDispositiuOptionsProvider());
        register(null, new InitialOnChangeDocumentResourceLogicProcessor());
    }
    
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
        String entitatActualCodi = configHelper.getEntitatActualCodi();
        String rolActual		 = configHelper.getRolActual();
    	EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);

    	Filter filtreUsuari = (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null;
        Filter filtreBase = FilterBuilder.and(
        		filtreUsuari,
                FilterBuilder.equal(ContingutResource.Fields.entitat + "." + EntitatResource.Fields.codi,
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
        );
        
        //Filtres opcionals, dependran de la namedQuery
        Filter filtreMetaExpedientsPermesos = null;
        Filter filtreDocumentsNotArxiuIds = null;
        Filter filtreEstatDocument = null;
        Filter filtrePfActiu = null;
        Filter filtreExpedientObert = null;
        Filter filtreNoAdjunt = null;
        Filter filtreArxiuPendents = null;
        Filter filtreNoEsborrat = null;
        Filter filtreTipusDoc = null;

        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
    	if (mapaNamedQueries.size()>0) {
    		
    		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientHelper.findPermesosAccioMassiva(entitat.getId(), rolActual);
    		boolean nomesAgafats = !rolActual.equals("IPA_ADMIN") && !rolActual.equals("IPA_ORGAN_ADMIN");
    		String codiUsuariActual = SecurityContextHolder.getContext().getAuthentication().getName();
    		
    		if (metaExpedientsPermesos==null || metaExpedientsPermesos.size()==0) {
				//Sense permisos
				return FilterBuilder.equal("id", 0).generate();
			}
    		
    		if (mapaNamedQueries.containsKey("MASSIU_PORTAFIRMES") || 
    			mapaNamedQueries.containsKey("MASSIU_PASARELA") ||
    			mapaNamedQueries.containsKey("MASSIU_PENDENT_ARXIU") ||
    			mapaNamedQueries.containsKey("MASSIU_ENLLAC_CSV")) {
	    			
    			if (mapaNamedQueries.containsKey("MASSIU_PENDENT_ARXIU")) {
        			
        			List<Long> idsArxiusPendents = documentRepository.findIdsArxiuPendents(
        					entitat,
        					metaExpedientsPermesos,
        					nomesAgafats,
        					codiUsuariActual,
        					true, null,
        					true, null,
        					true, null,
        					true, null,
        					true, null);
        			
    		    	List<String> permesosClausulesIn = Utils.getIdsEnGruposMil(idsArxiusPendents);
    		    	if (permesosClausulesIn!=null) {
	    		        for (String aux: permesosClausulesIn) {
	    			        if (aux != null && !aux.isEmpty()) {
	    			        	filtreDocumentsNotArxiuIds = FilterBuilder.or(filtreDocumentsNotArxiuIds, Filter.parse("id IN (" + aux + ")"));
	    			        }
	    		        }
    		    	}
        		} else {    			
    			
					List<Long> metaExpedientsPermesosIds = new ArrayList<Long>();			
	    			
					for (MetaExpedientEntity mex: metaExpedientsPermesos) {
						metaExpedientsPermesosIds.add(mex.getId());
					}
					
	    			String procedimentId = DocumentResource.Fields.expedient + "." + ExpedientResource.Fields.metaExpedient + ".id";
			    	List<String> permesosClausulesIn = Utils.getIdsEnGruposMil(metaExpedientsPermesosIds);
			        for (String aux: permesosClausulesIn) {
				        if (aux != null && !aux.isEmpty()) {
				        	filtreMetaExpedientsPermesos = FilterBuilder.or(filtreMetaExpedientsPermesos, Filter.parse(procedimentId + " IN (" + aux + ")"));
				        }
			        }
	
			        String documentEsborratField = ContingutResource.Fields.esborrat;
			        filtreNoEsborrat = FilterBuilder.equal(documentEsborratField, 0); //NO BORRAT
			        
			        String documentTipusField = DocumentResource.Fields.documentTipus;
			        filtreTipusDoc = FilterBuilder.equal(documentTipusField, DocumentTipusEnumDto.DIGITAL.toString()); //DIGITAL
			        
			        if (mapaNamedQueries.containsKey("MASSIU_ENLLAC_CSV")) {
			        	String documentEstatField = DocumentResource.Fields.estat;
			        	filtreEstatDocument = FilterBuilder.or(
			        		FilterBuilder.equal(documentEstatField, DocumentEstatEnumDto.FIRMAT.toString()),
			        		FilterBuilder.equal(documentEstatField, DocumentEstatEnumDto.CUSTODIAT.toString()),
			        		FilterBuilder.equal(documentEstatField, DocumentEstatEnumDto.DEFINITIU.toString())
	    				);
	
				        String docAdjuntField = DocumentResource.Fields.gesDocAdjuntId;
				        filtreNoAdjunt = FilterBuilder.isNull(docAdjuntField);
	
			        } else {
	
				        String documentEstatField = DocumentResource.Fields.estat;
				        filtreEstatDocument = FilterBuilder.equal(documentEstatField, DocumentEstatEnumDto.REDACCIO.toString()); //ESBORRANY
	
				        if (mapaNamedQueries.containsKey("MASSIU_PORTAFIRMES")) {
				        	String metaDocPortafirmes = DocumentResource.Fields.metaDocument + "." + MetaDocumentResource.Fields.firmaPortafirmesActiva;
				        	filtrePfActiu = FilterBuilder.equal(metaDocPortafirmes, true); //ENVIAMENT A PF ACTIU EN EL PROCEDIMENT
					        String docAdjuntField = DocumentResource.Fields.gesDocAdjuntId;
					        filtreNoAdjunt = FilterBuilder.isNull(docAdjuntField);
				        } else if (mapaNamedQueries.containsKey("MASSIU_PASARELA")) {
				        	String metaDocPortafirmes = DocumentResource.Fields.metaDocument + "." + MetaDocumentResource.Fields.firmaPassarelaActiva;
				        	filtrePfActiu = FilterBuilder.equal(metaDocPortafirmes, true); //ENVIAMENT A PF ACTIU EN EL PROCEDIMENT
					        String docAdjuntField = DocumentResource.Fields.gesDocAdjuntId;
					        filtreNoAdjunt = FilterBuilder.isNull(docAdjuntField);
				        }
			        }
        		}
		        
		        Filter resultat = FilterBuilder.and(
		        		filtreBase, //Entitat i filtre del usuari
		        		filtreMetaExpedientsPermesos,
		        		filtreDocumentsNotArxiuIds,
		        		filtreEstatDocument,
		        		filtreNoEsborrat,
		        		filtreNoAdjunt,
		        		filtreTipusDoc,
		        		filtrePfActiu,
		        		filtreExpedientObert,
		        		filtreArxiuPendents);
		        
		        return resultat.generate();

    		}
    	}
        
        return filtreBase.generate();
    }
    
    public class InitialOnChangeDocumentResourceLogicProcessor implements OnChangeLogicProcessor<DocumentResource> {
		@Override
		public void onChange(Serializable id, DocumentResource previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, DocumentResource target) {
			//Camps transient per inicialitzar al carregar el formulari
	        target.setPluginSummarizeActiu(Utils.hasValue(configHelper.getConfig(PropertyConfig.SUMMARIZE_PLUGIN_CLASS)));
	        target.setFuncionariHabilitatDigitalib(RolHelper.doesCurrentUserHasRol("DIB_USER"));
	        target.setDeteccioFirmaAutomaticaActiva(configHelper.getAsBoolean(PropertyConfig.DETECCIO_FIRMA_AUTOMATICA));
	        target.setDocumentFirmaTipus(DocumentFirmaTipusEnumDto.SENSE_FIRMA);
	        
	        if(previous.getAdjunt()!=null) {
                new AdjuntOnchangeLogicProcessor().onChange(id, previous, DocumentResource.Fields.adjunt, previous.getAdjunt(), answers, previousFieldNames, target);
            }
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
    
    public class ViaFirmaDispositiuOptionsProvider implements FieldOptionsProvider {
		@Override
		public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
			String[] requestParam = requestParameterMap.get(DocumentResource.ViaFirmaForm.Fields.codiUsuariViaFirma);
			String vfUserCodi = requestParam!=null?requestParam[0]:"";
			List<ViaFirmaDispositiuDto> dispos = pluginHelper.getDeviceUser(
					vfUserCodi,
					firmaViaFirmaHelper.getViaFirmaUsuariPassword(vfUserCodi));
			List<FieldOption> resultat = new ArrayList<FieldOption>();
			if (dispos!=null) {
				for (ViaFirmaDispositiuDto dsp: dispos) {
					resultat.add(new FieldOption(dsp.getCodi(), dsp.getDescripcio()));
				}
			}
			return resultat;
		}
    }
    
    @Override
    public DocumentResource create(DocumentResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
    	try {
    		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
			ContingutEntity pare = null;
    		if (resource.getCarpeta()!=null) {
    			pare = contingutRepository.findById(resource.getCarpeta().getId()).get();	
    		} else {
    			pare = contingutRepository.findById(resource.getExpedient().getId()).get();
    		}
    		DocumentDto documentCreat = documentHelper.crearDocument(
    				entitatEntity.getId(),
                    resource.toDocumentDto(),
    				pare,
    				true,
    				false,
    				true);
    		resource.setId(documentCreat.getId());
    		afterDbChange(documentCreat.getExpedientId());
    	} catch (ValidationException ex) {
    		throw ex;
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/document/"+resource.getId()+"/create", ex);
    		throw ex;
    	}
    	return resource;
    }
    
    @Override
    public void delete(Long id, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
    	try {
    		Long expedientId = documentRepository.findById(id).get().getExpedient().getId();
    		EntitatEntity entitatEntity = entitatRepository.findByCodi(configHelper.getEntitatActualCodi());
    		contingutHelper.deleteReversible(entitatEntity.getId(), id, null, configHelper.getRolActual());
    		afterDbChange(expedientId);
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/document/"+id+"/delete", ex);
    		throw new ResourceNotFoundException(getResourceClass(), ex.getMessage());
    	}
    }
    
    @Override
	public DocumentResource update(Long id, DocumentResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
    	try {
    		DocumentEntity documentActual = documentRepository.findById(resource.getId()).get();
    		if (resource.isOrdrePatch()) {
    			DocumentResourceEntity documentResourceActual = documentResourceRepository.findById(resource.getId()).get();
    			Long reorderPreviousParentId = reorderGetParentId(documentResourceActual);
    			Long reorderResourceSequence = reorderGetSequenceFromResourceOrEntity(resource, documentResourceActual);
				if (!Objects.equals(resource.getPare().getId(), documentResourceActual.getPare().getId())) {
					documentResourceActual.setPare(contingutResourceRepository.findById(resource.getPare().getId()).get());
				}
				reorderIfReorderable(
						documentResourceActual,
						reorderResourceSequence,
						reorderPreviousParentId,
						true,
						false);
				//mourer també al arxiu
				boolean parentIdChanged = !Objects.equals(documentResourceActual.getOrderParentId(), reorderPreviousParentId);
				if (parentIdChanged) {
					ContingutEntity contingutPare = contingutRepository.findById(documentResourceActual.getOrderParentId()).get();
//					pluginHelper.arxiuDocumentMoure(documentActual.getArxiuUuid(), contingutPare.getArxiuUuid());
					contingutHelper.arxiuDocumentPropagarMoviment(
							documentActual.getArxiuUuid(),
							contingutPare,
							documentActual.getExpedient().getArxiuUuid());
				}
    		} else {
    			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
        		DocumentDto documentActualitzat = documentHelper.updateDocument(
        				entitatEntity.getId(),
        				documentActual,
    					resource.toDocumentDto(),
        				true);
        		resource.setId(documentActualitzat.getId());
        		afterDbChange(documentActual.getExpedient().getId());
    		}
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/document/"+resource.getId()+"/update", ex);
    	}
    	return resource;
    }
    
    private void afterDbChange(Long expedientId) {
    	//Esborram cache de validacions del expedient
		cacheHelper.evictErrorsValidacioPerNode(expedientId); // Primero hace evict
		ErrorsValidacioChangedEvent evce = new ErrorsValidacioChangedEvent(
				expedientId,
				cacheHelper.findErrorsValidacioPerNodeAndSendComanda(expedientId));
		eventHelper.notifyErrorsValidacio(evce); // Luego notifica con datos frescos	
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
        
        resource.setErrors(cacheHelper.findErrorsValidacioPerNode(entity.getId()));
        resource.setValid(resource.getErrors().isEmpty());
        resource.setAmbNotificacions(!entity.getNotificacions().isEmpty());
        
		DocumentNotificacioEstatEnumDto estatDarreraNotificacio = documentNotificacioRepository.findLastEstatNotificacioByDocumentId(entity.getId());
		resource.setEstatDarreraNotificacio(estatDarreraNotificacio != null ? estatDarreraNotificacio.name() : "");

		Boolean isErrorLastNotificacio = documentNotificacioRepository.findErrorLastNotificacioByDocumentId(entity.getId());
		resource.setErrorDarreraNotificacio(isErrorLastNotificacio != null ? isErrorLastNotificacio : false);

		Boolean isErrorLastEnviament = documentPortafirmesRepository.findErrorLastEnviamentPortafirmesByDocumentId(entity.getId());
		resource.setErrorEnviamentPortafirmes(isErrorLastEnviament != null ? isErrorLastEnviament : false);
        
        resource.setHasFirma(resource.getDocumentFirmaTipus()!=DocumentFirmaTipusEnumDto.SENSE_FIRMA);
        resource.setFirmaParcial(DocumentEstatEnumDto.FIRMA_PARCIAL.equals(entity.getEstat()));
        
        if (entity.getMetaDocument()!=null) {
//        	MetaDocumentResourceEntity metaDocumentResourceEntity = (MetaDocumentResourceEntity) Hibernate.unproxy(entity.getMetaDocument());
        	resource.setMetaDocumentInfo(objectMappingHelper.newInstanceMap(
        			entity.getMetaDocument(),
        			MetaDocumentResource.class,
        			"portafirmesResponsables", "serialVersionUID"));
        }
        
        if (entity.getCreatedBy()!=null) {
    		UsuariResourceEntity usuariResourceEntity = usuariResourceRepository.findById(entity.getCreatedBy()).orElse(null);
    		if (usuariResourceEntity!=null) {
    			resource.setCreatedByFullName(usuariResourceEntity.getNom() + " (" + usuariResourceEntity.getCodi() + ")");
    		}
    	}
    	if (entity.getLastModifiedBy()!=null) {
    		UsuariResourceEntity usuariResourceEntity = usuariResourceRepository.findById(entity.getLastModifiedBy()).orElse(null);
    		resource.setLastModifiedByFullName(usuariResourceEntity.getNom() + " (" + usuariResourceEntity.getCodi() + ")");
    	}
    }

	@Override
	protected List<DocumentResourceEntity> reorderFindLinesWithParent(Serializable parentId) {
		return documentResourceRepository.findAllByPareIdOrderByOrdreAsc((Long)parentId);
	}

    private class PathPerspectiveApplicator implements PerspectiveApplicator<DocumentResourceEntity, DocumentResource> {
        @Override
        public void applySingle(String code, DocumentResourceEntity entity, DocumentResource resource) throws PerspectiveApplicationException {
            resource.setTreePath(contingutResourceHelper.getTreePath(entity));
        }
    }
    
    private class ArxiuDocumentPerspectiveApplicator implements PerspectiveApplicator<DocumentResourceEntity, DocumentResource> {
        @Override
        public void applySingle(String code, DocumentResourceEntity entity, DocumentResource resource) throws PerspectiveApplicationException {
            Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
                    entity.getId(), entity.getNom(), entity.getArxiuUuid(), entity.getEntitat().getCodi(),
                    null, null, true, false);
            ArxiuDetallDto arxiu = contingutResourceHelper.getArxiuDocumentDetall(arxiuDocument,entity.getEntitat().getId());
            resource.setArxiu(arxiu);
        }
    }
    
    private class ProcedimentPerspectiveApplicator implements PerspectiveApplicator<DocumentResourceEntity, DocumentResource> {
		@Override
		public void applySingle(String code, DocumentResourceEntity entity, DocumentResource resource) throws PerspectiveApplicationException {
			resource.setMetaExpedient(ResourceReference.toResourceReference(
					entity.getExpedient().getMetaExpedient().getId(),
					entity.getExpedient().getMetaExpedient().getNom()));
		}
    }
    
    private class FirmesPerspectiveApplicator implements PerspectiveApplicator<DocumentResourceEntity, DocumentResource> {
        @Override
        public void applySingle(String code, DocumentResourceEntity entity, DocumentResource resource) throws PerspectiveApplicationException {
        	try {
	        	if (!DocumentFirmaTipusEnumDto.SENSE_FIRMA.equals(entity.getDocumentFirmaTipus())) {
	        		if (Utils.hasValue(entity.getArxiuUuid())) {
	        			resource.setFirmes(pluginHelper.validaSignaturaObtenirFirmes(entity.getArxiuUuid(), false));
	        		} else if (entity.getGesDocAdjuntId()!=null) {
	        			ByteArrayOutputStream streamDoc = new ByteArrayOutputStream();
	        			pluginHelper.gestioDocumentalGet(entity.getGesDocAdjuntId(), PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS, streamDoc);
	        			
	        			ByteArrayOutputStream streamFirma = null;
	        			if (entity.getGesDocAdjuntFirmaId()!=null) {
	        				streamFirma = new ByteArrayOutputStream();
	            			pluginHelper.gestioDocumentalGet(entity.getGesDocAdjuntFirmaId(), PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS, streamFirma);
	        			}
	        			resource.setFirmes(pluginHelper.validaSignaturaObtenirFirmes(
	        					entity.getFitxerNom(), 
	        					streamDoc.toByteArray(), 
	        					streamFirma!=null?streamFirma.toByteArray():null, 
	        					entity.getFitxerContentType(), 
	        					false));
	        		}
	        	}
        	} catch (Exception ex) {
        		excepcioLogHelper.addExcepcio("/expedient/FirmesPerspectiveApplicator", ex);
                ArxiuFirmaDto arxiuFirmaDto = new ArxiuFirmaDto();
                arxiuFirmaDto.setErrorFirma(true);
                arxiuFirmaDto.setErrorDesc(ex.getMessage());
                resource.setFirmes(List.of(arxiuFirmaDto));
        	}
        }
    }
    
    private class ArxiuVersionsPerspectiveApplicator implements PerspectiveApplicator<DocumentResourceEntity, DocumentResource> {
        @Override
        public void applySingle(String code, DocumentResourceEntity entity, DocumentResource resource) throws PerspectiveApplicationException {
            List<DocumentVersioDto> versions = contingutResourceHelper.getVersions(entity);
            resource.setVersions(versions);
            resource.setCsvLinkUrl(configHelper.getConfig(PropertyConfig.CONCSV_BASE_URL));
        }
    }
    
    private class CountPerspectiveApplicator implements PerspectiveApplicator<DocumentResourceEntity, DocumentResource> {
        @Override
        public void applySingle(String code, DocumentResourceEntity entity, DocumentResource resource) throws PerspectiveApplicationException {
        	if (entity.getMetaNode() != null)
        		resource.setNumMetaDades(entity.getMetaNode().getMetaDades().size());
            resource.setNumMoviments(contingutMovimentRepository.countByContingutId(entity.getId()));
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
            	UsuariResourceEntity usuari = usuariResourceRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName()).get();
    			String dadesURL = previous.getExpedient().getId()+"#"+previous.getId()+"#"+SecurityContextHolder.getContext().getAuthentication().getName();
				String paramSecure = Utils.encripta(dadesURL, configHelper.getConfig(PropertyConfig.CLAU_ENCRIPTACIO));
            	String urlReturn = configHelper.getConfig(PropertyConfig.BASE_URL) + "/modal/digitalitzacio/event/resultatScan/"+paramSecure+"/";
        		DigitalitzacioTransaccioRespostaDto respostaDto = pluginHelper.digitalitzacioIniciarProces(
        				usuari.getIdioma()!=null?usuari.getIdioma().toString():"ca",
        				fieldValue.toString(),
        				usuari.toUsuariDto(), 
        				urlReturn);
        		target.setDigitalitzacioProcesUrl(respostaDto.getUrlRedireccio());
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
              //La majoria de les vegades no arriba el contentType, al detectaFirmaDocument l'emplenam si es buid
                target.setFitxerContentType(adjunt.getContentType());

                if (Boolean.parseBoolean(configHelper.getConfig(PropertyConfig.DETECCIO_FIRMA_AUTOMATICA))) {
                	
                	SignatureInfoDto signatureInfoDto = pluginHelper.detectaFirmaDocument(
                			adjunt.getContent(),
                			Utils.getFitxerContentType(adjunt.getName(), adjunt.getContentType()));

                    target.setAmbFirma(signatureInfoDto.isSigned());
                    target.setHasFirma(signatureInfoDto.isSigned());
                    target.setValidacioFirmaCorrecte(!signatureInfoDto.isError());
                    target.setValidacioFirmaErrorMsg(signatureInfoDto.getErrorMsg());

                    if (signatureInfoDto.isSigned()) {
                        target.setDocumentFirmaTipus(DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA);
                        if (signatureInfoDto.isError() && !answers.containsKey(ERROR_SIGNATURE_VALIDATION)) {
                            throw new AnswerRequiredException(DocumentResource.class, ERROR_SIGNATURE_VALIDATION, signatureInfoDto.getErrorMsg());
                        }
                    } else {
                    	target.setDocumentFirmaTipus(DocumentFirmaTipusEnumDto.SENSE_FIRMA);
                    }
                }
            } else {
            	target.setDocumentFirmaTipus(DocumentFirmaTipusEnumDto.SENSE_FIRMA);
                target.setFitxerNom(null);
                target.setFitxerContingut(null);
                target.setFitxerTamany(null);
                target.setFitxerContentType(null);
                target.setAmbFirma(false);
                target.setHasFirma(false);
                target.setValidacioFirmaCorrecte(false);
                target.setValidacioFirmaErrorMsg("");
            }
        }
    }
    private class FirmaAdjuntOnchangeLogicProcessor implements OnChangeLogicProcessor<DocumentResource> {
        @Override
        public void onChange(Serializable id, DocumentResource previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentResource target) {

            if (fieldValue != null) {
                FileReference adjunt = (FileReference) fieldValue;
                target.setAmbFirma(true);
                target.setHasFirma(true);
                target.setNomFitxerFirmat(adjunt.getName());
                target.setFirmaNom(adjunt.getName());
                target.setFirmaContingut(adjunt.getContent());
//                target.(adjunt.getContentLength());
                target.setFirmaContentType(adjunt.getContentType());

            } else {
                target.setAmbFirma(false);
                target.setHasFirma(false);
                target.setNomFitxerFirmat(null);
                target.setFirmaNom(null);
                target.setFirmaContingut(null);
//                target.(null);
                target.setFirmaContentType(null);
            }
        }
    }
    private class HasFirmaOnchangeLogicProcessor implements OnChangeLogicProcessor<DocumentResource> {
        @Override
        public void onChange(Serializable id, DocumentResource previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentResource target) {
            boolean isDeteccioFirmaAutomaticaActiva = configHelper.getAsBoolean(PropertyConfig.DETECCIO_FIRMA_AUTOMATICA);
            boolean ambFirma = fieldValue != null && (Boolean) fieldValue;
            
            if (isDeteccioFirmaAutomaticaActiva) {
                if (!DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA.equals(previous.getDocumentFirmaTipus())) {
                    target.setDocumentFirmaTipus(ambFirma
                            ? DocumentFirmaTipusEnumDto.FIRMA_SEPARADA
                            : DocumentFirmaTipusEnumDto.SENSE_FIRMA);
                }
            } else if (ambFirma && DocumentFirmaTipusEnumDto.SENSE_FIRMA.equals(previous.getDocumentFirmaTipus())) {
            	//Quant no hi ha detecció de firma, i s'ha marcat document amb firma, preseleccionar firma adjunta (el cas mes probable)
            	target.setDocumentFirmaTipus(DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA);
            }
        }
    }

    private class EnviarViaEmailActionExecutor implements ActionExecutor<DocumentResourceEntity, DocumentResource.EnviarViaEmailFormAction, DocumentResource> {

        @Override
        public DocumentResource exec(String code, DocumentResourceEntity entity, DocumentResource.EnviarViaEmailFormAction params) throws ActionExecutionException {

            if (!params.getEmail().isEmpty() || !params.getResponsables().isEmpty()) {
                List<String> emails = Arrays.asList(params.getEmail().split(","));
                List<String> desinataris = params.getResponsables().stream()
                        .map(ResourceReference::getId)
                        .collect(Collectors.toList());

                emailHelper.enviarDocument(entity.getId(), emails, desinataris, params.getVersioDocument());
            }

            return objectMappingHelper.newInstanceMap(entity, DocumentResource.class);
        }

        @Override
        public void onChange(Serializable id, DocumentResource.EnviarViaEmailFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentResource.EnviarViaEmailFormAction target) {
            if (fieldName == null) {
                DocumentResourceEntity entity = documentResourceRepository.findById((Long) id).orElse(null);
                if (entity != null) {
                    if (
                        !(entity.getDocumentTipus() == DocumentTipusEnumDto.DIGITAL || entity.getDocumentTipus() == DocumentTipusEnumDto.IMPORTAT)
                            || !(
                                    (entity.getArxiuEstat() == ArxiuEstatEnumDto.DEFINITIU || entity.getEstat() == DocumentEstatEnumDto.FIRMA_PARCIAL)
                                            || Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.IMPRIMIBLE_NO_FIRMAT_ACTIU))
                            )
                    ) {
                        target.setVersioDocument(VersioDocumentEnum.ORIGINAL);
                        target.setDisableVersioDocument(true);
                    }
                }
            }
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
                	Long expedientId = null; //Tots els documents son del mateix expedient.
                	for (Long id: params.getIds()) {
                		DocumentEntity  document = documentHelper.comprovarDocument(entitatEntity.getId(), id, false, true, false, false, false, configHelper.getRolActual());
                		expedientId = document.getExpedient().getId();
            			documentHelper.updateTipusDocumentDocument(
            					entitatEntity.getId(),
            					document,
            					params.getMetaDocument().getId(),
            					false);
                	}
                	if (expedientId!=null) {
                		afterDbChange(expedientId);
                	}
                }
                
                return objectMappingHelper.newInstanceMap(entity, DocumentResource.class);
                
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/CanviTipusDocumentsActionExecutor", e);
				throw new ReportGenerationException(DocumentResource.class, null, code, "document.canviTipus.reject");
			}
		}
    }
    private class DescarregarDocumentsMassiuZipGenerator implements ReportGenerator<DocumentResourceEntity, MassiveAction, Serializable> {

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

    		try {

	    		ExpedientResource.MassiveAction params = (ExpedientResource.MassiveAction)data.get(1);
	    		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
        		FitxerDto fitxerDto = documentHelper.getZipFromDocumentsIds(entitatEntity.getId(), params.getIds());
                return new DownloadableFile(
            			fitxerDto.getNom(),
            			fitxerDto.getContentType(),
	            		fitxerDto.getContingut());

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/descarregarDocumentsMassiuZip", e);
				throw new ReportGenerationException(ExpedientResource.class, null, code, "document.descarregar.reject");
			}
		}
    }
    private class DescarregarVersionReportGenerator implements ReportGenerator<DocumentResourceEntity, DocumentResource.DescarregarVersionFormAction, FitxerDto> {

        @Override
        public List<FitxerDto> generateData(String code, DocumentResourceEntity entity, DocumentResource.DescarregarVersionFormAction params) throws ReportGenerationException {
            try {
                List<FitxerDto> parametres = new ArrayList<FitxerDto>();
                parametres.add(documentHelper.getFitxerAssociat(entity.getId(), params.getVersion()));
                return parametres;
            } catch (Exception e) {
                excepcioLogHelper.addExcepcio("/expedient/"+entity.getId()+"/descarregarDocumentsMassiuZip", e);
                throw new ReportGenerationException(ExpedientResource.class, entity.getId(), code, "document.descarregar.reject");
            }
        }

        @Override
        public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
            for(FitxerDto fitxerDto : (List<FitxerDto>) data){
                return new DownloadableFile(
                        fitxerDto.getNom(),
                        fitxerDto.getContentType(),
                        fitxerDto.getContingut()
                );
            }
            return null;
        }

        @Override
        public void onChange(Serializable id, DocumentResource.DescarregarVersionFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, DocumentResource.DescarregarVersionFormAction target) {}
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
        		ContingutEntity pare = contingutRepository.findById(params.getExpedient().getId()).get();
        		DocumentDto documentDto = new DocumentDto();
            	MetaNodeDto metaNode = new MetaNodeDto();
            	metaNode.setId(params.getMetaDocument().getId());
            	documentDto.setMetaNode(metaNode);
            	documentDto.setPareId(null);
            	documentDto.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
            	documentDto.setNom(fitxerDto.getNom());
            	documentDto.setData(Calendar.getInstance().getTime());
            	documentDto.setNtiOrigen(params.getNtiOrigen());
            	documentDto.setNtiEstadoElaboracion(params.getNtiEstadoElaboracion());
            	documentDto.setFitxerContingut(fitxerDto.getContingut());
            	documentDto.setFitxerContentType(fitxerDto.getContentType());
            	documentDto.setFitxerTamany((long)fitxerDto.getContentType().length());
            	documentDto.setFitxerNom(fitxerDto.getNom());
            	documentDto.setAmbFirma(false);
            	documentDto.setData(Calendar.getInstance().getTime());
            	documentDto = documentHelper.crearDocument(entitatEntity.getId(), documentDto, pare, true, false, true);

            	DocumentResourceEntity newZipFile = new DocumentResourceEntity();
        		newZipFile.setId(documentDto.getId());
        		newZipFile.setNom(documentDto.getNom());
        		//newZipFile.setExpedient(expedientResourceRepository.findById(params.getExpedient().getId()).get());
        		ExpedientResourceEntity ere = new ExpedientResourceEntity();
        		ere.setId(params.getExpedient().getId());
        		newZipFile.setExpedient(ere);
        		
        		return objectMappingHelper.newInstanceMap(newZipFile, DocumentResource.class);
        		
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/NotificarDocumentsZipActionExecutor", e);
				throw new ReportGenerationException(DocumentResource.class, null, code, "document.notificarDocuments.reject");
			}
		}
    }
    private class MoureCopiarVincularActionExecutor implements ActionExecutor<DocumentResourceEntity, DocumentResource.MoureFormAction, DocumentResource> {

        @Override
        public DocumentResource exec(String code, DocumentResourceEntity entity, DocumentResource.MoureFormAction params) throws ActionExecutionException {

            try {
                EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
                for (Long contingutOrigenId: params.getIds()) {
                    Long contingutDestiId = params.getCarpeta()!=null?params.getCarpeta().getId():params.getExpedient().getId();
                    switch (params.getAction()) {
                    case MOURE:
                        contingutHelper.move(entitatEntity.getId(), contingutOrigenId, contingutDestiId, params.getCarpetaNova(), configHelper.getRolActual());
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
                excepcioLogHelper.addExcepcio("/expedient/MoureCopiarVincularActionExecutor", e);
                throw new ActionExecutionException(getResourceClass(), null, code, e.getMessage());
            }

            if (!params.isMassivo() && params.getIds().size() == 1){
                DocumentResourceEntity documentResourceEntity = documentResourceRepository.findById(params.getIds().get(0)).get();
                if (documentResourceEntity != null) {
                    return objectMappingHelper.newInstanceMap(documentResourceEntity, DocumentResource.class);
                }
            }

            return null;
        }

        @Override
        public void onChange(Serializable id, DocumentResource.MoureFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentResource.MoureFormAction target) {}
    }
    
    private class GuardarArxiuActionExecutor implements ActionExecutor<DocumentResourceEntity, MassiveAction, Serializable> {

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}

		@Override
		public Serializable exec(String code, DocumentResourceEntity entity, MassiveAction params) throws ActionExecutionException {
			try {

				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);

				if (params.isMassivo()) {

					List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
					ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(
							ExecucioMassivaTipusDto.CUSTODIAR_ELEMENTS_PENDENTS,
							new Date(),
							null,
							configHelper.getRolActual());
					execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.DOCUMENT);
				
					return params.getIds()!=null?params.getIds().size():0;
	        	
				} else {

					Exception errorGuardant = null;
					entity = documentResourceRepository.findById(params.getIds().get(0)).get();
					if (entity.getArxiuUuid() == null) {
						errorGuardant = documentHelper.guardarDocumentArxiu(entity.getId());
					} else {
						Long registreAnnexId = annexPendentMourerArxiu(entity.getId());
						if (registreAnnexId!=null) {
							errorGuardant = expedientHelper.moveDocumentArxiuNewTransaction(registreAnnexId);
						} else if (!StringUtils.isEmpty(entity.getGesDocFirmatId())) {
							errorGuardant = firmaPortafirmesHelper.portafirmesReintentar(entitatEntity.getId(), entity.getId());
						}
					}

					if (errorGuardant!=null) {
						throw new ActionExecutionException(getResourceClass(), entity.getId(), code, errorGuardant.getMessage());
					}
					
					return objectMappingHelper.newInstanceMap(entity, DocumentResource.class);
	        	}

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio(
						"/document/GuardarArxiuActionExecutor",
						e,
						Utils.getIdsSeparatsComa(params.getIds()),
						"massiu="+params.isMassivo());
				String message = messageHelper.getMessage("message.common.action.error")+": "+e.getMessage();
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, message);
			}
		}
		
		private Long annexPendentMourerArxiu(Long documentId) {
			List<RegistreAnnexResourceEntity> annexosDoc = registreAnnexResourceRepository.findByDocumentId(documentId);
			if (annexosDoc!=null) {
				for (RegistreAnnexResourceEntity rare: annexosDoc) {
					if (rare.getError() != null && !rare.getError().isEmpty()) {
						return rare.getId();
					}
				}
			}
			return null;
		}
    }
    
    private class ConvertirDefinitiuActionExecutor implements ActionExecutor<DocumentResourceEntity, MassiveAction, Serializable> {

		@Override
		public Serializable exec(String code, DocumentResourceEntity entity, MassiveAction params) throws ActionExecutionException {
			try {
				if (params.getIds()!=null) {
					EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
					for (Long docId: params.getIds()) {
						DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
								entitatEntity.getId(),
								docId,
								false,
								true);
						documentHelper.actualitzarEstat(document, DocumentEstatEnumDto.DEFINITIU);
					}
				}
				int numElem = params!=null && params.getIds()!=null?params.getIds().size():0;
				return "{\"num\": \""+numElem+"\"}";
			} catch (Exception e) {
				String docIdStr = Utils.getIdsSeparatsComa(params.getIds());
				excepcioLogHelper.addExcepcio("/document/ConvertirDefinitiuActionExecutor", e, docIdStr, "massiu="+params.isMassivo());
				String message = messageHelper.getMessage("message.common.action.error")+": "+e.getMessage();
				throw new ActionExecutionException(getResourceClass(), docIdStr, code, message);
			}
		}

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}
    }
    
    private class CsvLinkActionExecutor implements ActionExecutor<DocumentResourceEntity, MassiveAction, Serializable> {

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}

		@Override
		public Serializable exec(String code, DocumentResourceEntity entity, MassiveAction params) throws ActionExecutionException {
			String docIdsStr = Utils.getIdsSeparatsComa(params.getIds());
			try {
				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
                Map<String, String> result = new HashMap<>();
                String urls = "";
                for (Long idDoc: params.getIds()) {
                	urls+=documentHelper.getEnllacCsv(entitatEntity.getId(), idDoc) + "\n";
                }
                result.put("url", urls);
                return (Serializable)result;
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/document/CsvLinkActionExecutor", e, docIdsStr, "massiu="+params.isMassivo());
				String message = messageHelper.getMessage("message.common.action.error")+": "+e.getMessage();
				throw new ActionExecutionException(getResourceClass(), docIdsStr, code, message);
			}
		}
    }
    
    private class ViaFirmaActionExecutor implements ActionExecutor<DocumentResourceEntity, DocumentResource.ViaFirmaForm, Serializable> {

        @Override
        public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
        	List<FieldOption> resultat = new ArrayList<>();
        	if (DocumentResource.ViaFirmaForm.Fields.codiUsuariViaFirma.equals(fieldName)) {
        		Set<ViaFirmaUsuariEntity> vfUsuaris = usuariHelper.viaFirmaUsuarisUsuariActual();
        		if (vfUsuaris!=null) {
        			for (ViaFirmaUsuariEntity vfue: vfUsuaris) {
        				resultat.add(new FieldOption(vfue.getCodi(), vfue.getDescripcio()));
        			}
        		}
        	}
        	return resultat;
        }
    	
		@Override
		public void onChange(Serializable id, ViaFirmaForm previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, ViaFirmaForm target) {
			if (fieldName==null) {
				DocumentResourceEntity docRes = documentResourceRepository.findById((Long)id).get();
				target.setTitol(docRes.getNom());
				target.setDescripcio("Firm de document "+docRes.getNom()+"["+docRes.getMetaDocument().getNom()+"]");
				target.setDispositiusEnabled(configHelper.getAsBoolean(PropertyConfig.VIAFIRMA_PLUGIN_DISPOSITIUS_ENABLED));
			} else if (DocumentResource.ViaFirmaForm.Fields.interessat.equals(fieldName)) {
				if (fieldValue!=null) {
					Long interessatId = ((ResourceReference<InteressatResource, Long>)fieldValue).getId();
					InteressatResourceEntity intRes = interessatResourceRepository.findById(interessatId).get();
					target.setSignantNom(intRes.getNomComplet()); 
					target.setSignantNif(intRes.getDocumentNum());
					target.setSignantEmail(intRes.getEmail());
				}
			}
		}

		@Override
		public Serializable exec(String code, DocumentResourceEntity entity, ViaFirmaForm params) throws ActionExecutionException {
			try {
				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
				ViaFirmaEnviarDto viaFirmaEnviarDto = new ViaFirmaEnviarDto();
				viaFirmaEnviarDto.setTitol(params.getTitol());
				viaFirmaEnviarDto.setDescripcio(params.getDescripcio());
				viaFirmaEnviarDto.setTipusDestinatari(params.getTipusDestinatari());
				viaFirmaEnviarDto.setSignantEmail(params.getSignantEmail());
				viaFirmaEnviarDto.setCodiUsuariViaFirma(params.getCodiUsuariViaFirma());
				viaFirmaEnviarDto.setViaFirmaDispositiuCodi(params.getViaFirmaDispositiuCodi());
				viaFirmaEnviarDto.setSignantNif(params.getSignantNif());
				viaFirmaEnviarDto.setSignantNom(params.getSignantNom());
				viaFirmaEnviarDto.setObservacions(params.getObservacions());
				viaFirmaEnviarDto.setFirmaParcial(params.getFirmaParcial());
				viaFirmaEnviarDto.setValidateCodeEnabled(params.getValidateCodeEnabled());
				viaFirmaEnviarDto.setValidateCode(params.getValidateCode());
				viaFirmaEnviarDto.setRebreCorreu(params.getRebreCorreu());
				viaFirmaEnviarDto.setEmplenable(params.getEmplenable());
				
				firmaViaFirmaHelper.viaFirmaEnviar(entitatEntity.getId(), entity.getId(), viaFirmaEnviarDto);
				return objectMappingHelper.newInstanceMap(entity, DocumentResource.class);
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/document/"+entity.getId()+"/ViaFirmaActionExecutor", e);
				throw new ActionExecutionException(
						getResourceClass(),
						entity.getId(),
						code,
						messageHelper.getMessage("document.viaFirma.reject", new Object[]{e.getMessage()}));
			}
		}
    }

    private class NouDocumentPinbalActionExecutor implements ActionExecutor<DocumentResourceEntity, DocumentResource.NewDocPinbalForm, Serializable> {

        @Override
        public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
            List<FieldOption> resultat = new ArrayList<FieldOption>();
            switch (fieldName) {
                case NewDocPinbalForm.Fields.provincia:
                case NewDocPinbalForm.Fields.provinciaNaixament:
                    resultat.add(new FieldOption("07", "Illes Balears"));
                    break;
                case NewDocPinbalForm.Fields.comunitatAutonoma:
                    resultat.add(new FieldOption("04", "Illes Balears"));
                    break;
                case NewDocPinbalForm.Fields.nacionalitat:
                case NewDocPinbalForm.Fields.paisNaixament:
                    List<PaisDto> paisos = cacheHelper.findPaisos();
                    if (paisos!=null) {
                        for (PaisDto dsp: paisos) {
                            resultat.add(new FieldOption(dsp.getCodi(), dsp.getNom()));
                        }
                    }
                    break;
                case NewDocPinbalForm.Fields.municipi:
                case NewDocPinbalForm.Fields.municipiNaixament:
                case NewDocPinbalForm.Fields.municipiRegistre:
                    List<MunicipiDto> munis = cacheHelper.findMunicipisPerProvinciaPinbal("07");
                    if (munis!=null) {
                        for (MunicipiDto dsp: munis) {
                            resultat.add(new FieldOption(dsp.getCodi(), dsp.getNom()));
                        }
                    }
                    break;
            }
            return resultat;
        }

		@Override
		public void onChange(Serializable id, NewDocPinbalForm previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, NewDocPinbalForm target) {
			if (NewDocPinbalForm.Fields.tipusDocument.equals(fieldName)) {
                if (fieldValue!=null) {
                    ResourceReference<MetaDocumentResource, Long> tipusDocument = (ResourceReference<MetaDocumentResource, Long>) fieldValue;

                    metaDocumentResourceRepository.findById(tipusDocument.getId())
                            .ifPresent(metaDocumentResourceEntity -> {
                                if (metaDocumentResourceEntity.getPinbalServei() != null && metaDocumentResourceEntity.getPinbalServei().getCodi() != null) {
                                    target.setCodiServeiPinbal(metaDocumentResourceEntity.getPinbalServei().getCodi());
                                    target.setFinalitat(metaDocumentResourceEntity.getPinbalFinalitat());
                                } else {
                                    target.setTipusDocument(null);
                                }
                            });
                } else {
                    target.setCodiServeiPinbal(null);
                    target.setFinalitat(null);
                }
			}
		}

		@Override
		public Serializable exec(String code, DocumentResourceEntity entity, NewDocPinbalForm params) throws ActionExecutionException {
			try {
				//La entitat ja es comprova a pinbalHelper
				EntitatEntity entitatEntity = entitatRepository.findByCodi(configHelper.getEntitatActualCodi());
				
				PinbalConsultaDto consulta = new PinbalConsultaDto();
				consulta.setInteressatId(params.getTitular().getId());
				consulta.setFinalitat(params.getFinalitat());
				consulta.setConsentiment(params.getConsentiment());
	            consulta.setComunitatAutonomaCodi(params.getComunitatAutonoma());
	            consulta.setProvinciaCodi(params.getProvincia());
	            consulta.setMunicipiCodi(params.getMunicipi());
	            consulta.setDataConsulta(String.valueOf(params.getDataConsulta()));
	            consulta.setDataNaixement(String.valueOf(params.getDataNaixement()));
	            consulta.setConsentimentTipusDiscapacitat(params.getConsentimentTipusDiscapacitat());
	            consulta.setNumeroTitol(params.getNumeroTitol());
	            consulta.setCodiNacionalitat(params.getNacionalitat());
	            consulta.setPaisNaixament(params.getPaisNaixament());
	            consulta.setProvinciaNaixament(params.getProvinciaNaixament());
	            consulta.setPoblacioNaixament(params.getPoblacioNaixament());
	            consulta.setCodiPoblacioNaixament(params.getPoblacioNaixament());
	            consulta.setSexe(params.getSexe());
	            consulta.setNomPare(params.getNomPare());
	            consulta.setNomMare(params.getNomMare());
	            consulta.setTelefon(params.getTelefon());
	            consulta.setEmail(params.getEmail());
	            consulta.setNombreAnysHistoric(params.getNombreAnysHistoric());
	            consulta.setExercici(params.getExercici());	
	            consulta.setTipusPassaport(params.getTipusPassaport());
	            consulta.setDataCaducidad(params.getDataCaducidad());
	            consulta.setDataExpedicion(params.getDataExpedicion());
	            consulta.setNumeroSoporte(params.getNumeroSoporte());	
	            consulta.setRegistreCivil(params.getRegistreCivil());
	            consulta.setTom(params.getTom());
	            consulta.setPagina(params.getPagina());
	            consulta.setAusenciaSegundoApellido(params.isAusenciaSegundoApellido());
	            consulta.setCurs(params.getCurs());
	
	            if ("SVDRRCCDEFUNCIONWS01".equals(params.getCodiServeiPinbal())){
	                consulta.setMunicipiRegistreSVDRRCCDEFUNCIONWS01(params.getMunicipiRegistre());
	                consulta.setMunicipiNaixamentSVDRRCCDEFUNCIONWS01(params.getMunicipiNaixament());
	            }
	            if ("SVDRRCCMATRIMONIOWS01".equals(params.getCodiServeiPinbal())){
	                consulta.setMunicipiRegistreSVDRRCCMATRIMONIOWS01(params.getMunicipiRegistre());
	                consulta.setMunicipiNaixamentSVDRRCCMATRIMONIOWS01(params.getMunicipiNaixament());
	            }
	            if ("SVDRRCCNACIMIENTOWS01".equals(params.getCodiServeiPinbal())){
	                consulta.setMunicipiRegistreSVDRRCCNACIMIENTOWS01(params.getMunicipiRegistre());
	                consulta.setMunicipiNaixamentSVDRRCCNACIMIENTOWS01(params.getMunicipiNaixament());
	            }
	            if ("SVDDELSEXWS01".equals(params.getCodiServeiPinbal())){
	                consulta.setMunicipiNaixamentSVDDELSEXWS01(params.getMunicipiNaixament());
	            }
	
	            //Com a millora, al crear una petició pinbal, o al crear un document, es podria especificar la carpeta destí.
	            Exception resultatConsulta = pinbalHelper.pinbalNovaConsulta(
						entitatEntity.getId(),
                        params.getExpedient().getId(),
                        params.getTipusDocument().getId(),
						consulta, 
						configHelper.getRolActual());
				
	            if (resultatConsulta==null) {
	            	return params;
	            } else {
	            	throw new ActionExecutionException(getResourceClass(), null, code, messageHelper.getMessage("document.nouDocumentPinbal.reject", new Object[]{resultatConsulta.getMessage()}));
	            }
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/document/NouDocumentPinbalActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), null, code, messageHelper.getMessage("document.nouDocumentPinbal.reject", new Object[]{e.getMessage()}));
			}				
		}
    }
    
    private class IniciarFirmaWebActionExecutor implements ActionExecutor<DocumentResourceEntity, DocumentResource.IniciarFirmaNavegador, Serializable> {

		@Override
		public void onChange(Serializable id, IniciarFirmaNavegador previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, IniciarFirmaNavegador target) {}

		@Override
		public Serializable exec(String code, DocumentResourceEntity entity, IniciarFirmaNavegador params) throws ActionExecutionException {
			
			String docIdStr = Utils.getIdsSeparatsComa(params.getIds());
			
			try {

    			String dadesURL = params.isMassivo()+"#"+docIdStr+"#"+SecurityContextHolder.getContext().getAuthentication().getName();
				String paramSecure = Utils.encripta(dadesURL, configHelper.getConfig(PropertyConfig.CLAU_ENCRIPTACIO));
				String urlReturnToRipea = configHelper.getConfig(PropertyConfig.BASE_URL) + "/modal/document/event/" + paramSecure + "/firmaSimpleWebEnd";
				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
				
				List<FitxerDto> fitxersFirma = new ArrayList<FitxerDto>();
				for (Long idDoc: params.getIds()) {
					FitxerDto fitxerAfirmar = documentHelper.convertirPdfPerFirmaClient(entitatEntity.getId(), idDoc);
					fitxerAfirmar.setId(idDoc);
					fitxersFirma.add(fitxerAfirmar);
				}
				
				Map<String, String> result = new HashMap<>();
                result.put("url", pluginHelper.firmaSimpleWebStart(fitxersFirma, params.getMotiu(), urlReturnToRipea, FirmaSimpleStartTransactionRequest.VIEW_FULLSCREEN));
                return (Serializable)result;
                
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/document/IniciarFirmaWebActionExecutor", e, docIdStr, "massiu="+params.isMassivo());
				throw new ActionExecutionException(getResourceClass(), docIdStr, code, messageHelper.getMessage("document.iniciarFirmaWeb.reject", new Object[]{e.getMessage()}));
			}
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
				String message = messageHelper.getMessage("message.common.action.error")+": "+e.getMessage();
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, message);
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
               	target.setDuracio(configHelper.getAsInt(PropertyConfig.NOTIB_PLUGIN_CADUCA, 10));
               	List<InteressatResourceEntity> interessatsExp = documentResourceRepository.findById((Long)id).get().getExpedient().getInteressats();
               	if (interessatsExp!=null && interessatsExp.size()==1) {
               		InteressatResourceEntity interessatUnic = interessatsExp.get(0);
               		target.getInteressats().add(ResourceReference.toResourceReference(interessatUnic.getId(), interessatUnic.getCodiNom()));
               	}
            } else {
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

                    case DocumentResource.NotificarFormAction.Fields.grups:
                    	Set<InteressatResourceEntity> interessatSet = new LinkedHashSet<>();
                        List<Long> ids = ((List<ResourceReference<InteressatGrupResource, Long>>) fieldValue).stream()
                                .map(ResourceReference::getId).collect(Collectors.toList());
                        List<InteressatGrupResourceEntity> grups = interessatGrupResourceRepository.findAllById(ids);
                        for (InteressatGrupResourceEntity grup : grups) {
                        	interessatSet.addAll(grup.getInteressats());
                        }
                        List<InteressatResourceEntity> interessats = new ArrayList<>(interessatSet);
                        List<ResourceReference<InteressatResource, Long>> interessatsResourceList = interessats.stream()
                                .map(i->ResourceReference.<InteressatResource, Long>toResourceReference(i.getId(), i.getCodiNom()))
                                .collect(Collectors.toList());
                        target.setInteressats(interessatsResourceList);
                        break;

                    case NotificarFormAction.Fields.interessats:
                        List<InteressatResourceEntity> interesats = interessatResourceRepository.findAllById(((List<ResourceReference<InteressatResource, Long>>) fieldValue).stream()
                                .map(ResourceReference::getId).collect(Collectors.toList()));
                        List<ResourceReference<InteressatResource, Long>> interessatsAmbAvis = new ArrayList<>();
                        boolean administracioSir = false;
                        for (InteressatResourceEntity titular : interesats) {
                        	InteressatResourceEntity destinatari = titular.getRepresentant()!=null?titular.getRepresentant():null;
                        	if (InteressatTipusEnum.InteressatPersonaFisicaEntity.equals(titular.getTipus())) {
	                            if ((destinatari == null && titular.getDocumentTipus()!=InteressatDocumentTipusEnumDto.NIF && titular.getDocumentTipus()!=InteressatDocumentTipusEnumDto.DOCUMENT_IDENTIFICATIU_ESTRANGERS)
	                            ||  (destinatari != null && destinatari.getDocumentTipus()!=InteressatDocumentTipusEnumDto.NIF && destinatari.getDocumentTipus()!=InteressatDocumentTipusEnumDto.DOCUMENT_IDENTIFICATIU_ESTRANGERS)
	                            ) {
	                                if(destinatari == null){
	                                    interessatsAmbAvis.add(ResourceReference.toResourceReference(titular.getId(), titular.getNomComplet()
	                                    ));
	                                }else {
	                                    interessatsAmbAvis.add(ResourceReference.toResourceReference(destinatari.getId(), destinatari.getNomComplet()
	                                    ));
	                                }
	                            }
                        	}
                            if (InteressatTipusEnum.InteressatAdministracioEntity.equals(titular.getTipus()) && titular.getAmbOficinaSir()!=null && titular.getAmbOficinaSir().booleanValue()) {
                            	administracioSir = true;
                            }
                        }
                        target.setInteressatsAmbAvis(interessatsAmbAvis);
                        target.setAdministracioSir(administracioSir);
                        break;
                }
            }
		}

		@Override
		public DocumentResource exec(String code, DocumentResourceEntity entity, NotificarFormAction params) throws ActionExecutionException {
        	try {
	        	List<Long> interessatsIds = params.getInteressats().stream()
                        .map(ResourceReference::getId)
                        .collect(Collectors.toList());
	        	boolean anyInteressatIsAdministracio = false;
                List<InteressatResourceEntity> interessatResourceEntityList = interessatResourceRepository.findAllById(interessatsIds);

                for (InteressatResourceEntity interessatResourceEntity: interessatResourceEntityList) {
                    if (InteressatTipusEnum.InteressatAdministracioEntity.equals(interessatResourceEntity.getTipus())) {
                        anyInteressatIsAdministracio = true;
                    }
                    if (params.getEntregaPostal()!=null && params.getEntregaPostal()) {
                    	if (!interessatResourceEntity.adressaCompleta()) {
	                        throw new ActionExecutionException(
	                        		interessatResourceEntity.getClass(),
	                        		interessatResourceEntity.getId(),
	                        		code,
	                        		messageHelper.getMessage("notificacio.controller.reject.postal"));
                    	} else if (!interessatResourceEntity.adressaNormalitzadaCompleta()) {
	                        throw new ActionExecutionException(
	                        		interessatResourceEntity.getClass(),
	                        		interessatResourceEntity.getId(),
	                        		code,
	                        		messageHelper.getMessage("notificacio.controller.reject.normalitzada"));
                    	}
                    }
                }

	        	if (DocumentNotificacioTipusEnumDto.COMUNICACIO.equals(params.getTipus()) && 
	        		"application/zip".equals(entity.getFitxerContentType()) &&
	        		anyInteressatIsAdministracio) {
	        			throw new ActionExecutionException(
	        					entity.getClass(),
	        					entity.getId(),
	        					code,
	        					messageHelper.getMessage("notificacio.controller.reject.comunicacio.zip.administracio"));
	        	}

	            String entitatActualCodi = configHelper.getEntitatActualCodi();
	            EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true, false);

	        	DocumentNotificacioDto notificacioDto = new DocumentNotificacioDto();
	        	notificacioDto.setTipus(params.getTipus());
	        	notificacioDto.setInteressatsIds(interessatsIds); //El helper notifica al representant si és necessari
	        	notificacioDto.setServeiTipusEnum(params.getServeiTipus());
	        	notificacioDto.setEntregaPostal(params.getEntregaPostal() != null && params.getEntregaPostal());
	        	notificacioDto.setObservacions(params.getDescripcio());
	        	notificacioDto.setAssumpte(params.getConcepte());
				notificacioDto.setDataProgramada(params.getDataProgramada()); 
				notificacioDto.setRetard(params.getRetard());
				notificacioDto.setDataCaducitat(params.getDataCaducitat());
	        	
	        	documentNotificacioHelper.notificacioCreate(entitatEntity.getId(), entity.getId(), notificacioDto);

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/document/"+entity.getId()+"/notificar", e);
				String message = messageHelper.getMessage("message.common.action.error")+": "+e.getMessage();
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, message);
			}
        	
        	return objectMappingHelper.newInstanceMap(entity, DocumentResource.class);
		}
    }
    private class EnviarPortafirmesActionExecutor implements ActionExecutor<DocumentResourceEntity, DocumentResource.EnviarPortafirmesFormAction, DocumentResource> {

        private Map<String, String> parseToMap(String input){
            String[] tokens = input.split(",", -1); // split preservando vacíos

            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < tokens.length - 1; i += 2) {
                String key = tokens[i].trim();
                String value = tokens[i + 1].trim();
                map.put(key, value);
            }
            return map;
        }
        
        private List<String> getNifsResponsables(DocumentResource.EnviarPortafirmesFormAction params) {
	    	List<String> pfResponsables = new ArrayList<String>();
	    	if (params.getResponsables()!=null) {
	    		for (ResourceReference <UsuariResource, String> usuari: params.getResponsables()) {
	                usuariResourceRepository.findById(usuari.getId())
	                                .ifPresent(user -> pfResponsables.add(user.getNif()));
	    		}
	    	}
	    	if (params.getNifsManuals()!=null) {
	    		if(params.getNifsManuals().indexOf(",")>0) {
	    			pfResponsables.addAll(Arrays.asList(params.getNifsManuals().split(",")));
	    		} else {
	    			pfResponsables.add(params.getNifsManuals());
	    		}
	    	}
	    	if (params.getCarrecs()!=null) {
	    		pfResponsables.addAll(params.getCarrecs());
	    	}
	    	return pfResponsables;
        }

        @Override
        public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
            List<FieldOption> resultat = new ArrayList<>();

            if (DocumentResource.EnviarPortafirmesFormAction.Fields.portafirmesEnviarFluxId.equals(fieldName)) {
                EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), true, false, false, false, false);
                List<PortafirmesFluxRespostaDto> fluxosDto = pluginHelper.portafirmesRecuperarPlantillesDisponibles(
                		entitatEntity.getId(),
                		requestParameterMap.get("metaDocumentId")!=null && requestParameterMap.get("metaDocumentId").length>0 ? Long.parseLong(requestParameterMap.get("metaDocumentId")[0]) : null,
                		true,
                		false);

                if (requestParameterMap.containsKey("additionalOption")){
                    Map<String, String> additionalOption = parseToMap(requestParameterMap.get("additionalOption")[0]);
                    if (additionalOption.containsKey("value")) {
                        resultat.add(new FieldOption(additionalOption.get("value"), additionalOption.get("description")));
                    }
                }

                if (fluxosDto != null) {
                    for (PortafirmesFluxRespostaDto flx : fluxosDto) {
                        resultat.add(new FieldOption(flx.getFluxId(), "[" + flx.getDescripcio() + "] > " + flx.getNom()));
                    }
                }
            }
            if (DocumentResource.EnviarPortafirmesFormAction.Fields.carrecs.equals(fieldName)) {
                try {
                    resultat = pluginHelper.portafirmesRecuperarCarrecs().stream()
                            .map(carrec -> new FieldOption(carrec.getUsuariPersonaNif(), carrec.getCarrecName()))
                            .collect(Collectors.toList());
                } catch (Exception e) {}
            }
            resultat.sort(Comparator.comparing(FieldOption::getDescription).reversed());
            return resultat;
        }

        @Override
        public DocumentResource exec(String code, DocumentResourceEntity entity, DocumentResource.EnviarPortafirmesFormAction params) throws ActionExecutionException {

        	try {
        	
        		if (!params.isMassivo()) {
        		
        			entity = documentResourceRepository.findById(params.getIds().get(0)).get();
        			
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
		        	
		        	//Unificar els portafirmes responsables en un array de NIFS
		        	List<String> pfResponsables = getNifsResponsables(params);
		        	
		        	List<Long> annexosIds = new ArrayList<Long>();
		        	if (params.getAnnexos()!=null) {
		        		for (ResourceReference <DocumentResource, Long> annex: params.getAnnexos()) {
		        			annexosIds.add(annex.getId());
		        		}
		        	}
		        	
		        	//Enviam com a parametre transactionID si s'ha creat un flux temporal, sino enviam el fluxId
					firmaPortafirmesHelper.portafirmesEnviar(
							entitatId,
							document,
							params.getMotiu(),
							params.getPrioritat(),
							null,
							params.getPortafirmesEnviarFluxId(),
							pfResponsables.toArray(new String[0]),
							params.getPortafirmesSequenciaTipus(),
							params.getPortafirmesFluxTipus(),
							annexosIds.toArray(new Long[0]),
							params.getFluxCreat()!=null?params.getFluxCreat().getFluxId():null,
							params.isAvisFirmaParcial(),
							params.isFirmaParcial());
		        	
		        	return objectMappingHelper.newInstanceMap(entity, DocumentResource.class);
        		
        		} else {
        			
	            	List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
	            	
		        	//Unificar els portafirmes responsables en un array de NIFS
		        	List<String> pfResponsables = getNifsResponsables(params);
	            	
	    			ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(
	    					ExecucioMassivaTipusDto.PORTASIGNATURES,
	    					params.getDataInici()!=null?params.getDataInici():new Date(),
	    					null,
	    					configHelper.getRolActual());
	    			
	    			execMassDto.setEnviarCorreu(params.isEnviarCorreu());
	    			execMassDto.setMotiu(params.getMotiu());
	    			execMassDto.setPrioritat(params.getPrioritat());
	    			execMassDto.setPortafirmesResponsables(pfResponsables.toArray(new String[0]));
	    			execMassDto.setPortafirmesSequenciaTipus(params.getPortafirmesSequenciaTipus());
	    			execMassDto.setPortafirmesFluxId(params.getPortafirmesEnviarFluxId());
	    			execMassDto.setPortafirmesTransaccioId(params.getFluxCreat()!=null?params.getFluxCreat().getFluxId():null);
	    			execMassDto.setContingutIds(params.getIds());
	    			execMassDto.setRolActual(configHelper.getRolActual());
	    			execMassDto.setPortafirmesAvisFirmaParcial(params.isAvisFirmaParcial());
	    			execMassDto.setPortafirmesFirmaParcial(params.isFirmaParcial());
	    			
	    			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
	    			execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.DOCUMENT);
        			
	    			return objectMappingHelper.newInstanceMap(entity, DocumentResource.class);
        		}
        	
			} catch (Exception e) {
				String docIdStr = Utils.getIdsSeparatsComa(params.getIds());
				excepcioLogHelper.addExcepcio("/document/EnviarPortafirmesActionExecutor", e, docIdStr, "massiu="+params.isMassivo());
				String message = messageHelper.getMessage("message.common.action.error")+": "+e.getMessage();
				if (e.getCause()!=null && e.getCause().getMessage()!=null) {
					message = e.getCause().getMessage();
				}
				throw new ActionExecutionException(getResourceClass(), docIdStr, code, message);
			}
        }

        @Override
        public void onChange(Serializable id, DocumentResource.EnviarPortafirmesFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentResource.EnviarPortafirmesFormAction target) {
        	
        	//S'està inicialitzant el formulari, posam els camps que corresponguin als seus valor per defecte 
        	if (fieldName==null) {
        		target.setMostrarFirmaParcial(configHelper.getAsBoolean(PropertyConfig.FIRMA_PARCIAL));
        		target.setMostrarAvisFirmaParcial(configHelper.getAsBoolean(PropertyConfig.AVIS_FIRMA_PARCIAL));
        		
        		//Pot venir com a execució massiva o individual
        		Long documentId = previous.getIds().get(0);
        		
        		DocumentResourceEntity documentResourceEntity = documentResourceRepository.findById(documentId).get();
        		MetaDocumentResourceEntity metaDocumentResourceEntity = documentResourceEntity.getMetaDocument();
        		target.setPortafirmesFluxTipus(metaDocumentResourceEntity.getPortafirmesFluxTipus());
        		
        		if (MetaDocumentFirmaFluxTipusEnumDto.SIMPLE.equals(metaDocumentResourceEntity.getPortafirmesFluxTipus())) {
        			List<ResourceReference<UsuariResource, String>> responsables = new ArrayList<>();
        			List<String> nifs = new ArrayList<>();
        			if (metaDocumentResourceEntity.getPortafirmesResponsables()!=null) {
        				String[] pfResponsables = metaDocumentResourceEntity.getPortafirmesResponsables().split(",");
                        for (String codi : pfResponsables) {
                        	UsuariDto usuariResponsable = usuariHelper.findUsuariCarrecAmbCodiDades(codi);
                            if (usuariResponsable != null) {
                            	String txtDisplay = usuariResponsable.getNom() + " (" + Utils.nifMask(usuariResponsable.getNif()) +")";
                                responsables.add(ResourceReference.toResourceReference(usuariResponsable.getCodi(), txtDisplay));
                            }
                        }
                    }
        			target.setResponsables(responsables);
        			//Al carregar la modal de enviament a PF, no hi ha NIFs, ja que no es configuren NIFs al procediment
        			//Aquets els afegeix opcionalment l'usuari en el moment de enviar a firmar
        			target.setNifsManuals(null);
        		} else if (!previous.isMassivo()){
        			//Carregar la URL de flux de FIRMA
        			String dadesURL = documentResourceEntity.getExpedient().getId()+"#"+documentResourceEntity.getId()+"#"+SecurityContextHolder.getContext().getAuthentication().getName();
    				String paramSecure = Utils.encripta(dadesURL, configHelper.getConfig(PropertyConfig.CLAU_ENCRIPTACIO));
    				String urlReturnToRipea = configHelper.getConfig(PropertyConfig.BASE_URL) + "/modal/document/event/portafirmes/flux/"+paramSecure+"/";
    				PortafirmesIniciFluxRespostaDto transaccioResponse = pluginHelper.portafirmesIniciarFluxDeFirma(false, urlReturnToRipea);
    				target.setUrlInicioFlujoFirma(transaccioResponse.getUrlRedireccio());
    				target.setIdTransaccio(transaccioResponse.getIdTransaccio());
    			}
        		
        	} else { //És un camp concret el que s'ha canviat
        		if (DocumentResource.EnviarPortafirmesFormAction.Fields.portafirmesEnviarFluxId.equals(fieldName)) {
        			if (fieldValue!=null) {
        			UsuariResourceEntity usuari = usuariResourceRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName()).get();
	        			target.setPortafirmesFluxUrl(pluginHelper.portafirmesRecuperarUrlPlantilla(
	        					fieldValue.toString(), 
	        					usuari.getIdioma()!=null?usuari.getIdioma().toString():"ca",
	        					null,
	        					false));
        			}
        		}
        	}
        }
    }
}