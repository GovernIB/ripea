package es.caib.ripea.service.resourceservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.Hibernate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.plugins.arxiu.api.Expedient;
import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.ExpedientEstatEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.DocumentResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.ExpedientResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientSequenciaResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.persistence.repository.ContingutMovimentRepository;
import es.caib.ripea.persistence.repository.ContingutRepository;
import es.caib.ripea.persistence.repository.DadaRepository;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.ExpedientEstatRepository;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.persistence.repository.ExpedientTascaRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ApplicationHelper;
import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.CarpetaHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ContingutHelper;
import es.caib.ripea.service.helper.DocumentHelper;
import es.caib.ripea.service.helper.DominiHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.ExecucioMassivaHelper;
import es.caib.ripea.service.helper.ExpedientHelper;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.helper.MetaDocumentHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.helper.ZipImportacioHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.base.model.FieldOption;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.ReportFileType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ArxiuDetallDto;
import es.caib.ripea.service.intf.dto.CodiValorDto;
import es.caib.ripea.service.intf.dto.DocumentAmbTipusDto;
import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.ElementTipusEnumDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaContingutDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaTipusDto;
import es.caib.ripea.service.intf.dto.FileNameOption;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.ImportacioDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import es.caib.ripea.service.intf.dto.PermisosPerExpedientsDto;
import es.caib.ripea.service.intf.dto.ResultatConsultaDto;
import es.caib.ripea.service.intf.dto.SiNoEnumDto;
import es.caib.ripea.service.intf.dto.TipusRegistreEnumDto;
import es.caib.ripea.service.intf.model.ContingutResource;
import es.caib.ripea.service.intf.model.DocumentResource;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.ExpedientEstatResource;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.model.ExpedientResource.ExpedientFilterForm;
import es.caib.ripea.service.intf.model.ExpedientResource.ExportarDocumentMassiu;
import es.caib.ripea.service.intf.model.ExpedientResource.ImportarDocumentsForm;
import es.caib.ripea.service.intf.model.ExpedientResource.ImportarDocumentsZipForm;
import es.caib.ripea.service.intf.model.ExpedientResource.ImportarExpedientFormAction;
import es.caib.ripea.service.intf.model.ExpedientResource.MassiveImportDocsAction;
import es.caib.ripea.service.intf.model.ExpedientResource.TancarExpedientFormAction;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.model.MetaExpedientOrganGestorResource;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
import es.caib.ripea.service.intf.model.NodeResource.MassiveAction;
import es.caib.ripea.service.intf.model.UsuariResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.permission.ExtendedPermission;
import es.caib.ripea.service.resourcehelper.ContingutLogResourceHelper;
import es.caib.ripea.service.resourcehelper.ContingutResourceHelper;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientResourceServiceImpl extends BaseMutableResourceService<ExpedientResource, Long, ExpedientResourceEntity> implements ExpedientResourceService {

	private final EntitatRepository entitatRepository;
	private final ExpedientRepository expedientRepository;
	private final ExpedientTascaRepository expedientTascaRepository;
	private final OrganGestorRepository organGestorRepository;
	private final ExpedientEstatRepository expedientEstatRepository;
    private final DocumentResourceRepository documentResourceRepository;
	private final DadaRepository dadaRepository;
	private final ContingutRepository contingutRepository;
	private final ContingutMovimentRepository contingutMovimentRepository;	
	
    private final UsuariResourceRepository usuariResourceRepository;
    private final UsuariRepository usuariRepository;
    private final ExpedientResourceRepository expedientResourceRepository;
    private final MetaExpedientResourceRepository metaExpedientResourceRepository;
    private final MetaExpedientSequenciaResourceRepository metaExpedientSequenciaResourceRepository;
    private final MetaExpedientRepository metaExpedientRepository;

    private final ContingutResourceHelper contingutResourceHelper;
    private final ApplicationHelper applicationHelper;
    private final PluginHelper pluginHelper;
    private final CacheHelper cacheHelper;
    private final DominiHelper dominiHelper;
    private final ConfigHelper configHelper;
    private final CarpetaHelper carpetaHelper;
    private final ExpedientHelper expedientHelper;
    private final ContingutHelper contingutHelper;
    private final DocumentHelper documentHelper;
    private final EntityComprovarHelper entityComprovarHelper;
    private final ExcepcioLogHelper excepcioLogHelper;
    private final ExecucioMassivaHelper execucioMassivaHelper;
    private final MetaDocumentHelper metaDocumentHelper;
    private final ZipImportacioHelper zipImportacioHelper;
    private final MessageHelper messageHelper;
    private final ContingutLogResourceHelper contingutLogResourceHelper;

    @PostConstruct
    public void init() {
        
    	//Exportar docs a ZIP amb formulari previ. Massiu o individual.
    	register(ExpedientResource.REPORT_MASSIVE_EXPORT_PDF_CODE,	new ExportZipGenerator());
    	//Exportar info expedients a EXCEL sense formulari previ. Nomes massiu de moment.
        register(ExpedientResource.REPORT_MASSIVE_EXPORT_ODS_CODE,	new ExportOdsGenerator());
        //Exportar info expedients a CSV sense formulari previ. Nomes massiu de moment.
        register(ExpedientResource.REPORT_MASSIVE_EXPORT_CSV_CODE,	new ExportCsvGenerator());
        //Genera els indexos dels expedients seleccionats i els comprimeix. Nomes massiu de moment.
        register(ExpedientResource.REPORT_MASSIVE_EXPORT_INDEX_ZIP, new ExportIndexZipGenerator());
        //Genera els indexos dels expedients seleccionats en PDF. Massiu o individual.
        register(ExpedientResource.REPORT_MASSIVE_EXPORT_INDEX_PDF, new ExportIdexPdfGenerator());
        //Genera els indexos dels expedients seleccionats en EXCEL. Massiu o individual.
        register(ExpedientResource.REPORT_MASSIVE_EXPORT_INDEX_XLS, new ExportIdexXlsGenerator());
        //Genera els indexos dels expedients seleccionats en PDF i els comprimeix en ZIP. Nomes individual.
        register(ExpedientResource.REPORT_MASSIVE_EXPORT_INDEX_ENI, new ExportIndexEniGenerator());
        register(ExpedientResource.REPORT_MASSIVE_EXPORT_ENI, 		new ExportEniGenerator());
        register(ExpedientResource.REPORT_MASSIVE_EXPORT_INSIDE, 	new ExportIdexInsideGenerator());
        register(ExpedientResource.REPORT_PLANTILLA_EXCEL_INTERESSATS, 	new PlantillaExcelInteressatsReportGenerator());
        register(ExpedientResource.REPORT_PLANTILLA_DADES_CSV, 	new PlantillaDadesCsvReportGenerator());
        //Genera un Zip de los documentos seleccionados para un expediente concreto
        register(ExpedientResource.REPORT_EXPORT_SELECTED_DOCS, new ExportSelectedDocsGenerator());
        
        register(ExpedientResource.ACTION_MASSIVE_AGAFAR_CODE, new AgafarActionExecutor());
        register(ExpedientResource.ACTION_MASSIVE_ALLIBERAR_CODE, new AlliberarActionExecutor());
        register(ExpedientResource.ACTION_MASSIVE_RETORNAR_CODE, new RetornarActionExecutor());
        register(ExpedientResource.ACTION_MASSIVE_FOLLOW_CODE, new FollowActionExecutor());
        register(ExpedientResource.ACTION_MASSIVE_UNFOLLOW_CODE, new UnFollowActionExecutor());
        register(ExpedientResource.ACTION_MASSIVE_DELETE_CODE, new DeleteActionExecutor());
        register(ExpedientResource.ACTION_MASSIVE_REOBRIR_CODE, new ReobrirActionExecutor());
        register(ExpedientResource.ACTION_MASSIVE_IMPORT_DOCS, new ImportarDocumentsMassiu());
        register(ExpedientResource.ACTION_MASSIVE_RELACIONAR_CODE, new RelacionarActionExecutor());

        register(ExpedientResource.ACTION_TANCAR_CODE, new TancarActionExecutor());
        register(ExpedientResource.ACTION_IMPORTAR_CODE, new ImportarActionExecutor());
        register(ExpedientResource.ACTION_SYNC_ARXIU, new SincronitzarArxiuActionExecutor());
        register(ExpedientResource.ACTION_IMPORT_DOCS, new ImportarDocumentsArxiuActionExecutor());
        register(ExpedientResource.ACTION_IMPORT_DOCS_ZIP, new ImportarDocumentsZipArxiuActionExecutor());
        register(ExpedientResource.ACTION_IMPORT_INTE, new ImportarInteressatsArxiuActionExecutor());

        register(ExpedientResource.PERSPECTIVE_AMB_PINBAL_CODE, new AmbDocumentsPinbalPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_FOLLOWERS, new FollowersPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_COUNT, new CountPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_INTERESSATS_CODE, new InteressatsPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_ESTAT_CODE, new EstatPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_META_EXPEDIENT_CODE, new MetaExpedientPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_RELACIONAT_CODE, new RelacionatPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_ARXIU_EXPEDIENT, new ArxiuExpedientPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_NOTIFICACIONS_CADUCADES, new NotificacionsCaducadesPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_DOCUMENTS_NO_MOGUTS, new DocumentsNoMogutsPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_DOCUMENTS_OBLIGATORIS_TANCAR, new DocumentsObligatorisAlTancarPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_PATH_CODE, new PathPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_AUDIT_CODE, new AuditoriaPerspectiveApplicator());
        
        register(ExpedientResource.Fields.metaExpedient, new MetaExpedientOnchangeLogicProcessor());
        register(ExpedientResource.Fields.any, new AnyOnchangeLogicProcessor());
        register(ExpedientResource.FILTER_CODE, new FilterOnchangeLogicProcessor());
        //register(null, new InitialOnChangeExpedientResourceLogicProcessor());
    }
    
    @Override
    protected <P> Specification<P> toProcessedSpecification(
        String quickFilter,
        String filter,
        String[] namedFilters) {

		Specification<P> processedSpecification = getSpringFilterSpecification(
				buildSpringFilterForQuickFilter(
						getResourceClass(),
						null,
						quickFilter));
		
		processedSpecification = appendSpecificationWithAnd(processedSpecification, getSpringFilterSpecification(filter));
		processedSpecification = appendSpecificationWithAnd(processedSpecification, 
				getSpringFilterSpecification(
						additionalSpringFilter(filter, namedFilters)));
		
		if (namedFilters != null) {
			for (String namedFilter: namedFilters) {
				Specification<P> namedSpecification = null;
				String namedSpringFilter = namedFilterToSpringFilter(namedFilter);
				if (namedSpringFilter != null) {
					namedSpecification = getSpringFilterSpecification(namedSpringFilter);
				} else {
					namedSpecification = namedFilterToSpecification(namedFilter);
				}
				processedSpecification = appendSpecificationWithAnd(processedSpecification, namedSpecification);
			}
		}
		Specification<P> finalSpecification = processSpecification(processedSpecification);
		
		Specification<P> distinctSpecification = (root, query, criteriaBuilder) -> {
			if (finalSpecification!=null) {
				query.distinct(true);
//				root.join("organGestorPares", JoinType.LEFT);	
//				Join<ExpedientResourceEntity, ExpedientOrganPareResourceEntity> join = root.join("organGestorPares", JoinType.LEFT);
				return finalSpecification.toPredicate(root, query, criteriaBuilder);
			} else {
				return null;
			}
		};
		
		return distinctSpecification != null ? distinctSpecification : Specification.where(null);
    }
    
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
        Timer.Sample sample = Timer.start(applicationHelper.getMeterRegistry());
        
        try {
    	
            // En cas de no disposar d'entitat actual, filtrarem per un string "................................................................................"
            // amb una mida superior a la mida màxima del camp codi de manera que asseguram que no es retornin resultats un cop aplicat el filtre
            String entitatActualCodi = configHelper.getEntitatActualCodi();
            String organActualCodi	 = configHelper.getOrganActualCodi();
            String rolActual		 = configHelper.getRolActual();
        	
            Filter filtreFrontAndEntitat = FilterBuilder.and(
                    (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                    FilterBuilder.equal(MetaExpedientResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
                    		entitatActualCodi != null?entitatActualCodi:"................................................................................")
            );
        	
        	Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
        	if (mapaNamedQueries.size()>0 && mapaNamedQueries.containsKey("WITHOUT_PERMISION_CHECK")) {
                return filtreFrontAndEntitat.generate();
        	}
       
    		Filter filtreNoEliminats = FilterBuilder.and(FilterBuilder.equal(ContingutResource.Fields.esborrat, "0"));
    		
			Filter filtrePermisosVaris = null;
			Filter filtreProcedimentsDirectes = null;
			Filter filtreExpOrgansComuns = null;
			Filter filtreGrupsPermesos = null;
			Filter filtreOrgansPermesos = null;
			
            EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true, false);
            OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitatEntity.getId(), organActualCodi);
			
    		PermisosPerExpedientsDto permisosPerExpedients = expedientHelper.findPermisosPerExpedients(
    				entitatEntity.getId(),
    				rolActual,
    				ogEntity!=null?ogEntity.getId():null);
			
			if (!rolActual.equals("IPA_ADMIN") && !rolActual.equals("IPA_ADMIN_LECTURA")) {

	    		//Si no es té permis per cap banda, no retornam resultats
	    		if (permisosPerExpedients.capPermis()) {
	    			return FilterBuilder.equal("id", 0).generate();
	    		}				
				
				filtrePermisosVaris = getFiltrePermisos(permisosPerExpedients);

				//APLICA FILTRE PERMIS DIRECTE A PROCEDIMENTS
				if (!rolActual.equals("IPA_ADMIN") && !rolActual.equals("IPA_SUPER") && !rolActual.equals("IPA_ADMIN_LECTURA") && 
					permisosPerExpedients.getIdsMetaExpedientsPermesos()!=null) {
					
					String campPermisDir = ExpedientResource.Fields.metaExpedient + "." + MetaExpedientResource.Fields.permisDirecte;
					String procedimentId = ExpedientResource.Fields.metaExpedient + ".id";
					
					Filter filtreProcedimentPermisDirecte = Filter.parse(campPermisDir + "!true");
					Filter filtreProcediments = null;
			    	List<String> permesosClausulesIn = Utils.getIdsEnGruposMil(permisosPerExpedients.getIdsMetaExpedientsPermesos());
			        for (String aux: permesosClausulesIn) {
				        if (aux != null && !aux.isEmpty()) {
				        	filtreProcediments = FilterBuilder.or(filtreProcediments, Filter.parse(procedimentId + " IN (" + aux + ")"));
				        }
			        }
			        
			        filtreProcedimentsDirectes = FilterBuilder.or(filtreProcedimentPermisDirecte, filtreProcediments);
				}
				
				//APLICA FILTRE DE GRUPS
				/** (:noFiltreGrups = true or (e.grup is null or (:esNullIdsGrupsPermesos = false and e.grup.id in (:idsGrupsPermesos)))) */
				if (!rolActual.equals("IPA_ADMIN") && !rolActual.equals("IPA_ORGAN_ADMIN") && !rolActual.equals("IPA_ADMIN_LECTURA")) {
					String grupId = ExpedientResource.Fields.grup + ".id";
					List<String> grupsClausulesIn = Utils.getIdsEnGruposMil(permisosPerExpedients.getIdsGrupsPermesos());
					if (grupsClausulesIn!=null && grupsClausulesIn.size()>0) {
						for (String aux: grupsClausulesIn) {
							if (aux != null && !aux.isEmpty()) {
								filtreGrupsPermesos = FilterBuilder.or(filtreGrupsPermesos, Filter.parse(grupId + " IN (" + aux + ")"));
							}
						}
						filtreGrupsPermesos = FilterBuilder.or(Filter.parse(ExpedientResource.Fields.grup + " IS NULL"), filtreGrupsPermesos);
					} else {
						filtreGrupsPermesos = Filter.parse(ExpedientResource.Fields.grup + " IS NULL");
					}
				}
			}
			
			//Si ets rol tothom, ara mateix estarien arribant tots els expedients de procediments comuns
			//Pero nomes volem els dels OGs amb permisos o nivells inferiors, no superiors
			if (rolActual.equals("tothom") && permisosPerExpedients.getIdsProcedimentsComuns()!=null) {
/*				String campProcComu	 = ExpedientResource.Fields.metaExpedient + "." + MetaExpedientResource.Fields.organGestor;
				String campOgExpedient = ExpedientResource.Fields.organGestor + ".id";
				Filter filtreProcedimentComu = Filter.parse(campProcComu + " IS NOT NULL");
				Filter filtreOgComunsExpedient = null;
				
				List<String> ogsComunsAndFillsClausulesIn = Utils.getIdsEnGruposMil(permisosPerExpedients.getIdsOrgansComunsAndFills());
				if (ogsComunsAndFillsClausulesIn!=null && ogsComunsAndFillsClausulesIn.size()>0) {
					for (String aux: ogsComunsAndFillsClausulesIn) {
						if (aux != null && !aux.isEmpty()) {
							filtreOgComunsExpedient = FilterBuilder.or(filtreOgComunsExpedient, Filter.parse(campOgExpedient + " IN (" + aux + ")"));
						}
					}
				}
				
				filtreExpOrgansComuns = FilterBuilder.or(filtreProcedimentComu, filtreOgComunsExpedient);*/
			}
			
			String expOgId = ExpedientResource.Fields.organGestor + ".id";
			List<String> ogsClausulesIn = Utils.getIdsEnGruposMil(permisosPerExpedients.getIdsOrgansPermesos());
			if (ogsClausulesIn!=null && ogsClausulesIn.size()>0) {
				for (String aux: ogsClausulesIn) {
					if (aux != null && !aux.isEmpty()) {
						filtreOrgansPermesos = FilterBuilder.or(filtreOrgansPermesos, Filter.parse(expOgId + " IN (" + aux + ")"));
					}
				}
			}
			
			Filter filtreResultat = FilterBuilder.and(
					filtreFrontAndEntitat,
					filtreNoEliminats,
					filtrePermisosVaris,
					filtreProcedimentsDirectes,
//					filtreExpOrgansComuns,
					filtreOrgansPermesos,
					filtreGrupsPermesos);
			
			return filtreResultat.generate();
        
    	} catch (Exception e) {
    		applicationHelper.stopTimer(sample, "METRICS@Subsystem_Expedient.list", "resultado", "error");
    		throw e;
    	}
    }

    private Filter getFiltrePermisos(PermisosPerExpedientsDto permisosPerExpedients) {
    	
    	/**
	 	"and (" +
		"     (:esNullIdsMetaExpedientsPermesos = false and (e.metaExpedient.id in (:idsMetaExpedientsPermesos0))) " +
		"     or (:esNullIdsOrgansPermesos = false and (meogp.organGestor.id in (:idsOrgansPermesos0))) " +
		"     or (:esNullIdsMetaExpedientOrganPairsPermesos = false and meogp.id in (:idsMetaExpedientOrganPairsPermesos)) " +
		"     or (:esNullIdsOrgansAmbProcedimentsComunsPermesos = false and meogp.organGestor.id in (:idsOrgansAmbProcedimentsComunsPermesos) and e.metaExpedient.id in (:idsProcedimentsComuns))
		) " +
    	 */
    	
    	Filter filtrePermisos = null;
    	
    	/** (:esNullIdsMetaExpedientsPermesos = false and (e.metaExpedient.id in (:idsMetaExpedientsPermesos0))) " */
    	//MetaExpedients permesos: ExtendedPermission.READ --> MetaNodeEntity.class
    	Filter filtreMetaExpedientsPermesos = null;
    	String procedimentId = ExpedientResource.Fields.metaExpedient + ".id";
    	
    	List<Long> procedimentsPermesosComunsAndNotComuns = new ArrayList<Long>();
    	if (permisosPerExpedients.getIdsMetaExpedientsPermesos()!=null) {
    		procedimentsPermesosComunsAndNotComuns.addAll(permisosPerExpedients.getIdsMetaExpedientsPermesos());
    	}
    	if (permisosPerExpedients.getIdsProcedimentsComuns()!=null) {
    		procedimentsPermesosComunsAndNotComuns.addAll(permisosPerExpedients.getIdsProcedimentsComuns());
    	}
    	
    	List<String> permesosClausulesIn = Utils.getIdsEnGruposMil(procedimentsPermesosComunsAndNotComuns);
    	if (permesosClausulesIn!=null) {
	        for (String aux: permesosClausulesIn) {
		        if (aux != null && !aux.isEmpty()) {
		        	filtreMetaExpedientsPermesos = FilterBuilder.or(filtreMetaExpedientsPermesos, Filter.parse(procedimentId + " IN (" + aux + ")"));
		        }
	        }
    	}
		
    	/** (:esNullIdsOrgansPermesos = false and (meogp.organGestor.id in (:idsOrgansPermesos0))) */
    	//Organs gestors permesos (nomes admin organ): Organ actual capçalera + fills
    	Filter filtreOrgansPermesos = null;
	  	String campOrganId = ExpedientResource.Fields.metaExpedient + "." + MetaExpedientResource.Fields.metaExpedientOrganGestors + "." + MetaExpedientOrganGestorResource.Fields.organGestor + ".id";
	    List<String> organsActualAndFillsClausulesIn = Utils.getIdsEnGruposMil(permisosPerExpedients.getIdsOrgansPermesos());
	    if (organsActualAndFillsClausulesIn!=null) {
	    	for (String aux: organsActualAndFillsClausulesIn) {
	    		if (aux != null && !aux.isEmpty()) {
	    			filtreOrgansPermesos = FilterBuilder.or(filtreOrgansPermesos, Filter.parse(campOrganId + " IN (" + aux + ")"));
	    		}
	    	}
	    }
    	
    	//MetaExpedientOrganPairsPermesos (nomes usuaris tothom): ExtendedPermission.READ --> MetaExpedientOrganGestorEntity.class
	    //Permisos que s'han donat sobre procediments comuns, a on es pot seleccionar organ gestor.
	    /** (:esNullIdsMetaExpedientOrganPairsPermesos = false and meogp.id in (:idsMetaExpedientOrganPairsPermesos)) */
    	Filter filtreMetaExpedientOrganPairsPermesos = null;
	  	String campMetaExpOrganId = ExpedientResource.Fields.metaExpedient + "." + MetaExpedientResource.Fields.metaExpedientOrganGestors + ".id";
	    List<String> organsMetaExpClausulesIn = Utils.getIdsEnGruposMil(permisosPerExpedients.getIdsMetaExpedientOrganPairsPermesos());
	    if (organsMetaExpClausulesIn!=null) {
	    	for (String aux: organsMetaExpClausulesIn) {
	    		if (aux != null && !aux.isEmpty()) {
	    			filtreMetaExpedientOrganPairsPermesos = FilterBuilder.or(filtreMetaExpedientOrganPairsPermesos, Filter.parse(campMetaExpOrganId + " IN (" + aux + ")"));
	    		}
	    	}
	    }
    	
    	//OrgansAmbProcedimentsComunsPermesos (nomes usuaris tothom): ExtendedPermission.COMU + ExtendedPermission.READ --> OrganGestorEntity.class
	    //Permisos que s'han donat sobre OrganGestor
	    /** (:esNullIdsOrgansAmbProcedimentsComunsPermesos = false and meogp.organGestor.id in (:idsOrgansAmbProcedimentsComunsPermesos) 
	     * 	and e.metaExpedient.id in (:idsProcedimentsComuns)) */
    	Filter filtreOrgansAmbProcedimentsComunsPermesos = null;
	  	String campMetaExpOrganComuId = ExpedientResource.Fields.metaExpedient + "." + MetaExpedientResource.Fields.metaExpedientOrganGestors + "." + MetaExpedientOrganGestorResource.Fields.organGestor + ".id";
	    List<String> organsMetaExpComunsClausulesIn = Utils.getIdsEnGruposMil(permisosPerExpedients.getIdsOrgansAmbProcedimentsComunsPermesos());
	    if (organsMetaExpComunsClausulesIn!=null) {
	    	for (String aux: organsMetaExpComunsClausulesIn) {
	    		if (aux != null && !aux.isEmpty()) {
	    			filtreOrgansAmbProcedimentsComunsPermesos = FilterBuilder.or(filtreOrgansAmbProcedimentsComunsPermesos, Filter.parse(campMetaExpOrganComuId + " IN (" + aux + ")"));
	    		}
	    	}
	    	
	    	Filter filtreIdsProcedimentsComuns = null;
		  	String campMetaExpId = ExpedientResource.Fields.metaExpedient + ".id";
		    List<String> idsProcedimentsComunsClausulesIn = Utils.getIdsEnGruposMil(permisosPerExpedients.getIdsProcedimentsComuns());
		    if (idsProcedimentsComunsClausulesIn!=null) {
		    	for (String aux: idsProcedimentsComunsClausulesIn) {
		    		if (aux != null && !aux.isEmpty()) {
		    			filtreIdsProcedimentsComuns = FilterBuilder.or(filtreIdsProcedimentsComuns, Filter.parse(campMetaExpId + " IN (" + aux + ")"));
		    		}
		    	}
		    }
		    
		    if (filtreOrgansAmbProcedimentsComunsPermesos!=null) {
		    	filtreOrgansAmbProcedimentsComunsPermesos = FilterBuilder.and(filtreOrgansAmbProcedimentsComunsPermesos, filtreIdsProcedimentsComuns);
		    }
	    }
    	
    	filtrePermisos = FilterBuilder.or(
    			filtreMetaExpedientsPermesos,
    			filtreOrgansPermesos,
    			filtreMetaExpedientOrganPairsPermesos
    			, filtreOrgansAmbProcedimentsComunsPermesos);
    	
    	return filtrePermisos;
    }
    
    @Override
    protected void afterConversion(ExpedientResourceEntity entity, ExpedientResource resource) {
        resource.setGestioAmbGrupsActiva(entity.getMetaExpedient().isGestioAmbGrupsActiva());
        usuariResourceRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .ifPresent(usuariResourceEntity -> resource.setSeguidor(entity.getSeguidors().contains(usuariResourceEntity)));
        resource.setUsuariActualWrite(entityComprovarHelper.comprovarPermisExpedient(entity.getId(), ExtendedPermission.WRITE, "WRITE", false));
        ExpedientEntity expedientEntity = expedientRepository.findById(entity.getId()).get();
        
        resource.setConteDocuments(CollectionUtils.isNotEmpty(documentResourceRepository.findByExpedientAndEsborrat(entity, 0)));
        resource.setConteDocumentsDefinitius(documentResourceRepository.expedientHasDocumentsDefinitius(entity));
        resource.setConteDocumentsEnProcessDeFirma(CollectionUtils.isNotEmpty(documentResourceRepository.findEnProccessDeFirma(entity)));
        resource.setConteDocumentsDePortafirmesNoCustodiats(CollectionUtils.isNotEmpty(documentResourceRepository.findDocumentsDePortafirmesNoCustodiats(entity)));
        resource.setConteDocumentsPendentsReintentsArxiu(CollectionUtils.isNotEmpty(documentResourceRepository.findDocumentsPendentsReintentsArxiu(entity, contingutHelper.getArxiuMaxReintentsDocuments())));
//        resource.setConteDocumentsDeAnotacionesNoMogutsASerieFinal(CollectionUtils.isNotEmpty(registreAnnexRepository.findDocumentsDeAnotacionesNoMogutsASerieFinalByExpedientId(entity.getId())));
        
        resource.setErrors(cacheHelper.findErrorsValidacioPerNode(entity.getId(), true));
        resource.setValid(resource.getErrors().isEmpty());

        resource.setPotTancar(
                resource.isValid()
                        && resource.isConteDocuments()
                        && !resource.isConteDocumentsEnProcessDeFirma()
                        && !resource.isConteDocumentsDePortafirmesNoCustodiats()
                        && !resource.isConteDocumentsPendentsReintentsArxiu()
//                        && !resource.isConteDocumentsDeAnotacionesNoMogutsASerieFinal()
        );
        if(!resource.isPotTancar()) {
            if (!resource.isValid()) resource.setTancarDisabledMessage(messageHelper.getMessage("contingut.errors.expedient.validacio"));
            if (!resource.isConteDocuments()) resource.setTancarDisabledMessage(messageHelper.getMessage("disabled.button.msg.noConteCapDocument"));
            if (resource.isConteDocumentsEnProcessDeFirma()) resource.setTancarDisabledMessage(messageHelper.getMessage("disabled.button.msg.conteDocumentsEnProcessDeFirma"));
            if (resource.isConteDocumentsDePortafirmesNoCustodiats()) resource.setTancarDisabledMessage(messageHelper.getMessage("disabled.button.msg.conteDocumentsDePortafirmesNoCustodiats"));
            if (resource.isConteDocumentsPendentsReintentsArxiu()) resource.setTancarDisabledMessage(messageHelper.getMessage("disabled.button.msg.conteDocumentsPendentsReintentsArxiu"));
//            if (resource.isConteDocumentsDeAnotacionesNoMogutsASerieFinal()) resource.setTancarDisabledMessage(messageHelper.getMessage("disabled.button.msg.conteDocumentsDeAnotacionesNoMogutsASerieFinal"));
        }
        
        resource.setErrorLastEnviament(cacheHelper.hasEnviamentsPortafirmesAmbErrorPerExpedient(expedientEntity));
		resource.setErrorLastNotificacio(cacheHelper.hasNotificacionsAmbErrorPerExpedient(expedientEntity));
		resource.setAmbEnviamentsPendents(cacheHelper.hasEnviamentsPortafirmesPendentsPerExpedient(expedientEntity.getId()));
		resource.setAmbNotificacionsPendents(cacheHelper.hasNotificacionsPendentsPerExpedient(expedientEntity));
		resource.setDataDarrerEnviament(cacheHelper.getDataDarrerEnviament(expedientEntity));
		resource.setPotModificar(entityComprovarHelper.comprovarSiEsPotModificarExpedient(expedientEntity));
		UsuariEntity usuariEntity = usuariRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName()).orElse(null);
		if (usuariEntity!=null && entity.getId()!=null) {
			resource.setPotModificarContingut(expedientTascaRepository.countTasquesResponsableExpedient(usuariEntity, entity.getId())>0);
    	}
		resource.setHasEsborranys(documentResourceRepository.hasFillsEsborranys(expedientEntity.getId()));
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
					resource.getPrioritatMotiu(),
					resource.isAsignarSeguidor()?SiNoEnumDto.SI:SiNoEnumDto.NO);
			
			expedientHelper.arxiuPropagarExpedientAmbInteressatsNewTransaction(expedientId);
			
			ExpedientResource resultat = new ExpedientResource();
			resultat.setId(expedientId);
			resultat.setNom(resource.getNom());
			return resultat;
			
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/expedient/create", ex);
    		throw ex;
    	}
    }

    private class AmbDocumentsPinbalPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
            List<MetaDocumentEntity> metaDocuments = metaDocumentHelper.findMetaDocumentsPinbalDisponiblesPerCreacio(entity.getId());
            resource.setAmbDocumentsPinbal(metaDocuments!=null && !metaDocuments.isEmpty());
        }
    }

    private class PathPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
            resource.setTreePath(contingutResourceHelper.getTreePath(entity));
        }
    }
    
    private class AuditoriaPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
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
    }

    private class CountPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
            resource.setNumInteressats((int) entity.getInteressats().stream().filter(interessatResourceEntity -> !interessatResourceEntity.isEsRepresentant()).count());
            resource.setNumTasques(entity.getTasques().size());
            resource.setNumAnotacions(entity.getPeticions().size());
            resource.setNumPublicacions(entity.getPublicacions().size());
            resource.setNumRemeses(entity.getNotificacions().size());
            resource.setNumMetaDades(entity.getMetaExpedient().getMetaDades()!=null?entity.getMetaExpedient().getMetaDades().size():0);
            resource.setNumDades(dadaRepository.countByNodeId(entity.getId()));
            resource.setNumContingut(documentResourceRepository.countAllByExpedientIdAndEsborrat(entity.getId(), 0));
            resource.setNumMoviments(contingutMovimentRepository.countByContingutId(entity.getId()));
            resource.setNumComentaris(entity.getComentaris().size());
            resource.setNumSeguidors(entity.getSeguidors().size());
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
    
    private class MetaExpedientPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
        	resource.setMetaExpedientInfo(objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getMetaExpedient()), MetaExpedientResource.class));
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
                    .filter(expedientResourceEntity -> expedientResourceEntity.getEsborrat()==0)
                    .map(expedientResourceEntity -> objectMappingHelper.newInstanceMap(expedientResourceEntity, ExpedientResource.class))
                    .map(expedientResource -> ResourceReference.<ExpedientResource, Long>toResourceReference(
                            expedientResource.getId(),
                            "["+ expedientResource.getSequencia() +"/"+ expedientResource.getAny() +"] "+ expedientResource.getNom()
                    ))
                    .collect(Collectors.toList());
            resource.setRelacionatsAmb(relacionatsAmb);

            List<ResourceReference<ExpedientResource, Long>> relacionatsPer = entity.getRelacionatsPer().stream()
                    .filter(expedientResourceEntity -> expedientResourceEntity.getEsborrat()==0)
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
            EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi());
            ArxiuDetallDto arxiu = contingutResourceHelper.getArxiuExpedientDetall(entitatEntity.getId(), arxiuExpedient);
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

    private class RelacionarActionExecutor implements ActionExecutor<ExpedientResourceEntity, ExpedientResource.RelacionarAction, Serializable> {

        @Override
        public Serializable exec(String code, ExpedientResourceEntity entity, ExpedientResource.RelacionarAction params) throws ActionExecutionException {
            List<ExpedientResourceEntity> expedientResourceEntityList = expedientResourceRepository.findAllById(params.getIds());
            if (ExpedientResource.RelacionarAction.Action.ADD.equals(params.getAction())) {
                for (ExpedientResourceEntity expedientResourceEntity : expedientResourceEntityList) {
                    if (!entity.getRelacionatsAmb().contains(expedientResourceEntity)) {
                        entity.getRelacionatsAmb().add(expedientResourceEntity);
                        contingutLogResourceHelper.crearRelacioExpedientLog(entity, expedientResourceEntity.getId());
                    }
                }

                // REMOVE IF UNSELECT
                List<ExpedientResourceEntity> toRemove = new ArrayList<>();
                for (ExpedientResourceEntity expedientResourceEntity : entity.getRelacionatsAmb()) {
                    if (!expedientResourceEntityList.contains(expedientResourceEntity)) {
                        toRemove.add(expedientResourceEntity);
                    }
                }
                for (ExpedientResourceEntity expedientResourceEntity : toRemove) {
                    entity.getRelacionatsAmb().remove(expedientResourceEntity);
                    contingutLogResourceHelper.eliminarRelacioExpedientLog(entity, expedientResourceEntity.getId());
                }
            } else {
                for (ExpedientResourceEntity expedientResourceEntity : expedientResourceEntityList) {
                    if (entity.getRelacionatsAmb().contains(expedientResourceEntity)) {
                        entity.getRelacionatsAmb().remove(expedientResourceEntity);
                        contingutLogResourceHelper.eliminarRelacioExpedientLog(entity, expedientResourceEntity.getId());
                    }
                    if (entity.getRelacionatsPer().contains(expedientResourceEntity)) {
                        entity.getRelacionatsPer().remove(expedientResourceEntity);
                        contingutLogResourceHelper.eliminarRelacioExpedientLog(entity, expedientResourceEntity.getId());
                    }
                }
            }
            return objectMappingHelper.newInstanceMap(entity, ExpedientResource.class);
        }

        @Override
        public void onChange(Serializable id, ExpedientResource.RelacionarAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientResource.RelacionarAction target) {}
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
            		expedientHelper.agafar(params.getIds().get(0), auth.getName(), null);
            	}
            }
            return objectMappingHelper.newInstanceMap(entity, ExpedientResource.class);
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
            return objectMappingHelper.newInstanceMap(entity, ExpedientResource.class);
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
            return objectMappingHelper.newInstanceMap(entity, ExpedientResource.class);
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
            return objectMappingHelper.newInstanceMap(entity, ExpedientResource.class);
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
            return objectMappingHelper.newInstanceMap(entity, ExpedientResource.class);
        }

        @Override
        public void onChange(Serializable id, ExpedientResource.MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientResource.MassiveAction target) {}
    }
    
    private class ImportarDocumentsMassiu implements ActionExecutor<ExpedientResourceEntity, ExpedientResource.MassiveImportDocsAction, Serializable> {

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

        @Override
        public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
        	
            List<FieldOption> resultat = new ArrayList<>();
            
            if (MassiveImportDocsAction.Fields.tipusDocument.equals(fieldName)) {
                
                MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.findById(Long.parseLong(requestParameterMap.get(MassiveImportDocsAction.Fields.metaExpedientId)[0])).get();
                
//                String entitatActualCodi = configHelper.getEntitatActualCodi(); //Arribava null per la darrera fila
//                EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
                
                EntitatEntity entitat = metaExpedientEntity.getEntitat();
                List<MetaDocumentEntity>  metaDocsPermesos = metaDocumentHelper.findMetaDocumentsDisponiblesPerCreacio(
                        entitat,
                        null,
                        metaExpedientEntity,
                        false);
                if (metaDocsPermesos!=null) {
                    String[] additionalOptionArr = requestParameterMap.get("tipusDocument")[0].split(",", -1);
                    List<String> additionalOption = Arrays.asList(additionalOptionArr);

                    String value = requestParameterMap.containsKey("value")
                            ? requestParameterMap.get("value")[0]
                            : null;

                    for (MetaDocumentEntity metaDoc : metaDocsPermesos) {
                        if (metaDoc.isMultiple() ||
                                (
                                        !additionalOption.contains(String.valueOf(metaDoc.getId())) ||
                                                String.valueOf(metaDoc.getId()).equals(value)
                                )
                        ) {
                            resultat.add(new FieldOption(metaDoc.getId().toString(), metaDoc.getNom()));
                        }
                    }
                    resultat.sort(Comparator.comparing(FieldOption::getDescription));
                }
            }
            return resultat;
        }

        @Override
		public void onChange(Serializable id, MassiveImportDocsAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveImportDocsAction target) {
            if (fieldName == null) {
                if (!previous.getIds().isEmpty()) {
                    List<ExpedientResourceEntity> expedients = expedientResourceRepository.findAllById(previous.getIds());
                    Long metaExpedientId = expedients.get(0).getMetaExpedient().getId();
                    target.setMetaExpedientId(metaExpedientId);

                    boolean allSame = expedients.stream()
                            .allMatch(e -> Objects.equals(e.getMetaExpedient().getId(), metaExpedientId));

                    target.setTotsExpedientsMateixProcediment(allSame);
                } else {
                    target.setTotsExpedientsMateixProcediment(false);
                }
            }
        }

		@Override
		public Serializable exec(String code, ExpedientResourceEntity entity, MassiveImportDocsAction params) throws ActionExecutionException {

			try {
			
				if (params!=null && params.getDocuments()!=null && params.getDocuments().size()>0) {
				
					//1.- Guardar fitxers temporalment a disc: en un sol fitxer ZIP amb els objectes passats a fitxers JSON
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
		            ZipOutputStream zipOut = new ZipOutputStream(baos);
					for (int i = 0; i < params.getDocuments().size(); i++) {
						DocumentAmbTipusDto doc = params.getDocuments().get(i);
						// Convertir el objeto a JSON
						ObjectMapper objectMapper = new ObjectMapper();
						String docAsString = objectMapper.writeValueAsString(doc);
						// Crear una entrada en el ZIP
						String entryName = "document_" + (i + 1) + ".json";
						ZipEntry zipEntry = new ZipEntry(entryName);
						zipOut.putNextEntry(zipEntry);
		                // Escribir el JSON en la entrada
		                byte[] jsonBytes = docAsString.getBytes("UTF-8");
		                zipOut.write(jsonBytes, 0, jsonBytes.length);
		                zipOut.closeEntry();
					}
					
//					FitxerDto resultat = new FitxerDto();
//					resultat.setNom(expedient.getNom().replaceAll(" ", "_") + ".zip");
//					resultat.setContentType("application/zip");
//					resultat.setContingut(baos.toByteArray());
					
					String gestioDocumentalAdjuntId = pluginHelper.gestioDocumentalCreate(
								PluginHelper.GESDOC_AGRUPACIO_DOCS_ESBORRANYS,
								new ByteArrayInputStream(baos.toByteArray()));
					
					//2.- Programar la acció massiva per els expedient seleccionats.
					List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
					ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(
							ExecucioMassivaTipusDto.IMPORTAR_DOCS,
							new Date(),
							null,
							configHelper.getRolActual());
					execMassDto.setDocumentNom(gestioDocumentalAdjuntId);
					
		        	String entitatActual = configHelper.getEntitatActualCodi();
		        	EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatActual, false, false, false, true, false);
					execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);
				}
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/"+entity.getId()+"/ImportarDocumentsMassiu", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, messageHelper.getMessage("message.common.action.error")+": "+e.getMessage());
			}
			return objectMappingHelper.newInstanceMap(entity, ExpedientResource.class);
		}    	
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
	        		//Reobrir expedient massiu no esta soportada, pero deixam la porta oberta a futures implementacións. Tendria sentit.
	        		throw new ActionExecutionException(getResourceClass(), null, code, "expedient.reobrir.massive.notSupported");
	        	} else {
	        		expedientHelper.reobrir(entitatEntity.getId(), params.getIds().get(0));
	        	}
	        	return objectMappingHelper.newInstanceMap(entity, ExpedientResource.class);
			} catch (Exception ex) {
				excepcioLogHelper.addExcepcio("/expedient/ReobrirActionExecutor", ex);
				String message = messageHelper.getMessage("message.common.action.error")+": "+ex.getMessage();
				throw new ActionExecutionException(getResourceClass(), entity==null?null:entity.getId(), code, message);
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
						String message = messageHelper.getMessage("message.common.action.error")+": "+e.getMessage();
						throw new ActionExecutionException(getResourceClass(), entity==null?null:entity.getId(), code, message);
					}
            	}
            }
            return objectMappingHelper.newInstanceMap(entity, ExpedientResource.class);
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
			try {
				expedientHelper.tancar(entity.getEntitat().getId(), entity.getId(), params.getMotiu(), params.getDocumentsPerFirmar().toArray(new Long[0]), false);
				return objectMappingHelper.newInstanceMap(entity, ExpedientResource.class);
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/"+entity.getId()+"/TancarActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, messageHelper.getMessage("expedient.tancar.reject", new Object[]{e.getMessage()}));
			}
		}
    }
    
    private class ImportarActionExecutor implements ActionExecutor<ExpedientResourceEntity, ExpedientResource.ImportarExpedientFormAction, Serializable> {

		@Override
		public void onChange(Serializable id, ImportarExpedientFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, ImportarExpedientFormAction target) {}

		@Override
		public Serializable exec(String code, ExpedientResourceEntity entity, ImportarExpedientFormAction params) throws ActionExecutionException {
			try {
            	String rolActual = configHelper.getRolActual();
            	String entitatActual = configHelper.getEntitatActualCodi();
				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatActual, false, false, false, true, false);
				expedientHelper.importarExpedient(entitatEntity.getId(), entity.getId(), params.getExpedientOrigen().getId(), rolActual);
				return objectMappingHelper.newInstanceMap(entity, ExpedientResource.class);
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/"+entity.getId()+"/TancarActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, messageHelper.getMessage("expedient.importar.reject", new Object[]{e.getMessage()}));
			}	
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
				throw new ReportGenerationException(getResourceClass(), null, code, "expedient.export.ods.reject");
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
				throw new ReportGenerationException(getResourceClass(), null, code, "expedient.export.csv.reject");
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
				throw new ReportGenerationException(getResourceClass(), null, code, "expedient.export.indexZip.reject");
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

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/"+expedientId+"/exportarZipMassiu", e);
				throw new ReportGenerationException(getResourceClass(), expedientId, code, messageHelper.getMessage("expedient.export.indexPdf.reject", new Object[]{e.getMessage()}));
			}
    	}
    }
    
    private class ImportarDocumentsZipArxiuActionExecutor implements ActionExecutor<ExpedientResourceEntity, ExpedientResource.ImportarDocumentsZipForm, Serializable> {

		@Override
		public void onChange(Serializable id, ImportarDocumentsZipForm previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, ImportarDocumentsZipForm target) {}

		@Override
		public Serializable exec(String code, ExpedientResourceEntity entity, ImportarDocumentsZipForm params) throws ActionExecutionException {
		    try {
		        EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
		        ContingutEntity pare = contingutRepository.findById(entity.getId()).orElseThrow();

		        FileReference zipFile = params.getDocumentZip();
		        try (InputStream inputStream = new ByteArrayInputStream(zipFile.getContent())) {
		            int total = zipImportacioHelper.descomprimirZip(
		                    inputStream,
		                    configHelper.getRolActual(),
		                    pare.getId(),
		                    null, // tascaId
		                    entitatEntity.getId()
		            );
		            log.info("S'han importat {} documents des del ZIP", total);
		        }

		        return objectMappingHelper.newInstanceMap(entity, ExpedientResource.class);
		    } catch (Exception ex) {
		        excepcioLogHelper.addExcepcio("/expedient/" + entity.getId() + "ImportarDocumentsZipArxiuActionExecutor", ex);
		        String message = messageHelper.getMessage("message.common.action.error") + ": " + ex.getMessage();
		        throw new ActionExecutionException(getResourceClass(), entity.getId(), code, message);
		    }			
		}
    }
    
    private class ImportarDocumentsArxiuActionExecutor implements ActionExecutor<ExpedientResourceEntity, ExpedientResource.ImportarDocumentsForm, Serializable> {

		@Override
		public void onChange(Serializable id, ImportarDocumentsForm previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, ImportarDocumentsForm target) {}

		@Override
		public Serializable exec(String code, ExpedientResourceEntity entity, ImportarDocumentsForm params) throws ActionExecutionException {
			try {
				String entitatActual = configHelper.getEntitatActualCodi();
				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatActual, false, false, false, true, false);
				Long destiId = entity.getId();
				if (params.getCarpeta()==null) {
					if (Utils.hasValue(params.getNovaCarpetaNom())) {
						destiId = carpetaHelper.create(
								entitatEntity.getId(),
								entity.getId(),
								params.getNovaCarpetaNom(),
								false,
								null,
								false,
								null, 
								false, 
								null, 
								true).getId();
					}
				} else {
					destiId = params.getCarpeta().getId();
				}
				ImportacioDto importacioDto = new ImportacioDto();
				importacioDto.setTipusImportacio(params.getTipusImportacio());
				importacioDto.setNumeroRegistre(params.getNumeroRegistre());
				importacioDto.setCodiEni(params.getCodiEni());
				importacioDto.setDataPresentacioFormatted(params.getDataPresentacio());
				importacioDto.setTipusRegistre(TipusRegistreEnumDto.ENTRADA);
				importacioDto.setDestiId(String.valueOf(destiId));
				
				contingutHelper.importarDocuments(entitatEntity.getId(), destiId, importacioDto, new HashMap<String, String>(), new ArrayList<DocumentDto>());
				return objectMappingHelper.newInstanceMap(entity, ExpedientResource.class);
			} catch (Exception ex) {
				excepcioLogHelper.addExcepcio("/expedient/"+entity.getId()+"ImportarDocumentsArxiuActionExecutor", ex);
				String message = messageHelper.getMessage("message.common.action.error")+": "+ex.getMessage();
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, message);
			}
		}
    }
    private class ImportarInteressatsArxiuActionExecutor implements ActionExecutor<ExpedientResourceEntity, ExpedientResource.ImportarInteressatsForm, Serializable> {

		@Override
		public void onChange(Serializable id, ExpedientResource.ImportarInteressatsForm previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, ExpedientResource.ImportarInteressatsForm target) {}

		@Override
		public Serializable exec(String code, ExpedientResourceEntity entity, ExpedientResource.ImportarInteressatsForm params) throws ActionExecutionException {
			return null;
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
	        		//Sincronitzar expedient amb arxiu massivament no esta soportada, pero deixam la porta oberta a futures implementacions. Tendria sentit.
	        		throw new ActionExecutionException(getResourceClass(), null, code, "expedient.sincronitzarArxiu.massive.notSupported");
	        	} else {
	        		resultat = contingutHelper.sincronitzarEstatArxiu(entitatEntity.getId(), params.getIds().get(0));
	        	}
	        	return (Serializable)resultat;
			} catch (Exception ex) {
				excepcioLogHelper.addExcepcio("/expedient/SincronitzarArxiuActionExecutor", ex);
				String message = messageHelper.getMessage("message.common.action.error")+": "+ex.getMessage();
				throw new ActionExecutionException(getResourceClass(), entity==null?null:entity.getId(), code, message);
			}
		}
    }
    
    private class ExportZipGenerator implements ReportGenerator<ExpedientResourceEntity, ExpedientResource.ExportarDocumentMassiu, Serializable> {

    	@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
    		DownloadableFile resultat = null;
    		ExpedientResource.ExportarDocumentMassiu params = (ExpedientResource.ExportarDocumentMassiu)data.get(0);

            if (params.isMassivo()) {
                resultat = new DownloadableFile("BACKGROUND", "application/"+fileType, null);
                List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
                ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(ExecucioMassivaTipusDto.EXPORTAR_ZIP, new Date(), null, configHelper.getRolActual());
                execMassDto.setCarpetes(params.isCarpetes());
                execMassDto.setVersioImprimible(params.isVersioImprimible());
                execMassDto.setNomFitxer(params.getNomFitxer());
                EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
                execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);
            } else {
                Long expedientId = params.getIds().get(0);
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
					throw new ReportGenerationException(getResourceClass(), expedientId, code, messageHelper.getMessage("expedient.export.zip.reject", new Object[]{e.getMessage()}));
				}
            }
            
            return resultat;
		}
    	
		@Override
		public List<Serializable> generateData(String code, ExpedientResourceEntity entity, ExpedientResource.ExportarDocumentMassiu params)
				throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
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
                    resultat = new DownloadableFile("BACKGROUND", "application/"+fileType, null);
                    List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
                    ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(ExecucioMassivaTipusDto.EXPORTAR_INDEX_PDF, new Date(), null, configHelper.getRolActual());
                    execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);
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
				throw new ReportGenerationException(getResourceClass(), expedientId, code, "expedient.export.indexPdf.reject");
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
                    resultat = new DownloadableFile("BACKGROUND", "application/"+fileType, null);
                    List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
                    ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(ExecucioMassivaTipusDto.EXPORTAR_INDEX_EXCEL, new Date(), null, configHelper.getRolActual());
                    execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);
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
				throw new ReportGenerationException(getResourceClass(), expedientId, code, "expedient.export.indexXlsx.reject");
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
                    resultat = new DownloadableFile("BACKGROUND", "application/"+fileType, null);
                    List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
                    ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(ExecucioMassivaTipusDto.EXPORTAR_ENI, new Date(), null, configHelper.getRolActual());
                    execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);
	            } else {
	        		FitxerDto fitxerDto = expedientHelper.exportarExpedient(new HashSet<>(params.getIds()), false);
	            	resultat = new DownloadableFile(
	            			fitxerDto.getNom(),
	            			fitxerDto.getContentType(),
		            		fitxerDto.getContingut());
	            }

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/"+expedientId+"/exportarEni", e);
				throw new ReportGenerationException(getResourceClass(), expedientId, code, "expedient.export.eni.reject");
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
					throw new ReportGenerationException(getResourceClass(), expedientId, code, "expedient.export.pdfeni.massive.reject");
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
				throw new ReportGenerationException(getResourceClass(), expedientId, code, "expedient.export.eni.reject");
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

    private class PlantillaDadesCsvReportGenerator implements ReportGenerator<ExpedientResourceEntity, Serializable, Serializable> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

		@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
			try {
				DownloadableFile resultat = new DownloadableFile(
	        			"model_dades_importacio_zip.csv",
	        			"text/csv",
	        			this.getClass().getResourceAsStream("/es/caib/ripea/core/templates/model_dades_importacio_zip.csv").readAllBytes());
				return resultat;
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/PlantillaDadesCsvReportGenerator", e);
				throw new ReportGenerationException(getResourceClass(), 0, code, "expedient.export.plantillaExcelInteressats.reject");
			}
		}
		
		@Override
		public List<Serializable> generateData(String code, ExpedientResourceEntity entity, Serializable params) throws ReportGenerationException {
			return null;
		} 	
    }

    private class PlantillaExcelInteressatsReportGenerator implements ReportGenerator<ExpedientResourceEntity, Serializable, Serializable> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

		@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
			try {
				DownloadableFile resultat = new DownloadableFile(
	        			"model_dades_interessats.xlsx",
	        			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
	        			this.getClass().getResourceAsStream("/es/caib/ripea/core/templates/model_dades_interessats.xlsx").readAllBytes());
				return resultat;
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/PlantillaExcelInteressatsReportGenerator", e);
				throw new ReportGenerationException(getResourceClass(), 0, code, "expedient.export.plantillaExcelInteressats.reject");
			}
		}
		
		@Override
		public List<Serializable> generateData(String code, ExpedientResourceEntity entity, Serializable params) throws ReportGenerationException {
			return null;
		}
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
                    resultat = new DownloadableFile("BACKGROUND", "application/"+fileType, null);
                    List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
                    ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(ExecucioMassivaTipusDto.EXPORTAR_INSIDE, new Date(), null, configHelper.getRolActual());
                    execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.EXPEDIENT);
	            } else {
	        		FitxerDto fitxerDto = expedientHelper.exportarExpedient(new HashSet<>(params.getIds()), true);
	            	resultat = new DownloadableFile(
	            			fitxerDto.getNom(),
	            			fitxerDto.getContentType(),
		            		fitxerDto.getContingut());
	            }

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedient/"+expedientId+"/exportarEni", e);
				throw new ReportGenerationException(getResourceClass(), expedientId, code, "expedient.export.inside.reject");
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
            	
                ResourceReference<MetaExpedientResource, Long> reference = (ResourceReference<MetaExpedientResource, Long>) fieldValue;
                Optional<MetaExpedientResourceEntity> metaExpedientResourceOptional = metaExpedientResourceRepository.findById(reference.getId());
                
                metaExpedientResourceOptional.ifPresent((metaExpedientResourceEntity) -> {
                    
                	MetaExpedientResource metaExpedientResource = objectMappingHelper.newInstanceMap(metaExpedientResourceEntity, MetaExpedientResource.class);
                    if (metaExpedientResource.isGestioAmbGrupsActiva()) {
                    	target.setGestioAmbGrupsActiva(true);
                    } else {
                    	target.setGestioAmbGrupsActiva(false);
                    	target.setGrup(null);
                    }
                    
                    //Calcular la sequència
                    if (previous.getAny() != null) {
                        Optional<Long> sequencia = metaExpedientSequenciaResourceRepository
                                .findValorByMetaExpedientAndAny(metaExpedientResourceEntity, previous.getAny());

                        sequencia.ifPresentOrElse(
                                (value) -> target.setSequencia(value + 1),
                                () -> target.setSequencia(1L)
                        );
                    }
                    
                    if (metaExpedientResource.getOrganGestor() != null) {
                        target.setOrganGestor(metaExpedientResource.getOrganGestor());
                        target.setDisableOrganGestor(true);
                    } else {
                    	//Procediment comú
                    	target.setDisableOrganGestor(false);
                    }
                });
            } else {
            	//Sense procediment seleccionat al formulari
                target.setGestioAmbGrupsActiva(false);
                target.setOrganGestor(null);
                target.setDisableOrganGestor(true);
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
    
    private class FilterOnchangeLogicProcessor implements FilterProcessor<ExpedientFilterForm> {

        @Override
        public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
            List<FieldOption> resultat = new ArrayList<FieldOption>();
            if(ExpedientResource.ExpedientFilterForm.Fields.estat.equals(fieldName)) {
                resultat.add(new FieldOption("0", messageHelper.getMessage("es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto.OBERT")));
                resultat.add(new FieldOption("-1", messageHelper.getMessage("es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto.TANCAT")));

                if (requestParameterMap.containsKey("metaExpedientId") && requestParameterMap.get("metaExpedientId").length>0){
                    Long metaExpedientId = Long.valueOf(requestParameterMap.get("metaExpedientId")[0]);
                    List<ExpedientEstatEntity> estatsProcediment = expedientEstatRepository.findByMetaExpedientIdOrderByOrdreAsc(metaExpedientId);

                    for (ExpedientEstatEntity dsp : estatsProcediment) {
                        resultat.add(new FieldOption(dsp.getId().toString(), dsp.getNom()));
                    }
                }
            } else if(ExpedientResource.ExpedientFilterForm.Fields.dominiValor.equals(fieldName)) {
            	String nomCampDomini = ExpedientResource.ExpedientFilterForm.Fields.domini;
            	if (requestParameterMap.containsKey(nomCampDomini) && requestParameterMap.get(nomCampDomini).length>0){
            		Long dominiId = Long.valueOf(requestParameterMap.get(nomCampDomini)[0]);
                    EntitatEntity entitatEntity = entitatRepository.findByCodi(configHelper.getEntitatActualCodi());
                    List<ResultatConsultaDto> dominiValors = dominiHelper.getResultDomini(entitatEntity.getId(), dominiId, "", 1, Integer.MAX_VALUE).getResultat();
                    if (dominiValors!=null) {
                        for (ResultatConsultaDto flx: dominiValors) {
                            resultat.add(new FieldOption(flx.getId(), flx.getText()));
                        }
                    }
            	}
            }
            return resultat;
        }

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
                    if(previous.getAgafatPer()!=null && fieldValue!=null && (Boolean) fieldValue){
                        target.setAgafatPer(null);
                    }
                    break;
            }
        }
    }
}
