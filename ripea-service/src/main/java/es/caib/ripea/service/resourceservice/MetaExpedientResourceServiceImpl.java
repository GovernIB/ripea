package es.caib.ripea.service.resourceservice;

import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.model.*;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.resourceentity.GrupResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.GrupResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaDadaResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaDocumentResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientCarpetaResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientEstatResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientTascaResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.plugin.usuari.DadesUsuari;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.DistribucioReglaHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.GrupHelper;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.helper.MetaExpedientHelper;
import es.caib.ripea.service.helper.PermisosHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.ReportFileType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.model.MetaExpedientResource.DesVincularGrupFormAction;
import es.caib.ripea.service.intf.model.MetaExpedientResource.ImportarFitxerFormAction;
import es.caib.ripea.service.intf.model.MetaExpedientResource.ImportarRolsacFormAction;
import es.caib.ripea.service.intf.model.MetaExpedientResource.RevisioChangeFormAction;
import es.caib.ripea.service.intf.model.MetaExpedientResource.ToggleReglaRolsacFormAction;
import es.caib.ripea.service.intf.model.MetaExpedientResource.VincularGrupFormAction;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.permission.ExtendedPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaExpedientResourceServiceImpl extends BaseMutableResourceService<MetaExpedientResource, Long, MetaExpedientResourceEntity> implements MetaExpedientResourceService {

	private final ConfigHelper configHelper;
	private final CacheHelper cacheHelper;
	private final GrupHelper grupHelper;
	private final MetaExpedientHelper metaExpedientHelper;
	private final DistribucioReglaHelper distribucioReglaHelper;
	private final MetaExpedientResourceRepository metaExpedientResourceRepository;
	private final MetaDocumentResourceRepository metaDocumentResourceRepository;
	private final MetaDadaResourceRepository metaDadaResourceRepository;
	private final MetaExpedientEstatResourceRepository metaExpedientEstatResourceRepository;
	private final MetaExpedientTascaResourceRepository metaExpedientTascaResourceRepository;
	private final MetaExpedientCarpetaResourceRepository metaExpedientCarpetaResourceRepository;
	private final EntityComprovarHelper entityComprovarHelper;
	private final UsuariResourceRepository usuariResourceRepository;
	private final OrganGestorRepository organGestorRepository;
	private final ExpedientRepository expedientRepository;
	private final GrupResourceRepository grupResourceRepository;
	private final PluginHelper pluginHelper;
	private final MessageHelper messageHelper;
	private final PermisosHelper permisosHelper;
	private final ExcepcioLogHelper excepcioLogHelper;

    @PostConstruct
    public void init() {
    	
    	register(MetaExpedientResource.PERSPECTIVE_AUDIT_CODE,		new AuditoriaPerspectiveApplicator());
    	register(MetaExpedientResource.PERSPECTIVE_COMMENTS_CODE,	new ComentarisPerspectiveApplicator());
    	register(MetaExpedientResource.PERSPECTIVE_PERMISOS_CODE,	new PermisosPerspectiveApplicator());
    	register(MetaExpedientResource.PERSPECTIVE_ELEMENTS_CODE,	new ElementsCountPerspectiveApplicator());
    	register(MetaExpedientResource.PERSPECTIVE_ROLSAC_CODE,		new ReglaRolsacPerspectiveApplicator());
    	
    	register(MetaExpedientResource.ACTION_CHANGE_REVISIO_CODE,	new RevisioChangeActionExecutor());
    	register(MetaExpedientResource.ACTION_VINCULAR_GRUP_CODE,	new VincularGrupActionExecutor());
    	register(MetaExpedientResource.ACTION_DESVINCULAR_GRUP_CODE,new DesVincularGrupActionExecutor());
    	register(MetaExpedientResource.ACTION_TOGGLE_REGLA_CODE,	new ToggleReglaActionExecutor());
    	
    	register(MetaExpedientResource.ACTION_IMPORT_ROLSAC_CODE, 	new ImportarRolsacActionExecutor());
    	register(MetaExpedientResource.ACTION_IMPORT_FITXER_CODE,	new ImportarFitxerActionExecutor());
    	
    	register(MetaExpedientResource.REPORT_EXPORT_JSON,			new ExportJsonGenerator());
    	
    	register(MetaExpedientResource.Fields.classificacio,		new OnchangeLogicProcessor());
    	register(MetaExpedientResource.Fields.tipusClassificacio,	new OnchangeLogicProcessor());
    	register(MetaExpedientResource.Fields.organGestor,			new OnchangeLogicProcessor());
    	register(MetaExpedientResource.Fields.procedimentComu,		new OnchangeLogicProcessor());
    }
	
	@Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {

        String entitatActualCodi = configHelper.getEntitatActualCodi();
//        String organActualCodi	 = configHelper.getOrganActualCodi();
        String rolActual		 = configHelper.getRolActual();
//        String organGestorFiltre = Utils.getValorCampFiltre("organGestor.id", currentSpringFilter);
        
		boolean isAdmin = "IPA_ADMIN".equals(rolActual);
		boolean isAdminOrgan = "IPA_ORGAN_ADMIN".equals(rolActual);
//		boolean isDissenyador = "IPA_DISSENY".equals(rolActual);
//		boolean usuariFiltreOrgan = isAdminOrgan || isDissenyador;
        
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
    	Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
		
		Filter filtreBase = null;
		//Si ja ve un filtre definit per entitat, no aplicarem el filtre de entitat actual.
		if (currentSpringFilter==null || !currentSpringFilter.contains("entitat.id")) {
	        filtreBase = FilterBuilder.and(
	                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
	                FilterBuilder.equal(MetaNodeResource.Fields.entitat + "." + EntitatResource.Fields.codi,
	                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
	        );
		}

        List<Long> procsPermesosIds = new ArrayList<Long>();
        List<MetaExpedientEntity> metaExpPermesos = null;
        Filter revisioActiva = null; //Dependrà de la propietat PropertyConfig.METAEXPEDIENT_REVISIO_ACTIVA
        if (mapaNamedQueries.size()>0 && mapaNamedQueries.containsKey("EXPEDIENT_CREATE")) {
            metaExpPermesos = metaExpedientHelper.findAmbPermis(
            		entitat.getId(),
            		ExtendedPermission.CREATE,
            		true, //nomesActius
            		null, //filtreNomOrCodiSia
            		isAdmin,
            		isAdminOrgan,
            		null, //organId
            		false); //comú
            
            if (metaExpedientHelper.isRevisioActiva()) {
            	revisioActiva = Filter.parse(MetaExpedientResource.Fields.revisioEstat+":'"+MetaExpedientRevisioEstatEnumDto.REVISAT.toString()+"'");
            }
            
        } else if (mapaNamedQueries.size()>0 && mapaNamedQueries.containsKey("EXPEDIENT_UPDATE")) {
            metaExpPermesos = metaExpedientHelper.findAmbPermis(
            		entitat.getId(),
            		ExtendedPermission.WRITE,
            		false, //nomesActius
            		null, //filtreNomOrCodiSia
            		isAdmin,
            		isAdminOrgan,
            		null, //organId
            		false); //comú
        } else if (mapaNamedQueries.size()>0 && mapaNamedQueries.containsKey("CONSULTA_REVISIO_ESTAT")) {
        	//Volem replicar metaExpedientServiceImpl.findByEntitat
        	//Nom comprova cap permis, ja que es un manteniment per admins
        	//Nomes filtra per entitat o els filtres del formulari de cerca.
        	return filtreBase.generate();
        } else { //Llistat de procediments
            metaExpPermesos = metaExpedientHelper.findAmbPermis(
            		entitat.getId(),
            		ExtendedPermission.READ,
            		false, //nomesActius
            		null, //filtreNomOrCodiSia
            		isAdmin,
            		isAdminOrgan,
            		null, //organId
            		false); //comú
        }
        
		if (metaExpPermesos==null || metaExpPermesos.size()==0) {
			return FilterBuilder.equal("id", 0).generate();
		} else {
			for (MetaExpedientEntity mee: metaExpPermesos) {
				procsPermesosIds.add(mee.getId());
			}
		}
		
		Filter filtrePermisos = null;
        List<String> permesosClausulesIn = Utils.getIdsEnGruposMil(procsPermesosIds);
        if (permesosClausulesIn!=null) {
	        for (String aux: permesosClausulesIn) {
		        if (aux != null && !aux.isEmpty()) {
		        	filtrePermisos = FilterBuilder.or(filtrePermisos, Filter.parse("id IN (" + aux + ")"));
		        }
	        }
        }
        
		Filter filtreResultat = FilterBuilder.and(filtreBase, revisioActiva, filtrePermisos);
        
        return filtreResultat.generate();
    }

    @Override
    protected void afterConversion(MetaExpedientResourceEntity entity, MetaExpedientResource resource) {
        resource.setNumComentaris(entity.getComentaris().size());
        resource.setProcedimentComu(entity.getOrganGestor()==null);
    }
    
    @Override
    protected void afterCreateSave(MetaExpedientResourceEntity entity, MetaExpedientResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {
		if ("IPA_ORGAN_ADMIN".equals(configHelper.getRolActual())) {
			String entitatActualCodi = configHelper.getEntitatActualCodi();
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
			String organActualCodi	 = configHelper.getOrganActualCodi();
			OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitat.getId(), organActualCodi);
			metaExpedientHelper.canviarRevisioADisseny(entitat.getId(), entity.getId(), ogEntity!=null?ogEntity.getId():null);
		} else {
			entity.setRevisioEstat(MetaExpedientRevisioEstatEnumDto.REVISAT);
			if (resource.isCrearReglaDistribucio()) {
				CrearReglaResponseDto crearReglaResponse = metaExpedientHelper.crearReglaDistribucio(entity.getId());
				if (StatusEnumDto.ERROR.equals(crearReglaResponse.getStatus())) {
					resource.setCrearReglaDistribucioError(crearReglaResponse.getMsg());
				}
			}
		}
    }
    
    @Override
    protected void beforeUpdateSave(MetaExpedientResourceEntity entity, MetaExpedientResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
    	resource.setEstatAnterior(entity.getRevisioEstat());
    }
    
    @Override
    protected void afterUpdateSave(MetaExpedientResourceEntity entity, MetaExpedientResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {
    	
		List<ExpedientEntity> expedients = expedientRepository.findByMetaExpedientIdAndEsborrat(entity.getId(), 0);
		
		for (ExpedientEntity expedient: expedients) {
			cacheHelper.evictErrorsValidacioPerNode(expedient.getId());
		}
		
		String entitatActualCodi = configHelper.getEntitatActualCodi();
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
		
		if ("IPA_ORGAN_ADMIN".equals(configHelper.getRolActual())) {			
			String organActualCodi	 = configHelper.getOrganActualCodi();
			OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitat.getId(), organActualCodi);
			if (MetaExpedientRevisioEstatEnumDto.DISSENY.equals(resource.getEstatAnterior()) && MetaExpedientRevisioEstatEnumDto.PENDENT.equals(entity.getRevisioEstat())) {
				metaExpedientHelper.canviarRevisioAPendentEnviarEmail(entitat.getId(), entity.getId(), ogEntity!=null?ogEntity.getId():null);
			} else {
				metaExpedientHelper.canviarRevisioADisseny(entitat.getId(), entity.getId(), ogEntity!=null?ogEntity.getId():null);
			}
		} else {
			metaExpedientHelper.canviarEstatRevisioASellecionat(
					entitat.getId(),
					entity.getId(),
					entity.getRevisioEstat());
		}
    }

    private class OnchangeLogicProcessor implements OnChangeLogicProcessor<MetaExpedientResource> {
		@Override
		public void onChange(Serializable id, MetaExpedientResource previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, MetaExpedientResource target) {
			
			if (MetaExpedientResource.Fields.procedimentComu.equals(fieldName)) {
				
				if (fieldValue==null || (boolean)fieldValue) {
					target.setOrganGestor(null);
					target.setClassificacio(null);
					target.setTipusClassificacio(TipusClassificacioEnumDto.SIA);
				}
				
			} else if (MetaExpedientResource.Fields.classificacio.equals(fieldName)) {
				
				if (TipusClassificacioEnumDto.SIA.equals(previous.getTipusClassificacio())) {
					
					if (fieldValue==null) {
						target.setMsgSiaRolsac(null);
					} else {
						String codiDir3 = getCodiDir3();
						ProcedimentDto procedimentDto = null;
						String msgSiaRolsac = null;
						try {
							if (codiDir3!=null) {
								procedimentDto = pluginHelper.procedimentFindByCodiSia(codiDir3, fieldValue.toString());
							}
						} catch (Exception e) {
							msgSiaRolsac = messageHelper.getMessage("MetaExpedientResource.msgSiaRolsac.NA");
						}
						
						if (procedimentDto == null) {
							msgSiaRolsac = messageHelper.getMessage("MetaExpedientResource.msgSiaRolsac.NO");
						}
						
						target.setMsgSiaRolsac(msgSiaRolsac);
					}
				}

			} else if (MetaExpedientResource.Fields.tipusClassificacio.equals(fieldName)) {
				 
				if (TipusClassificacioEnumDto.ID.equals(fieldValue)) {
					String idCalculat = null;
					if (previous.getOrganGestor()!=null) {
						idCalculat = getIdCalculadoOrganoGestor(previous.getOrganGestor().getId());
					}
					target.setClassificacio(idCalculat);
				} else {
					target.setClassificacio(null);
				}
				
			} else if (MetaExpedientResource.Fields.organGestor.equals(fieldName)) {
				if (TipusClassificacioEnumDto.ID.equals(previous.getTipusClassificacio())) {
					String idCalculat = null;
					if (fieldValue != null) {
						ResourceReference<OrganGestorResource, Long> resourceReference = (ResourceReference<OrganGestorResource, Long>) fieldValue;
						idCalculat = getIdCalculadoOrganoGestor(resourceReference.getId());
					}
					target.setClassificacio(idCalculat);
				}
			}
		}
    }
    
    private String getCodiDir3() {
		String rolActual = configHelper.getRolActual();
		boolean rolOrgan = rolActual.equals("IPA_DISSENY") || rolActual.equals("IPA_ORGAN_ADMIN");
		String entitatActualCodi = configHelper.getEntitatActualCodi();
		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true, false);
		if (rolOrgan) {
			String organActualCodi	 = configHelper.getOrganActualCodi();
			OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitatEntity.getId(), organActualCodi);
			if (ogEntity!=null) {
				return ogEntity.getCodi();
			}
		} else {
			return entitatEntity.getUnitatArrel();
		}
		return null;
    }
    
    private String getIdCalculadoOrganoGestor(Long organActualId) {
    	try {
    		OrganGestorEntity ogEntity	= organGestorRepository.findById(organActualId).get();
    		return ogEntity.getCodi() +  "_PRO_" + String.format("%030d", System.currentTimeMillis()) + "3F";
    	} catch (Exception e) {
    		return null;	
		}
    }
    
    private class ElementsCountPerspectiveApplicator implements PerspectiveApplicator<MetaExpedientResourceEntity, MetaExpedientResource> {
		@Override
		public void applySingle(String code, MetaExpedientResourceEntity entity, MetaExpedientResource resource) throws PerspectiveApplicationException {
			resource.setNumMetaDocument(metaDocumentResourceRepository.countByMetaExpedientId(entity.getId()));
			resource.setNumMetaDada(metaDadaResourceRepository.countByMetaNodeId(entity.getId()));
			resource.setNumEstat(metaExpedientEstatResourceRepository.countByMetaExpedientId(entity.getId()));
			resource.setNumTasca(metaExpedientTascaResourceRepository.countByMetaExpedientId(entity.getId()));
			List<GrupResourceEntity> grupsProcediment = entity.getGrups();
			resource.setNumGrup(grupsProcediment!=null?grupsProcediment.size():0);
			resource.setNumCarpetes(metaExpedientCarpetaResourceRepository.countByMetaExpedientId(entity.getId()));
		}
    }
    
    private class ReglaRolsacPerspectiveApplicator implements PerspectiveApplicator<MetaExpedientResourceEntity, MetaExpedientResource> {
		@Override
		public void applySingle(String code, MetaExpedientResourceEntity entity, MetaExpedientResource resource) throws PerspectiveApplicationException {
			resource.setRegla(distribucioReglaHelper.consultarRegla(entity.getClassificacio()));
		}
    }    
    
    private class ComentarisPerspectiveApplicator implements PerspectiveApplicator<MetaExpedientResourceEntity, MetaExpedientResource> {
		@Override
		public void applySingle(String code, MetaExpedientResourceEntity entity, MetaExpedientResource resource) throws PerspectiveApplicationException {
			resource.setNumComentaris(entity.getComentaris()!=null?entity.getComentaris().size():0);
		}
    }
    
    private class PermisosPerspectiveApplicator implements PerspectiveApplicator<MetaExpedientResourceEntity, MetaExpedientResource> {
		@Override
		public void applySingle(String code, MetaExpedientResourceEntity entity, MetaExpedientResource resource) throws PerspectiveApplicationException {
			List<PermisDto> permisosGrup = permisosHelper.findPermisos(entity.getId(), MetaExpedientEntity.class); 
			resource.setNumPermisos(permisosGrup!=null?permisosGrup.size():0);
		}
    }
    
    private class AuditoriaPerspectiveApplicator implements PerspectiveApplicator<MetaExpedientResourceEntity, MetaExpedientResource> {
        @Override
        public void applySingle(String code, MetaExpedientResourceEntity entity, MetaExpedientResource resource) throws PerspectiveApplicationException {
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
    
    private class ImportarRolsacActionExecutor implements ActionExecutor<MetaExpedientResourceEntity, MetaExpedientResource.ImportarRolsacFormAction, MetaExpedientResource> {
		@Override
		public void onChange(Serializable id, ImportarRolsacFormAction previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, ImportarRolsacFormAction target) {
		}
		@Override
		public MetaExpedientResource exec(String code, MetaExpedientResourceEntity entity, ImportarRolsacFormAction params) throws ActionExecutionException {
			try {
				String codiDir3 = getCodiDir3();
				ProcedimentDto procedimentDto = pluginHelper.procedimentFindByCodiSia(codiDir3, params.getCodiSia());
				if (procedimentDto!=null) {
					MetaExpedientResource aux = new MetaExpedientResource();
					aux.setTipusClassificacio(TipusClassificacioEnumDto.SIA);
					aux.setClassificacio(params.getCodiSia());
					aux.setNom(procedimentDto.getNom());
					aux.setDescripcio(procedimentDto.getResum());
					aux.setProcedimentComu(procedimentDto.isComu());
					if (procedimentDto != null && procedimentDto.getUnitatOrganitzativaCodi() != null && !procedimentDto.getUnitatOrganitzativaCodi().isEmpty()) {
						OrganGestorEntity organEntity = organGestorRepository.findByEntitatCodiAndCodi(
								configHelper.getEntitatActualCodi(),
								procedimentDto.getUnitatOrganitzativaCodi());
						if (organEntity != null) {
							aux.setOrganGestor(ResourceReference.toResourceReference(
									organEntity.getId(),
									organEntity.getCodiINom()));
						}
					}
					return aux;
				} else {
					throw new ActionExecutionException(
							getResourceClass(),
							null,
							code,
							messageHelper.getMessage("MetaExpedientResourceServiceImpl.ImportarRolsacActionExecutor.notFound")
							+" "+params.getCodiSia()+" Dir3: "+codiDir3);
				}
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/metaExpedient/ImportarRolsacActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), null, code, messageHelper.getMessage("message.common.action.error")+": "+e.getMessage());
			}
		}
    }
    
    private class ImportarFitxerActionExecutor implements ActionExecutor<MetaExpedientResourceEntity, MetaExpedientResource.ImportarFitxerFormAction, Serializable> {
		@Override
		public void onChange(Serializable id, ImportarFitxerFormAction previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, ImportarFitxerFormAction target) {
			if (fieldName != null) {
				if (ImportarFitxerFormAction.Fields.importJson.equals(fieldName) && fieldValue != null) {

					try {
					
						ObjectMapper objectMapper = new ObjectMapper();
						objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
						objectMapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
						
						String jsonString = new String(((FileReference)fieldValue).getContent(), StandardCharsets.UTF_8);
						
						MetaExpedientExportDto metaExpedientExport = objectMapper.readValue(jsonString, MetaExpedientExportDto.class);
						
						//Convertir MetaExpedientExportDto a MetaExpedientResource
						target.setTipusClassificacio(metaExpedientExport.getTipusClassificacio());
						target.setClassificacio(metaExpedientExport.getClassificacio());
						target.setNom(metaExpedientExport.getNom());
						target.setDescripcio(metaExpedientExport.getDescripcio());
						target.setSerieDocumental(metaExpedientExport.getSerieDocumental());
						if (metaExpedientExport.getOrganGestor()==null) {
							target.setProcedimentComu(true);
						} else {
							target.setProcedimentComu(false);
							target.setOrganGestor(ResourceReference.toResourceReference(
									metaExpedientExport.getOrganGestor().getId(),
									metaExpedientExport.getOrganGestor().getCodiINom()));
						}
						target.setExpressioNumero(metaExpedientExport.getExpressioNumero());

						List<MetaDocumentResource> metaDocumentsImportats = new ArrayList<MetaDocumentResource>();
						if (metaExpedientExport.getMetaDocuments()!=null) {
							for (MetaDocumentDto metaDocumentDto: metaExpedientExport.getMetaDocuments()) {
								MetaDocumentResource mdRes = new MetaDocumentResource();
								mdRes.setCodi(metaDocumentDto.getCodi());
								mdRes.setNom(metaDocumentDto.getNom());
								mdRes.setDescripcio(metaDocumentDto.getDescripcio());
								mdRes.setMultiplicitat(metaDocumentDto.getMultiplicitat());
								mdRes.setNtiOrigen(metaDocumentDto.getNtiOrigen());
								mdRes.setNtiTipoDocumental(metaDocumentDto.getNtiTipoDocumental());
								mdRes.setNtiEstadoElaboracion(metaDocumentDto.getNtiEstadoElaboracion());
								mdRes.setPortafirmesFluxTipus(metaDocumentDto.getPortafirmesFluxTipus());
								if (metaDocumentDto.getPortafirmesResponsables()!=null) {
									List<ResourceReference<UsuariResource, String>> portafirmesResponsables = new ArrayList<ResourceReference<UsuariResource, String>>();
									for (String pfResp: metaDocumentDto.getPortafirmesResponsables()) {
										UsuariResourceEntity usu = usuariResourceRepository.findById(pfResp).orElse(null);
										if (usu!=null) {
											portafirmesResponsables.add(ResourceReference.toResourceReference(
													usu.getCodi(), 
													usu.getNom()));
										} else {
											List<DadesUsuari> usuarisPlugin = pluginHelper.findAmbFiltre(pfResp);
											if (usuarisPlugin!=null && usuarisPlugin.size()==1) {
												portafirmesResponsables.add(ResourceReference.toResourceReference(
														usuarisPlugin.get(0).getCodi(), 
														usuarisPlugin.get(0).getNomSencer()));
											}
										}
									}
									mdRes.setPortafirmesResponsables(portafirmesResponsables);
								}
								metaDocumentsImportats.add(mdRes);
							}
						}
						target.setMetaDocumentsImportats(metaDocumentsImportats);
						
					} catch (Exception e) {
						excepcioLogHelper.addExcepcio("/metaExpedient/ImportarFitxerActionExecutor.onChange", e);
						throw new ActionExecutionException(getResourceClass(), null, fieldName, messageHelper.getMessage("message.common.action.error")+": "+e.getMessage());
					}
				}
			}
		}

		@Override
		public Serializable exec(String code, MetaExpedientResourceEntity entity, ImportarFitxerFormAction params) throws ActionExecutionException {
			try {
				return "{\"resultado\": \"OK\"}";
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/metaExpedient/ImportarFitxerActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), null, code, messageHelper.getMessage("message.common.action.error")+": "+e.getMessage());
			}
		}
    }
    
    private class ToggleReglaActionExecutor implements ActionExecutor<MetaExpedientResourceEntity, MetaExpedientResource.ToggleReglaRolsacFormAction, Serializable> {

		@Override
		public void onChange(Serializable id, ToggleReglaRolsacFormAction previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, ToggleReglaRolsacFormAction target) {
		}

		@Override
		public Serializable exec(String code, MetaExpedientResourceEntity entity, ToggleReglaRolsacFormAction params) throws ActionExecutionException {
			try {
				return metaExpedientHelper.canviarEstatReglaDistribucio(entity.getId(), params.isActiva());
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/metaExpedient/"+entity.getId()+"/ToggleReglaActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, messageHelper.getMessage("message.common.action.error")+": "+e.getMessage());
			}
		}
    }
    
    private class DesVincularGrupActionExecutor implements ActionExecutor<MetaExpedientResourceEntity, MetaExpedientResource.DesVincularGrupFormAction, Serializable> {

		@Override
		public void onChange(Serializable id, DesVincularGrupFormAction previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, DesVincularGrupFormAction target) {
		}

		@Override
		public Serializable exec(String code, MetaExpedientResourceEntity entity, DesVincularGrupFormAction params) throws ActionExecutionException {
			try {
				String entitatActualCodi = configHelper.getEntitatActualCodi();
				EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
				String organActualCodi	 = configHelper.getOrganActualCodi();
				OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitat.getId(), organActualCodi);
				grupHelper.desvincularAmbMetaExpedient(
						entitat.getId(),
						entity.getId(),
						params.getGrup().getId(),
						configHelper.getRolActual(),
						ogEntity!=null?ogEntity.getId():null);
				return "{\"resultado\": \"OK\"}";
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/metaExpedient/"+entity.getId()+"/DesVincularGrupActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, messageHelper.getMessage("message.common.action.error")+": "+e.getMessage());
			}
		}
    }
    
    private class VincularGrupActionExecutor implements ActionExecutor<MetaExpedientResourceEntity, MetaExpedientResource.VincularGrupFormAction, Serializable> {

		@Override
		public void onChange(Serializable id, VincularGrupFormAction previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, VincularGrupFormAction target) {
			if (fieldName != null) {
				if (VincularGrupFormAction.Fields.grup.equals(fieldName)) {
                    if (fieldValue != null) {
                        grupResourceRepository.findById(((ResourceReference<GrupResource, Long>)fieldValue).getId()).ifPresent((gre) -> {
                            if (gre.getOrganGestor() != null) {
                                target.setOrganGestor(ResourceReference.toResourceReference(
                                        gre.getOrganGestor().getId(),
                                        gre.getOrganGestor().getCodiINom()));
                            }
                        });
                    } else {
                        target.setOrganGestor(null);
                    }
                }
			}
		}

		@Override
		public Serializable exec(String code, MetaExpedientResourceEntity entity, VincularGrupFormAction params) throws ActionExecutionException {
			try {
				grupHelper.relacionarAmbMetaExpedient(entity.getId(), params.getGrup().getId(), params.isPerDefecte());
				if ("IPA_ORGAN_ADMIN".equals(configHelper.getRolActual())) {
					String entitatActualCodi = configHelper.getEntitatActualCodi();
					EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
					String organActualCodi	 = configHelper.getOrganActualCodi();
					OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitat.getId(), organActualCodi);
					metaExpedientHelper.canviarRevisioADisseny(entitat.getId(), entity.getId(), ogEntity!=null?ogEntity.getId():null);
				}
				return objectMappingHelper.newInstanceMap(entity, MetaExpedientResource.class);
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/metaExpedient/"+entity.getId()+"/VincularGrupActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, messageHelper.getMessage("message.common.action.error")+": "+e.getMessage());
			}				
		}    	
    }
    
    private class RevisioChangeActionExecutor implements ActionExecutor<MetaExpedientResourceEntity, MetaExpedientResource.RevisioChangeFormAction, Serializable> {

		@Override
		public void onChange(Serializable id, RevisioChangeFormAction previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, RevisioChangeFormAction target) {
		}

		@Override
		public Serializable exec(String code, MetaExpedientResourceEntity entity, RevisioChangeFormAction params) throws ActionExecutionException {
			try {
				
				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
				
				if (params.getRevisioComentari() != null && !params.getRevisioComentari().isEmpty()) {
					metaExpedientHelper.publicarComentariPerMetaExpedient(
							entitatEntity.getId(),
							entity.getId(),
							params.getRevisioComentari(),
							configHelper.getRolActual());
				}
				
				return metaExpedientHelper.canviarEstatRevisioASellecionat(
						entitatEntity.getId(),
						entity.getId(),
						params.getRevisioEstat());
				
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/metaExpedient/"+entity.getId()+"/RevisioChangeActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, messageHelper.getMessage("message.common.action.error")+": "+e.getMessage());
			}
		}    	
    }
    
    private class ExportJsonGenerator implements ReportGenerator<MetaExpedientResourceEntity, Serializable, Serializable> {

    	@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
    		DownloadableFile resultat = null;
    		Long procedimentId = data.get(0)!=null?(Long)data.get(0):null;
    		
    		try {
    			String entitatActualCodi = configHelper.getEntitatActualCodi();
    			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
    			String organActualCodi	 = configHelper.getOrganActualCodi();
    			OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitat.getId(), organActualCodi);
    			
    			String json = metaExpedientHelper.export(entitat.getId(), procedimentId, ogEntity!=null?ogEntity.getId():null);
    			
    			MetaExpedientResourceEntity mere = metaExpedientResourceRepository.findById(procedimentId).get();
    			
    			String fileNom = mere.getCodi().replaceAll("[^a-zA-Z0-9-]", "_");
    			if (fileNom.length() > 60) {
    				fileNom = fileNom.substring(0, 60);
    			}
    			
            	resultat = new DownloadableFile(
            			fileNom,
            			"application/json; charset=UTF-8",
            			json.getBytes(StandardCharsets.UTF_8));
    			
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/metaExpedient/"+procedimentId+"/ExportJsonGenerator", e);
				throw new ReportGenerationException(getResourceClass(), procedimentId, code, "expedient.export.eni.reject");
			}

            return resultat;
    	}
    	
		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {
		}

		@Override
		public List<Serializable> generateData(String code, MetaExpedientResourceEntity entity, Serializable params) throws ReportGenerationException {
			return List.of(entity.getId());
		}
    }
}