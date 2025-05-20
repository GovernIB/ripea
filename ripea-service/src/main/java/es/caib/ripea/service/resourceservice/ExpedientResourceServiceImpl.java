package es.caib.ripea.service.resourceservice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.hibernate.Hibernate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.plugins.arxiu.api.Expedient;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientSequenciaResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ContingutHelper;
import es.caib.ripea.service.helper.DocumentHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.ExecucioMassivaHelper;
import es.caib.ripea.service.helper.ExpedientHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.base.model.ReportFileType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ArxiuDetallDto;
import es.caib.ripea.service.intf.dto.CodiValorDto;
import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.ElementTipusEnumDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaContingutDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaTipusDto;
import es.caib.ripea.service.intf.dto.FileNameOption;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import es.caib.ripea.service.intf.dto.PermisosPerExpedientsDto;
import es.caib.ripea.service.intf.model.ContingutResource;
import es.caib.ripea.service.intf.model.DocumentResource;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.ExpedientEstatResource;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.model.ExpedientResource.ExpedientFilterForm;
import es.caib.ripea.service.intf.model.ExpedientResource.ExportarDocumentMassiu;
import es.caib.ripea.service.intf.model.ExpedientResource.TancarExpedientFormAction;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.model.MetaExpedientOrganGestorResource;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
import es.caib.ripea.service.intf.model.NodeResource.MassiveAction;
import es.caib.ripea.service.intf.model.UsuariResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientResourceService;
import es.caib.ripea.service.permission.ExtendedPermission;
import es.caib.ripea.service.resourcehelper.ContingutResourceHelper;
import es.caib.ripea.service.resourcehelper.ExpedientResourceHelper;
import es.caib.ripea.service.resourceservice.DocumentResourceServiceImpl.InitialOnChangeDocumentResourceLogicProcessor;
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
public class ExpedientResourceServiceImpl extends BaseMutableResourceService<ExpedientResource, Long, ExpedientResourceEntity> implements ExpedientResourceService {

    private final UsuariResourceRepository usuariResourceRepository;
    private final MetaExpedientResourceRepository metaExpedientResourceRepository;
    private final MetaExpedientSequenciaResourceRepository metaExpedientSequenciaResourceRepository;
    private final OrganGestorRepository organGestorRepository;

    private final ContingutResourceHelper contingutResourceHelper;
    private final PluginHelper pluginHelper;
    private final ConfigHelper configHelper;
    private final ExpedientHelper expedientHelper;
    private final ContingutHelper contingutHelper;
    private final DocumentHelper documentHelper;
    private final ExpedientResourceHelper expedientResourceHelper;
    private final EntityComprovarHelper entityComprovarHelper;
    private final ExcepcioLogHelper excepcioLogHelper;
    private final ExecucioMassivaHelper execucioMassivaHelper;

    @PostConstruct
    public void init() {
        
    	//Exportar docs a ZIP amb formulari previ. Massiu o individual.
    	register(ExpedientResource.ACTION_MASSIVE_EXPORT_PDF_CODE,	new ExportZipGenerator());
    	//Exportar info expedients a EXCEL sense formulari previ. Nomes massiu de moment.
        register(ExpedientResource.ACTION_MASSIVE_EXPORT_ODS_CODE,	new ExportOdsGenerator());
        //Exportar info expedients a CSV sense formulari previ. Nomes massiu de moment.
        register(ExpedientResource.ACTION_MASSIVE_EXPORT_CSV_CODE,	new ExportCsvGenerator());
        //Genera els indexos dels expedients seleccionats i els comprimeix. Nomes massiu de moment.
        register(ExpedientResource.ACTION_MASSIVE_EXPORT_INDEX_ZIP, new ExportIndexZipGenerator());
        //Genera els indexos dels expedients seleccionats en PDF. Massiu o individual.
        register(ExpedientResource.ACTION_MASSIVE_EXPORT_INDEX_PDF, new ExportIdexPdfGenerator());
        //Genera els indexos dels expedients seleccionats en EXCEL. Massiu o individual.
        register(ExpedientResource.ACTION_MASSIVE_EXPORT_INDEX_XLS, new ExportIdexXlsGenerator());
        //Genera els indexos dels expedients seleccionats en PDF i els comprimeix en ZIP. Nomes individual.
        register(ExpedientResource.ACTION_MASSIVE_EXPORT_INDEX_ENI, new ExportIndexEniGenerator());
        register(ExpedientResource.ACTION_MASSIVE_EXPORT_ENI, 		new ExportEniGenerator());
        register(ExpedientResource.ACTION_MASSIVE_EXPORT_INSIDE, 	new ExportIdexInsideGenerator());
        //Genera un Zip de los documentos seleccionados para un expediente concreto
        register(ExpedientResource.ACTION_EXPORT_SELECTED_DOCS, new ExportSelectedDocsGenerator());
        
        register(ExpedientResource.ACTION_MASSIVE_AGAFAR_CODE, new AgafarActionExecutor());
        register(ExpedientResource.ACTION_MASSIVE_ALLIBERAR_CODE, new AlliberarActionExecutor());
        register(ExpedientResource.ACTION_MASSIVE_RETORNAR_CODE, new RetornarActionExecutor());
        register(ExpedientResource.ACTION_MASSIVE_FOLLOW_CODE, new FollowActionExecutor());
        register(ExpedientResource.ACTION_MASSIVE_UNFOLLOW_CODE, new UnFollowActionExecutor());
        register(ExpedientResource.ACTION_MASSIVE_DELETE_CODE, new DeleteActionExecutor());
        register(ExpedientResource.ACTION_MASSIVE_REOBRIR_CODE, new ReobrirActionExecutor());
        
        register(ExpedientResource.ACTION_TANCAR_CODE, new TancarActionExecutor());
        register(ExpedientResource.ACTION_SYNC_ARXIU, new SincronitzarArxiuActionExecutor());
        
        register(ExpedientResource.PERSPECTIVE_FOLLOWERS, new FollowersPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_COUNT, new CountPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_INTERESSATS_CODE, new InteressatsPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_ESTAT_CODE, new EstatPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_RELACIONAT_CODE, new RelacionatPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_ARXIU_EXPEDIENT, new ArxiuExpedientPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_NOTIFICACIONS_CADUCADES, new NotificacionsCaducadesPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_DOCUMENTS_NO_MOGUTS, new DocumentsNoMogutsPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_DOCUMENTS_OBLIGATORIS_TANCAR, new DocumentsObligatorisAlTancarPerspectiveApplicator());
        register(ExpedientResource.Fields.metaExpedient, new MetaExpedientOnchangeLogicProcessor());
        register(ExpedientResource.Fields.any, new AnyOnchangeLogicProcessor());
        register(ExpedientResource.FILTER_CODE, new FilterOnchangeLogicProcessor());
        //register(null, new InitialOnChangeExpedientResourceLogicProcessor());
    }
    
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
        // En cas de no disposar d'entitat actual, filtrarem per un string "................................................................................"
        // amb una mida superior a la mida màxima del camp codi de manera que asseguram que no es retornin resultats un cop aplicat el filtre
        String entitatActualCodi = configHelper.getEntitatActualCodi();
        String organActualCodi	 = configHelper.getOrganActualCodi();
        String rolActual		 = configHelper.getRolActual();

        //throw new PermissionDeniedException
        EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true, false);
        OrganGestorEntity ogEntity	= organGestorRepository.findByCodi(organActualCodi);
        
		PermisosPerExpedientsDto permisosPerExpedients = expedientHelper.findPermisosPerExpedients(
				entitatEntity.getId(),
				rolActual,
				ogEntity!=null?ogEntity.getId():null);

        Filter filtreEntitatSessio = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(ContingutResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
//                ,FilterBuilder.equal(ExpedientResource.Fields.organGestor + ".codi", organActualCodi)
        );

        /**
         * Procediment (meta-expedients) amb permisos
         */
        String procedimentId = ExpedientResource.Fields.metaExpedient + ".id";
        Filter filtreProcedimentsPermesos = null; 
        List<String> grupsClausulesIn = permisosPerExpedients.getIdsProcedimentsGruposMil();
        if (grupsClausulesIn!=null) {
	        for (String aux: grupsClausulesIn) {
		        if (aux != null && !aux.isEmpty()) {
		        	filtreProcedimentsPermesos = FilterBuilder.or(filtreProcedimentsPermesos, Filter.parse(procedimentId + " IN (" + aux + ")"));
		        }
	        }
        }

        /**
         * Meta expedient - Organs gestors amb permisos (permis ACL a OrganGestorEntity)
         */
        String ogId = ExpedientResource.Fields.metaexpedientOrganGestorPares + "." + MetaExpedientOrganGestorResource.Fields.metaExpedient + ".id";
        Filter filtreOrgansPermesos = null;
        List<String> grupsOrgansPermesosClausulesIn = permisosPerExpedients.getIdsOrgansGruposMil();
        if (grupsOrgansPermesosClausulesIn!=null) {
	        for (String aux: grupsOrgansPermesosClausulesIn) {
		        if (aux != null && !aux.isEmpty()) {
	        		filtreOrgansPermesos = FilterBuilder.or(filtreOrgansPermesos, Filter.parse(ogId + " IN (" + aux + ")"));
		        }
	        }
        }
        
        /**
         * Meta expedient - Organs gestors amb permisos (permis ACL a MetaexpedientOrganGestorEntity)
         */
        String meogId = ExpedientResource.Fields.metaexpedientOrganGestorPares + ".id";
        Filter filtreProcedimentOrgansPermesos = null;
        List<String> grupsMetaExpedientOrganPairsPermesosClausulesIn = permisosPerExpedients.getIdsMetaExpedientOrganPairsGruposMil();
        if (grupsMetaExpedientOrganPairsPermesosClausulesIn!=null) {
	        for (String aux: grupsMetaExpedientOrganPairsPermesosClausulesIn) {
		        if (aux != null && !aux.isEmpty()) {
		        	filtreProcedimentOrgansPermesos = FilterBuilder.or(filtreProcedimentOrgansPermesos, Filter.parse(meogId + " IN (" + aux + ")"));
		        }
	        }
        }
        
        /**
         * Meta expedient - Procediments comuns
         */
        String orgComuId = ExpedientResource.Fields.metaexpedientOrganGestorPares + ".id";
        Filter filtreOrgansComunsPermesos = null;
        List<String> grupsOrgansAmbProcedimentsComunsClausulesIn = permisosPerExpedients.getIdsOrgansAmbProcedimentsComunsGruposMil();
        if (grupsOrgansAmbProcedimentsComunsClausulesIn!=null) {
	        for (String aux: grupsOrgansAmbProcedimentsComunsClausulesIn) {
		        if (aux != null && !aux.isEmpty()) {
		        	filtreOrgansComunsPermesos = FilterBuilder.or(filtreOrgansComunsPermesos, Filter.parse(orgComuId + " IN (" + aux + ")"));
		        }
	        }
        }
        
        String procedimentComuId = ExpedientResource.Fields.metaexpedientOrganGestorPares + ".id";
        Filter filtreProcedimentComunsPermesos = null;
        List<String> grupsProcedimentsComunsClausulesIn = permisosPerExpedients.getIdsProcedimentsComunsGruposMil();
        if (grupsProcedimentsComunsClausulesIn!=null) {
	        for (String aux: grupsProcedimentsComunsClausulesIn) {
		        if (aux != null && !aux.isEmpty()) {
		        	filtreProcedimentComunsPermesos = FilterBuilder.or(filtreProcedimentComunsPermesos, Filter.parse(procedimentComuId + " IN (" + aux + ")"));
		        }
	        }
        }

        //Combinam els 2 filtres de comuns anteriors
        Filter combinedComunsAnd = FilterBuilder.and(filtreOrgansComunsPermesos, filtreProcedimentComunsPermesos);
        
        //Combinam els 4 filtres anteriors
        Filter combinedFilterProcedimentsOr = FilterBuilder.or(
        		filtreProcedimentsPermesos,
        		filtreOrgansPermesos,
        		filtreProcedimentOrgansPermesos,
        		combinedComunsAnd);
        
        //No aplica filtre permis directe procediment
        if (!rolActual.equals("IPA_ADMIN") && !rolActual.equals("IPA_SUPER")) {
            Filter filtreProcedimentPermisDirecte = FilterBuilder.or(
            		FilterBuilder.equal(ExpedientResource.Fields.metaExpedient+"."+MetaExpedientResource.Fields.permisDirecte, false), //Permis directe
            		filtreProcedimentsPermesos
            );
            combinedFilterProcedimentsOr = FilterBuilder.and(combinedFilterProcedimentsOr, filtreProcedimentPermisDirecte);
        }
        
        Filter filtreNoEliminats = FilterBuilder.and(FilterBuilder.equal(ContingutResource.Fields.esborrat, "0"));
        
        Filter filtreResultat = FilterBuilder.and(filtreNoEliminats, filtreEntitatSessio, combinedFilterProcedimentsOr);

        return filtreResultat.generate();
    }
    
    @Override
    protected void afterConversion(ExpedientResourceEntity entity, ExpedientResource resource) {
        resource.setNumComentaris(entity.getComentaris().size());
        resource.setNumSeguidors(entity.getSeguidors().size());
        usuariResourceRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .ifPresent(usuariResourceEntity -> resource.setSeguidor(entity.getSeguidors().contains(usuariResourceEntity)));

        /*/////////////////////////////////////////////////*/
        resource.setUsuariActualWrite(entityComprovarHelper.comprovarPermisExpedient(entity.getId(), ExtendedPermission.WRITE, "WRITE", false));
        expedientResourceHelper.setPotTancar(entity, resource);
        /*/////////////////////////////////////////////////*/
    }

    @Override
    public ExpedientResource create(ExpedientResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
    	try {
    		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
    		
			Long expedientId = expedientHelper.create(
					entitatEntity.getId(),
					resource.getMetaExpedient().getId(), //Not pot ser null
					null,
					resource.getOrganGestor().getId(), //Not pot ser null
					resource.getAny(),
					resource.getNom(),
					null,
					false,
					null,
					resource.getGrup()!=null?resource.getGrup().getId():null,
					configHelper.getRolActual(),
					resource.getPrioritat(),
					resource.getPrioritatMotiu());
			
			expedientHelper.arxiuPropagarExpedientAmbInteressatsNewTransaction(expedientId);
			
			return null;
			
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/expedient/create", ex);
    	}
    	return null;
    }

    private class CountPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
            resource.setNumInteressats((int) entity.getInteressats().stream().filter(interessatResourceEntity -> !interessatResourceEntity.isEsRepresentant()).count());
            resource.setNumTasques(entity.getTasques().size());
            resource.setNumAnotacions(entity.getPeticions().size());
            resource.setNumPublicacions(entity.getPublicacions().size());
            resource.setNumRemeses(entity.getNotificacions().size());
            resource.setNumMetaDades(entity.getMetaNode().getMetaDades().size());
            resource.setNumAlert(entity.getAlertes().size());
        }
    }
    
    private class InteressatsPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
            List<InteressatResource> interessats = entity.getInteressats().stream()
                    .map(interessatResourceEntity -> objectMappingHelper.newInstanceMap(interessatResourceEntity, InteressatResource.class))
                    .collect(Collectors.toList());
            resource.setInteressats(interessats);
        }
    }
    
    private class EstatPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
            if (entity.getEstatAdditional()!=null) {
                resource.setEstatAdditionalInfo(objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getEstatAdditional()), ExpedientEstatResource.class));
            }
        }
    }
    
    private class RelacionatPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
            List<ResourceReference<ExpedientResource, Long>> relacionatsAmb = entity.getRelacionatsAmb().stream()
                    .map(expedientResourceEntity -> objectMappingHelper.newInstanceMap(expedientResourceEntity, ExpedientResource.class))
                            .map(expedientResource -> ResourceReference.<ExpedientResource, Long>toResourceReference(
                                    expedientResource.getId(),
                                    "["+ expedientResource.getSequencia() +"/"+ expedientResource.getAny() +"] "+ expedientResource.getNom()
                            ))
                    .collect(Collectors.toList());
            resource.setRelacionatsAmb(relacionatsAmb);

            List<ResourceReference<ExpedientResource, Long>> relacionatsPer = entity.getRelacionatsPer().stream()
                    .map(expedientResourceEntity -> objectMappingHelper.newInstanceMap(expedientResourceEntity, ExpedientResource.class))
                            .map(expedientResource -> ResourceReference.<ExpedientResource, Long>toResourceReference(
                                    expedientResource.getId(),
                                    "["+ expedientResource.getSequencia() +"/"+ expedientResource.getAny() +"] "+ expedientResource.getNom()
                            ))
                    .collect(Collectors.toList());
            resource.setRelacionatsPer(relacionatsPer);
        }
    }
    
    private class ArxiuExpedientPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
            Expedient arxiuExpedient = pluginHelper.arxiuExpedientConsultar(
                    entity.getId(), entity.getNom(), entity.getMetaExpedient().getNom(), entity.getArxiuUuid());
            ArxiuDetallDto arxiu = contingutResourceHelper.getArxiuExpedientDetall(arxiuExpedient);
//            ArxiuDetallDto arxiu = contingutResourceHelper.getArxiuDetall(entity.getEntitat().getId(), entity.getId());
            resource.setArxiu(arxiu);
        }
    }
    
    private class NotificacionsCaducadesPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
            resource.setConteNotificacionsCaducades(expedientHelper.expedientTeNotificacionsCaducades(entity.getId()));
        }
    }
    
    private class DocumentsNoMogutsPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
            resource.setConteDocumentsDeAnotacionesNoMogutsASerieFinal(expedientHelper.expedientTeDocumentsDeAnotacionesNoMogutsASerieFinal(entity.getId()));
        }
    }
    
    private class DocumentsObligatorisAlTancarPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {

        	EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
        	List <DocumentEntity> documentsPendents = documentHelper.findDocumentsNoFirmatsOAmbFirmaInvalidaONoGuardatsEnArxiu(entitatEntity.getId(), entity.getId());
        	List<ResourceReference<DocumentResource, Long>> resultat = new ArrayList<ResourceReference<DocumentResource, Long>>();

        	if (documentsPendents!=null) {
        		for (DocumentEntity documentEntity: documentsPendents) {
        			if (documentEntity.isDocFromAnnex() ||
        				MultiplicitatEnumDto.M_1.equals(documentEntity.getMetaDocument().getMultiplicitat()) ||
        				MultiplicitatEnumDto.M_1_N.equals(documentEntity.getMetaDocument().getMultiplicitat())) {
        					resultat.add(ResourceReference.toResourceReference(documentEntity.getId(),  documentEntity.getNom()));
        			}
        		}
        	}

        	resource.setDocumentObligatorisAlTancar(resultat);
        }
    }

    private class FollowersPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
            List<ResourceReference<UsuariResource, String>> seguidors = entity.getSeguidors().stream()
                    .map(usuariResourceEntity -> {
                        UsuariResource usuariResource = objectMappingHelper.newInstanceMap(usuariResourceEntity, UsuariResource.class);
                        return ResourceReference.<UsuariResource, String>toResourceReference(usuariResource.getId(), usuariResource.getCodiAndNom());
                    })
                    .collect(Collectors.toList());
            resource.setSeguidors(seguidors);
        }
    }

    private class AgafarActionExecutor implements ActionExecutor<ExpedientResourceEntity, ExpedientResource.MassiveAction, Serializable> {

        @Override
        public Serializable exec(String code, ExpedientResourceEntity entity, ExpedientResource.MassiveAction params) throws ActionExecutionException {
        	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth!=null) {
            	if (params.isMassivo()) {
	            	List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
	    			ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(ExecucioMassivaTipusDto.AGAFAR_EXPEDIENT, new Date(), null, configHelper.getRolActual());
	    			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
	    			execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);
            	} else {
            		entityComprovarHelper.comprovarExpedient(
            				params.getIds().get(0),
            				false,
            				false,
            				true,
            				false,
            				false,
            				null);
            		expedientHelper.agafar(params.getIds().get(0), auth.getName());
            	}
            }
            return null;
        }

        @Override
        public void onChange(Serializable id, ExpedientResource.MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientResource.MassiveAction target) {}
    }
    private class AlliberarActionExecutor implements ActionExecutor<ExpedientResourceEntity, ExpedientResource.MassiveAction, Serializable> {

        @Override
        public Serializable exec(String code, ExpedientResourceEntity entity, ExpedientResource.MassiveAction params) throws ActionExecutionException {
        	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth!=null) {
            	if (params.isMassivo()) {
	            	List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
	    			ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(ExecucioMassivaTipusDto.ALLIBERAR_EXPEDIENT, new Date(), null, configHelper.getRolActual());
	    			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
	    			execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);
            	} else {
            		entityComprovarHelper.comprovarExpedient(
            				params.getIds().get(0),
            				false, //Agafat per usuari actual
            				false, //Permis read
            				true,  //Permis write
            				false, //Permis create
            				false, //Permis delete
            				null);
            		expedientHelper.alliberar(params.getIds().get(0));
            	}
            }
            return null;
        }

        @Override
        public void onChange(Serializable id, ExpedientResource.MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientResource.MassiveAction target) {}
    }
    private class RetornarActionExecutor implements ActionExecutor<ExpedientResourceEntity, ExpedientResource.MassiveAction, Serializable> {

		@Override
		public Serializable exec(String code, ExpedientResourceEntity entity, MassiveAction params) throws ActionExecutionException {
        	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth!=null) {
            	if (params.isMassivo()) {
	            	List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
	    			ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(ExecucioMassivaTipusDto.RETORNAR_EXPEDIENT, new Date(), null, configHelper.getRolActual());
	    			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
	    			execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);
            	} else {
            		//Comprovam que l'expedient esta agafat per el usuari actual.
		        	entityComprovarHelper.comprovarExpedient(
		        			params.getIds().get(0),
		        			true,
		        			false,
		        			false,
		        			false,
		        			false,
		        			configHelper.getRolActual());
            		expedientHelper.retornar(params.getIds().get(0));
            	}
            }
            return null;
		}

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}
    }
    
    private class FollowActionExecutor implements ActionExecutor<ExpedientResourceEntity, ExpedientResource.MassiveAction, Serializable> {

        @Override
        public Serializable exec(String code, ExpedientResourceEntity entity, ExpedientResource.MassiveAction params) throws ActionExecutionException {
        	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth!=null) {
            	if (params.isMassivo()) {
	            	List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
	    			ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(ExecucioMassivaTipusDto.SEGUIR_EXPEDIENT, new Date(), null, configHelper.getRolActual());
	    			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
	    			execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);
            	} else {
            		expedientHelper.follow(params.getIds().get(0), auth.getName());
            	}
            }
            return null;
        }

        @Override
        public void onChange(Serializable id, ExpedientResource.MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientResource.MassiveAction target) {}
    }
    
    private class UnFollowActionExecutor implements ActionExecutor<ExpedientResourceEntity, ExpedientResource.MassiveAction, Serializable> {

        @Override
        public Serializable exec(String code, ExpedientResourceEntity entity, ExpedientResource.MassiveAction params) throws ActionExecutionException {
        	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth!=null) {
            	if (params.isMassivo()) {
	            	List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
	    			ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(ExecucioMassivaTipusDto.UNFOLLOW_EXPEDIENT, new Date(), null, configHelper.getRolActual());
	    			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
	    			execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);
            	} else {
            		expedientHelper.unfollow(params.getIds().get(0), auth.getName());
            	}
            }
            return null;
        }

        @Override
        public void onChange(Serializable id, ExpedientResource.MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientResource.MassiveAction target) {}
    }
    
    private class ReobrirActionExecutor implements ActionExecutor<ExpedientResourceEntity, ExpedientResource.MassiveAction, Serializable> {

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}

		@Override
		public Serializable exec(String code, ExpedientResourceEntity entity, MassiveAction params) throws ActionExecutionException {
			try {
				String entitatActual = configHelper.getEntitatActualCodi();
				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatActual, false, false, false, true, false);
	        	if (params.isMassivo()) {
	        		//TODO: No soportat
	        		throw new ActionExecutionException(getResourceClass(), null, code, "L'accio de reobrir expedient massiu no esta soportada.");
	        	} else {
	        		expedientHelper.reobrir(entitatEntity.getId(), params.getIds().get(0));
	        	}
	        	return null;
			} catch (Exception ex) {
				excepcioLogHelper.addExcepcio("/expedient/ReobrirActionExecutor", ex);
				throw new ActionExecutionException(getResourceClass(), null, code, ex.getMessage());
			}
		}
    }
    
    private class DeleteActionExecutor implements ActionExecutor<ExpedientResourceEntity, ExpedientResource.MassiveAction, Serializable> {

        @Override
        public Serializable exec(String code, ExpedientResourceEntity entity, ExpedientResource.MassiveAction params) throws ActionExecutionException {
        	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth!=null) {
            	String rolActual = configHelper.getRolActual();
            	String entitatActual = configHelper.getEntitatActualCodi();
            	EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatActual, false, false, false, true, false);
            	if (params.isMassivo()) {
	            	List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
	    			ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(ExecucioMassivaTipusDto.ESBORRAR_EXPEDIENT, new Date(), null, rolActual);
	    			execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);
            	} else {
           			try {
						contingutHelper.deleteReversible(entitatEntity.getId(), params.getIds().get(0), null, code);
					} catch (IOException e) {
						excepcioLogHelper.addExcepcio("/expedient/"+entity.getId()+"/delete", e);
						throw new ActionExecutionException(getResourceClass(), entity.getId(), code, e.getMessage());
					}
            	}
            }
            return null;
        }

        @Override
        public void onChange(Serializable id, ExpedientResource.MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientResource.MassiveAction target) {}
    }

    private class TancarActionExecutor implements ActionExecutor<ExpedientResourceEntity, ExpedientResource.TancarExpedientFormAction, Serializable> {
		@Override
		public void onChange(Serializable id, TancarExpedientFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, TancarExpedientFormAction target) {
		}
		@Override
		public Serializable exec(String code, ExpedientResourceEntity entity, TancarExpedientFormAction params) throws ActionExecutionException {
			expedientHelper.tancar(entity.getEntitat().getId(), entity.getId(), params.getMotiu(), params.getDocumentsPerFirmar().toArray(new Long[0]), false);
			return null;
		}
    }

    private <T extends BaseAuditableResource<Long>> Long[] getIdsFromResources(List<ResourceReference<T, Long>> resourcesPerFirmar) {
        List<Long> resultat = new ArrayList<>();
        if (resourcesPerFirmar != null) {
            for (ResourceReference<T, Long> resource : resourcesPerFirmar) {
                resultat.add(resource.getId());
            }
        }
        return resultat.toArray(new Long[0]);
    }
    
    private class ExportOdsGenerator implements ReportGenerator<ExpedientResourceEntity, ExpedientResource.MassiveAction, Serializable> {

    	@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
    		try {
    			ExpedientResource.MassiveAction params = (ExpedientResource.MassiveAction)data.get(1);
				DownloadableFile resultat = new DownloadableFile("BACKGROUND", "application/"+fileType, null);
            	List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
    			ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(ExecucioMassivaTipusDto.EXPORTAR_EXCEL, new Date(), null, configHelper.getRolActual());
    			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
    			execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);				
				return resultat;
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/export/ODS", e);
				throw new ReportGenerationException(ExpedientResource.class, null, code, "S'ha produit un error al exportar a excel els expedients seleccionats.");
			}
    	}
    	
		@Override
		public List<Serializable> generateData(String code, ExpedientResourceEntity entity, MassiveAction params) throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
			parametres.add(entity!=null?entity.getId():0l);
			parametres.add(params);
			return parametres;
		}

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}
    }
    
    private class ExportCsvGenerator implements ReportGenerator<ExpedientResourceEntity, ExpedientResource.MassiveAction, Serializable> {

    	@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
    		try {
    			ExpedientResource.MassiveAction params = (ExpedientResource.MassiveAction)data.get(1);
            	DownloadableFile resultat = new DownloadableFile("BACKGROUND", "application/"+fileType, null);
            	List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
    			ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(ExecucioMassivaTipusDto.EXPORTAR_CSV, new Date(), null, configHelper.getRolActual());
    			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
    			execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);
    			return resultat;
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/export/CSV", e);
				throw new ReportGenerationException(ExpedientResource.class, null, code, "S'ha produit un error al exportar a CSV els expedients seleccionats.");
			}
    	}
    	
		@Override
		public List<Serializable> generateData(String code, ExpedientResourceEntity entity, MassiveAction params) throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
			parametres.add(entity!=null?entity.getId():0l);
			parametres.add(params);
			return parametres;
		}

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}
    }
    
    private class ExportIndexZipGenerator implements ReportGenerator<ExpedientResourceEntity, ExpedientResource.MassiveAction, Serializable> {

    	@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
    		try {
				ExpedientResource.MassiveAction params = (ExpedientResource.MassiveAction)data.get(1);
            	DownloadableFile resultat = new DownloadableFile("BACKGROUND", "application/"+fileType, null);
            	List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
    			ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(ExecucioMassivaTipusDto.EXPORTAR_INDEX_ZIP, new Date(), null, configHelper.getRolActual());
    			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
    			execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);
    			return resultat;
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/ExportIndexZipGenerator", e);
				throw new ReportGenerationException(ExpedientResource.class, null, code, "S'ha produit un error al generar index ZIP dels expedients seleccionats.");
			}
    	}
    	
		@Override
		public List<Serializable> generateData(String code, ExpedientResourceEntity entity, MassiveAction params) throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
			parametres.add(entity!=null?entity.getId():0l);
			parametres.add(params);
			return parametres;
		}

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}
    }

    private class ExportSelectedDocsGenerator implements ReportGenerator<ExpedientResourceEntity, ExpedientResource.MassiveAction, Serializable> {

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}

		@Override
		public List<Serializable> generateData(String code, ExpedientResourceEntity entity, MassiveAction params) throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
			parametres.add(entity!=null?entity.getId():0l);
			parametres.add(params);
			return parametres;
		}
		
    	@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
    		
    		Long expedientId = data.get(0)!=null?(Long)data.get(0):null;
    		
    		try {		
	    		
	    		ExpedientResource.MassiveAction params = (ExpedientResource.MassiveAction)data.get(1);
	    		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);

	        	ExpedientEntity expedientEntity = entityComprovarHelper.comprovarExpedient(
	        			expedientId,
	        			false,
	        			true,
	        			false,
	        			false,
	        			false,
	        			configHelper.getRolActual());
	    		
	        	//Aprofitam la mateixa funció que la de exportar documents de expedient a ZIP, pero modificada per nomes exportar els IDs seleccionats.
	    		return getZipFileDocumentsExpedient(expedientEntity, FileNameOption.ORIGINAL, false, true, params.getIds());
	    		
//	    		//TODO: Convertir els params en llista de ArbreJsonDto
//	    		List<ArbreJsonDto> arbreSeleccionats = new ArrayList<ArbreJsonDto>();
//        		FitxerDto fitxerDto = documentHelper.descarregarAllDocumentsOfExpedientWithSelectedFolders(
//        				entitatEntity.getId(),
//        				expedientId,
//        				arbreSeleccionats, //params.getIds()
//        				configHelper.getRolActual(),
//        				null);
//            	resultat = new DownloadableFile(
//            			fitxerDto.getNom(),
//            			fitxerDto.getContentType(),
//	            		fitxerDto.getContingut());

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/"+expedientId+"/exportarZipMassiu", e);
				throw new ReportGenerationException(ExpedientResource.class, expedientId, code, "S'ha produit un error al generar el index en format PDF per els expedients seleccionats.");
			}
    	}
    }
    
    private class SincronitzarArxiuActionExecutor implements ActionExecutor<ExpedientResourceEntity, ExpedientResource.MassiveAction, Serializable> {

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}

		@Override
		public Serializable exec(String code, ExpedientResourceEntity entity, MassiveAction params) throws ActionExecutionException {
			try {
				List<CodiValorDto> resultat = new ArrayList<>();
				String entitatActual = configHelper.getEntitatActualCodi();
				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatActual, false, false, false, true, false);
	        	if (params.isMassivo()) {
	        		//TODO: No soportat
	        		throw new ActionExecutionException(getResourceClass(), null, code, "L'accio de reobrir expedient massiu no esta soportada.");
	        	} else {
	        		resultat = contingutHelper.sincronitzarEstatArxiu(entitatEntity.getId(), params.getIds().get(0));
	        	}
	        	return (Serializable)resultat;
			} catch (Exception ex) {
				excepcioLogHelper.addExcepcio("/expedient/SincronitzarArxiuActionExecutor", ex);
				throw new ActionExecutionException(getResourceClass(), null, code, ex.getMessage());
			}
		}
    }
    
    private class ExportZipGenerator implements ReportGenerator<ExpedientResourceEntity, ExpedientResource.ExportarDocumentMassiu, Serializable> {

    	@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
    		
    		DownloadableFile resultat = null;
    		Long expedientId = data.get(0)!=null?(Long)data.get(0):null;
    		ExpedientResource.ExportarDocumentMassiu params = (ExpedientResource.ExportarDocumentMassiu)data.get(1);

            if (params.isMassivo()) {
            	
	            if (params.getIds()!=null && !params.getIds().isEmpty()) {
	            	resultat = new DownloadableFile("BACKGROUND", "application/"+fileType, null);
	            	List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
	    			ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(ExecucioMassivaTipusDto.EXPORTAR_ZIP, new Date(), null, configHelper.getRolActual());
	    			execMassDto.setCarpetes(params.isCarpetes());
	    			execMassDto.setVersioImprimible(params.isVersioImprimible());
	    			execMassDto.setNomFitxer(params.getNomFitxer());
	    			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
	    			execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);
	            } else {
					throw new ReportGenerationException(ExpedientResource.class, expedientId, code, "S'ha produit un error al generar ZIP: no hi ha cap expedient seleccionat.");
	            }
	            
            } else {
            	
            	try {
            	
		        	ExpedientEntity expedientEntity = entityComprovarHelper.comprovarExpedient(
		        			expedientId,
		        			false,
		        			true,
		        			false,
		        			false,
		        			false,
		        			configHelper.getRolActual());
		        	
		        	return getZipFileDocumentsExpedient(
		        			expedientEntity,
		        			params.getNomFitxer(),
		        			params.isVersioImprimible(),
		        			params.isCarpetes(),
		        			null);

				} catch (Exception e) {
					excepcioLogHelper.addExcepcio("/expedient/"+expedientId+"/exportarZipMassiu", e);
					throw new ReportGenerationException(ExpedientResource.class, expedientId, code, "S'ha produit un error al generar ZIP per els expedients seleccionats.");
				}
            }
            
            return resultat;
		}
    	
		@Override
		public List<Serializable> generateData(String code, ExpedientResourceEntity entity, ExpedientResource.ExportarDocumentMassiu params)
				throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
			parametres.add(entity!=null?entity.getId():0l);
			parametres.add(params);
			return parametres;
		}

		@Override
		public void onChange(Serializable id, ExportarDocumentMassiu previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, ExportarDocumentMassiu target) {}
    }
    
	private DownloadableFile getZipFileDocumentsExpedient(
			ExpedientEntity expedientEntity,
			FileNameOption nomFitxer,
			boolean versioImprimible,
			boolean carpetes,
			List<Long> idsInclosos) throws IOException {
    	double actualMbFitxer = 0;
    	List<DocumentDto> docsZip = execucioMassivaHelper.getDocumentsForExportacioZip(
    			expedientEntity, nomFitxer, versioImprimible, carpetes, actualMbFitxer, idsInclosos);
	
		ByteArrayOutputStream baos = execucioMassivaHelper.getZipFromDocuments(docsZip);
    	return new DownloadableFile(
        		"documentsExpedient_" + expedientEntity.getNumero() + "_" + Calendar.getInstance().getTimeInMillis() + ".zip",
        		"application/zip",
        		baos.toByteArray());
	}
    
    private class ExportIdexPdfGenerator implements ReportGenerator<ExpedientResourceEntity, ExpedientResource.MassiveAction, Serializable> {

    	@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
    		
    		DownloadableFile resultat = null;
    		Long expedientId = data.get(0)!=null?(Long)data.get(0):null;
    		
    		try {		
	    		
	    		ExpedientResource.MassiveAction params = (ExpedientResource.MassiveAction)data.get(1);
	    		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
	    		
	            if (params.isMassivo()) {
	            	
		            if (params.getIds()!=null && !params.getIds().isEmpty()) {
		            	resultat = new DownloadableFile("BACKGROUND", "application/"+fileType, null);
		            	List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
		    			ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(ExecucioMassivaTipusDto.EXPORTAR_INDEX_PDF, new Date(), null, configHelper.getRolActual());
		    			execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);
		            } else {
						throw new ReportGenerationException(ExpedientResource.class, expedientId, code, "S'ha produit un error al generar el index en format PDF per els expedients seleccionats: no hi ha cap expedient seleccionat.");
		            }
		            
	            } else {
	        		FitxerDto fitxerDto = expedientHelper.generarIndexExpedients(
	        				entitatEntity.getId(),
	        				new HashSet<>(params.getIds()),
	        				false,
	        				"PDF");
	            	resultat = new DownloadableFile(
	            			fitxerDto.getNom(),
	            			fitxerDto.getContentType(),
		            		fitxerDto.getContingut());
	            }

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/"+expedientId+"/exportarZipMassiu", e);
				throw new ReportGenerationException(ExpedientResource.class, expedientId, code, "S'ha produit un error al generar el index en format PDF per els expedients seleccionats.");
			}
            
            return resultat;
		}
    	
		@Override
		public List<Serializable> generateData(String code, ExpedientResourceEntity entity, ExpedientResource.MassiveAction params)
				throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
			parametres.add(entity!=null?entity.getId():0l);
			parametres.add(params);
			return parametres;
		}

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}
    }
    private class ExportIdexXlsGenerator implements ReportGenerator<ExpedientResourceEntity, ExpedientResource.MassiveAction, Serializable> {

    	@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
    		
    		DownloadableFile resultat = null;
    		Long expedientId = data.get(0)!=null?(Long)data.get(0):null;
    		
    		try {	    		
	    		
	    		ExpedientResource.MassiveAction params = (ExpedientResource.MassiveAction)data.get(1);
	    		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
	    		
	            if (params.isMassivo()) {
	            	
		            if (params.getIds()!=null && !params.getIds().isEmpty()) {
		            	resultat = new DownloadableFile("BACKGROUND", "application/"+fileType, null);
		            	List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
		    			ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(ExecucioMassivaTipusDto.EXPORTAR_INDEX_EXCEL, new Date(), null, configHelper.getRolActual());
		    			execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);
		            } else {
						throw new ReportGenerationException(ExpedientResource.class, expedientId, code, "S'ha produit un error al generar el index en format XLSX per els expedients seleccionats: no hi ha cap expedient seleccionat.");
		            }
		            
	            } else {
	        		FitxerDto fitxerDto = expedientHelper.generarIndexExpedients(
	        				entitatEntity.getId(),
	        				new HashSet<>(params.getIds()),
	        				false,
	        				"XLSX");
	            	resultat = new DownloadableFile(
	            			fitxerDto.getNom(),
	            			fitxerDto.getContentType(),
		            		fitxerDto.getContingut());
	            }

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/"+expedientId+"/exportarZipMassiu", e);
				throw new ReportGenerationException(ExpedientResource.class, expedientId, code, "S'ha produit un error al generar el index en format XLSX per els expedients seleccionats.");
			}
            
            return resultat;
		}
    	
		@Override
		public List<Serializable> generateData(String code, ExpedientResourceEntity entity, ExpedientResource.MassiveAction params)
				throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
			parametres.add(entity!=null?entity.getId():0l);
			parametres.add(params);
			return parametres;
		}

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}
    }
    private class ExportEniGenerator implements ReportGenerator<ExpedientResourceEntity, ExpedientResource.MassiveAction, Serializable> {

    	@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
    		
    		DownloadableFile resultat = null;
    		Long expedientId = data.get(0)!=null?(Long)data.get(0):null;
    		
    		try {	    		
	    		
	    		ExpedientResource.MassiveAction params = (ExpedientResource.MassiveAction)data.get(1);
	    		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
	    		
	            if (params.isMassivo()) {
	            	
		            if (params.getIds()!=null && !params.getIds().isEmpty()) {
		            	resultat = new DownloadableFile("BACKGROUND", "application/"+fileType, null);
		            	List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
		    			ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(ExecucioMassivaTipusDto.EXPORTAR_ENI, new Date(), null, configHelper.getRolActual());
		    			execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);
		            } else {
						throw new ReportGenerationException(ExpedientResource.class, expedientId, code, "S'ha produit un error al exportar a ENI els expedients seleccionats: no hi ha cap expedient seleccionat.");
		            }
		            
	            } else {
	        		FitxerDto fitxerDto = expedientHelper.exportarExpedient(new HashSet<>(params.getIds()), false);
	            	resultat = new DownloadableFile(
	            			fitxerDto.getNom(),
	            			fitxerDto.getContentType(),
		            		fitxerDto.getContingut());
	            }

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/"+expedientId+"/exportarEni", e);
				throw new ReportGenerationException(ExpedientResource.class, expedientId, code, "S'ha produit un error al exportar a ENI per els expedients seleccionats.");
			}
            
            return resultat;
		}
    	
		@Override
		public List<Serializable> generateData(String code, ExpedientResourceEntity entity, ExpedientResource.MassiveAction params)
				throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
			parametres.add(entity!=null?entity.getId():0l);
			parametres.add(params);
			return parametres;
		}

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}
    }

    private class ExportIndexEniGenerator implements ReportGenerator<ExpedientResourceEntity, ExpedientResource.MassiveAction, Serializable> {

    	@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
    		
    		DownloadableFile resultat = null;
    		Long expedientId = data.get(0)!=null?(Long)data.get(0):null;
    		
    		try {	    		
	    		
	    		ExpedientResource.MassiveAction params = (ExpedientResource.MassiveAction)data.get(1);
	    		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
	    		
	            if (params.isMassivo()) {
					throw new ReportGenerationException(ExpedientResource.class, expedientId, code, "La funcio de Índex PDF i exportació ENI no esta implementada massivament.");
	            } else {
	        		FitxerDto fitxerDto = expedientHelper.generarIndexExpedients(
	        				entitatEntity.getId(),
	        				new HashSet<>(params.getIds()),
	        				true,
	        				"PDF");
	            	resultat = new DownloadableFile(
	            			fitxerDto.getNom(),
	            			fitxerDto.getContentType(),
		            		fitxerDto.getContingut());
	            }

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/"+expedientId+"/exportarEni", e);
				throw new ReportGenerationException(ExpedientResource.class, expedientId, code, "S'ha produit un error al exportar a ENI per els expedients seleccionats.");
			}
            
            return resultat;
		}
    	
		@Override
		public List<Serializable> generateData(String code, ExpedientResourceEntity entity, ExpedientResource.MassiveAction params)
				throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
			parametres.add(entity!=null?entity.getId():0l);
			parametres.add(params);
			return parametres;
		}

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}
    }

    private class ExportIdexInsideGenerator implements ReportGenerator<ExpedientResourceEntity, ExpedientResource.MassiveAction, Serializable> {

    	@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
    		
    		DownloadableFile resultat = null;
    		Long expedientId = data.get(0)!=null?(Long)data.get(0):null;
    		
    		try {	    		
	    		
	    		ExpedientResource.MassiveAction params = (ExpedientResource.MassiveAction)data.get(1);
	    		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
	    		
	            if (params.isMassivo()) {
	            	
		            if (params.getIds()!=null && !params.getIds().isEmpty()) {
		            	resultat = new DownloadableFile("BACKGROUND", "application/"+fileType, null);
		            	List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
		    			ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(ExecucioMassivaTipusDto.EXPORTAR_INSIDE, new Date(), null, configHelper.getRolActual());
		    			execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);
		            } else {
						throw new ReportGenerationException(ExpedientResource.class, expedientId, code, "S'ha produit un error al exportar a format INSIDE els expedients seleccionats: no hi ha cap expedient seleccionat.");
		            }
		            
	            } else {
	        		FitxerDto fitxerDto = expedientHelper.exportarExpedient(new HashSet<>(params.getIds()), true);
	            	resultat = new DownloadableFile(
	            			fitxerDto.getNom(),
	            			fitxerDto.getContentType(),
		            		fitxerDto.getContingut());
	            }

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/"+expedientId+"/exportarEni", e);
				throw new ReportGenerationException(ExpedientResource.class, expedientId, code, "S'ha produit un error al exportar a format INSIDE els expedients seleccionats.");
			}
            
            return resultat;
		}
    	
		@Override
		public List<Serializable> generateData(String code, ExpedientResourceEntity entity, ExpedientResource.MassiveAction params)
				throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
			parametres.add(entity!=null?entity.getId():0l);
			parametres.add(params);
			return parametres;
		}

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}
    }
    
    // OnChangeLogicProcessor
    private class MetaExpedientOnchangeLogicProcessor implements OnChangeLogicProcessor<ExpedientResource> {
        @Override
        public void onChange(
		        Serializable id,
		        ExpedientResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                ExpedientResource target) {

            if (fieldValue != null) {
                ResourceReference<MetaExpedientResource, Long> reference =
                        (ResourceReference<MetaExpedientResource, Long>) fieldValue;
                Optional<MetaExpedientResourceEntity> metaExpedientResourceOptional =
                        metaExpedientResourceRepository.findById(reference.getId());

                metaExpedientResourceOptional.ifPresent((metaExpedientResourceEntity) -> {
                    MetaExpedientResource metaExpedientResource =
                            objectMappingHelper.newInstanceMap(metaExpedientResourceEntity, MetaExpedientResource.class);
                    if (metaExpedientResource.getOrganGestor() != null) {
                        target.setOrganGestor(metaExpedientResource.getOrganGestor());
                        target.setDisableOrganGestor(true);
                        if (previous.getAny() != null) {
                            Optional<Long> sequencia = metaExpedientSequenciaResourceRepository
                                    .findValorByMetaExpedientAndAny(metaExpedientResourceEntity, previous.getAny());

                            sequencia.ifPresentOrElse(
                                    (value) -> target.setSequencia(value + 1),
                                    () -> target.setSequencia(1L)
                            );
                        }
                    }
                });
            } else {
                target.setOrganGestor(null);
                target.setSequencia(null);
            }
        }
    }
    private class AnyOnchangeLogicProcessor implements OnChangeLogicProcessor<ExpedientResource> {
        @Override
        public void onChange(
		        Serializable id,
		        ExpedientResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                ExpedientResource target) {

            if (fieldValue != null && previous.getMetaExpedient() != null) {
                Optional<MetaExpedientResourceEntity> metaExpedientResourceOptional =
                        metaExpedientResourceRepository.findById(previous.getMetaExpedient().getId());

                metaExpedientResourceOptional.ifPresent((metaExpedientResourceEntity) -> {
                    MetaExpedientResource metaExpedientResource =
                            objectMappingHelper.newInstanceMap(metaExpedientResourceEntity, MetaExpedientResource.class);
                    if (metaExpedientResource.getOrganGestor() != null) {
                        Optional<Long> sequencia = metaExpedientSequenciaResourceRepository
                                .findValorByMetaExpedientAndAny(metaExpedientResourceEntity, (Integer) fieldValue);

                        sequencia.ifPresentOrElse(
                                (value) -> target.setSequencia(value + 1),
                                () -> target.setSequencia(1L)
                        );
                    }
                });
            } else {
                target.setSequencia(null);
            }
        }
    }
    private static class FilterOnchangeLogicProcessor implements FilterProcessor<ExpedientFilterForm> {

        @Override
        public void onChange(Serializable id, ExpedientFilterForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientFilterForm target) {
            switch (fieldName) {
                case ExpedientFilterForm.Fields.dataCreacioInici:
                    if (fieldValue != null && previous.getDataCreacioFinal() != null
                            && previous.getDataCreacioFinal().isBefore((ChronoLocalDateTime<?>) fieldValue)) {
                        target.setDataCreacioInici(null);
                    }
                    break;
                case ExpedientFilterForm.Fields.dataCreacioFinal:
                    if (fieldValue != null && previous.getDataCreacioInici() != null
                            && previous.getDataCreacioInici().isAfter((ChronoLocalDateTime<?>) fieldValue)) {
                        target.setDataCreacioFinal(null);
                    }
                    break;
                case ExpedientFilterForm.Fields.agafatPer:
                    if (previous.getAgafat()!=null && fieldValue!=null){
                        target.setAgafat(null);
                    }
                    break;
                case ExpedientFilterForm.Fields.agafat:
                    if(previous.getAgafatPer()!=null && (Boolean) fieldValue){
                        target.setAgafatPer(null);
                    }
                    break;
            }
        }
    }
}
