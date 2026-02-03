package es.caib.ripea.service.resourceservice;

import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
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
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.plugin.usuari.DadesUsuari;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
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
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.ReportFileType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.DominiDto;
import es.caib.ripea.service.intf.dto.ExpedientEstatDto;
import es.caib.ripea.service.intf.dto.GrupDto;
import es.caib.ripea.service.intf.dto.ItemValidacioTascaEnum;
import es.caib.ripea.service.intf.dto.MetaDadaDto;
import es.caib.ripea.service.intf.dto.MetaDocumentDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaExpedientCarpetaMinDto;
import es.caib.ripea.service.intf.dto.MetaExpedientDto;
import es.caib.ripea.service.intf.dto.MetaExpedientExportDto;
import es.caib.ripea.service.intf.dto.MetaExpedientFiltreDto;
import es.caib.ripea.service.intf.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.service.intf.dto.MetaExpedientTascaDto;
import es.caib.ripea.service.intf.dto.MetaExpedientTascaValidacioDto;
import es.caib.ripea.service.intf.dto.MetaNodeTipusEnum;
import es.caib.ripea.service.intf.dto.OrganGestorDto;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.dto.PinbalServeiDto;
import es.caib.ripea.service.intf.dto.ProcedimentDto;
import es.caib.ripea.service.intf.dto.ProgresActualitzacioDto;
import es.caib.ripea.service.intf.dto.TipusClassificacioEnumDto;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.GrupResource;
import es.caib.ripea.service.intf.model.MetaDadaResource;
import es.caib.ripea.service.intf.model.MetaDocumentFluxPortafibResource;
import es.caib.ripea.service.intf.model.MetaDocumentResource;
import es.caib.ripea.service.intf.model.MetaExpedientCarpetaResource;
import es.caib.ripea.service.intf.model.MetaExpedientEstatResource;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
import es.caib.ripea.service.intf.model.MetaExpedientResource.DesVincularGrupFormAction;
import es.caib.ripea.service.intf.model.MetaExpedientResource.ImportarFitxerFormAction;
import es.caib.ripea.service.intf.model.MetaExpedientResource.ImportarRolsacFormAction;
import es.caib.ripea.service.intf.model.MetaExpedientResource.RevisioChangeFormAction;
import es.caib.ripea.service.intf.model.MetaExpedientResource.ToggleReglaRolsacFormAction;
import es.caib.ripea.service.intf.model.MetaExpedientResource.VincularGrupFormAction;
import es.caib.ripea.service.intf.model.MetaExpedientTascaResource;
import es.caib.ripea.service.intf.model.MetaExpedientTascaValidacioResource;
import es.caib.ripea.service.intf.model.MetaNodeResource;
import es.caib.ripea.service.intf.model.NodeResource.MassiveAction;
import es.caib.ripea.service.intf.model.OrganGestorResource;
import es.caib.ripea.service.intf.model.UsuariResource;
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
	private final GrupResourceRepository grupResourceRepository;
	private final MetaExpedientRepository metaExpedientRepository;
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
    	register(MetaExpedientResource.ACTION_CREAR_REGLA_CODE,		new CrearReglaActionExecutor());
    	register(MetaExpedientResource.ACTION_UPDATE_ROLSAC_CODE,	new ActualitzarProcedimentsRolsacActionExecutor());
    	
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
		boolean isDissenyador = "IPA_DISSENY".equals(rolActual);
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
            procsPermesosIds = metaExpedientEntityToListLong(metaExpPermesos);
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
            procsPermesosIds = metaExpedientEntityToListLong(metaExpPermesos);
        } else if (mapaNamedQueries.size()>0 && mapaNamedQueries.containsKey("CONSULTA_REVISIO_ESTAT")) {
        	//Volem replicar metaExpedientServiceImpl.findByEntitat
        	//Nom comprova cap permis, ja que es un manteniment per admins
        	//Nomes filtra per entitat o els filtres del formulari de cerca.
        	return filtreBase.generate();
        } else { 
        	/**
        	 * LLISTAT DE PROCEDIMENTS
        	 */
        	
        	if (isAdminOrgan || isDissenyador) {
        		OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(
        				entitat.getId(),
        				configHelper.getOrganActualCodi());
        		boolean hasPermisAdminComu = permisosHelper.isGrantedAll(
        				ogEntity.getId(),
        				OrganGestorEntity.class,
        				new Permission[] { ExtendedPermission.ADMINISTRATION, ExtendedPermission.ADM_COMU },
        				SecurityContextHolder.getContext().getAuthentication());
        		procsPermesosIds = metaExpedientHelper.findMetaExpedientIdsFiltratsAmbPermisosOrganGestor(
        				entitat.getId(),
        				ogEntity.getId(),
        				hasPermisAdminComu);
        	} else if (isAdmin) {
        		MetaExpedientFiltreDto filtre = new MetaExpedientFiltreDto();
        		metaExpPermesos = metaExpedientHelper.findByEntitat(entitat, filtre, Utils.sensePaginacio(), null).getContent();
        		procsPermesosIds = metaExpedientEntityToListLong(metaExpPermesos);
        	} else {
	            metaExpPermesos = metaExpedientHelper.findAmbPermis(
	            		entitat.getId(),
	            		ExtendedPermission.READ,
	            		false, //nomesActius
	            		null, //filtreNomOrCodiSia
	            		isAdmin,
	            		isAdminOrgan,
	            		null, //organId
	            		false); //comú
	            procsPermesosIds = metaExpedientEntityToListLong(metaExpPermesos);
        	}
        }
        
		if (procsPermesosIds==null || procsPermesosIds.size()==0) {
			return FilterBuilder.equal("id", 0).generate();
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

	private List<Long> metaExpedientEntityToListLong(List<MetaExpedientEntity> metaExpPermesos) {
		List<Long> resultat = null;
		if (metaExpPermesos!=null && metaExpPermesos.size()>0) {
			resultat = new ArrayList<Long>();
			for (MetaExpedientEntity mee: metaExpPermesos) {
				resultat.add(mee.getId());
			}
		}
		return resultat;
	}
	
    @Override
    protected void afterConversion(MetaExpedientResourceEntity entity, MetaExpedientResource resource) {
        resource.setNumComentaris(entity.getComentaris().size());
        resource.setProcedimentComu(entity.getOrganGestor()==null);
        resource.setTipus(MetaNodeTipusEnum.EXPEDIENT);
    }
    
	@Override
	public MetaExpedientResource create(MetaExpedientResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		
		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
		OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitatEntity.getId(), configHelper.getOrganActualCodi());
		
		MetaExpedientEntity metaExpedientEntity = metaExpedientHelper.create(
				entitatEntity.getId(),
				metaExpedientresourceToDto(resource),
				configHelper.getRolActual(),
				ogEntity!=null?ogEntity.getId():null);
		
		resource.setId(metaExpedientEntity.getId());
		resource.setCrearReglaDistribucioError(metaExpedientEntity.getCrearReglaDistribucioError());
		resource.setCrearReglaDistribucioEstat(metaExpedientEntity.getCrearReglaDistribucioEstat());
		
		return resource;
	}
	
	@Override
	public MetaExpedientResource update(
			Long id,
			MetaExpedientResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
		
		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
		OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitatEntity.getId(), configHelper.getOrganActualCodi());
		MetaExpedientResourceEntity mere = metaExpedientResourceRepository.findById(id).get();
		
		if (mere.isActiu()!=resource.isActiu()) {
			metaExpedientHelper.updateActiu(
					entitatEntity.getId(),
					id,
					resource.isActiu(),
					configHelper.getRolActual(),
					ogEntity!=null?ogEntity.getId():null);
		} else {
			metaExpedientHelper.update(
					entitatEntity.getId(),
					metaExpedientresourceToDto(resource),
					configHelper.getRolActual(),
					resource.getRevisioEstat(),
					ogEntity!=null?ogEntity.getId():null);
		}
		
		return resource;
	}
	
	private MetaExpedientDto metaExpedientresourceToDto(MetaExpedientResource resource) {
		MetaExpedientDto metaExpedientDto = objectMappingHelper.newInstanceMap(
				resource
				, MetaExpedientDto.class
				, "serialVersionUID", "createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate");
		if (resource.getOrganGestor()!=null) {
			OrganGestorDto og = new OrganGestorDto();
			og.setId(resource.getOrganGestor().getId());
			og.setNom(resource.getOrganGestor().getDescription());
			metaExpedientDto.setOrganGestor(og);
		}
		return metaExpedientDto;
	}
    
    @Override
	public void delete(Long id, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
    	
		String entitatActualCodi = configHelper.getEntitatActualCodi();
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true, false);
    	
		String organActualCodi	 = configHelper.getOrganActualCodi();
		OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitat.getId(), organActualCodi);
		
		metaExpedientHelper.delete(entitat.getId(), id, ogEntity!=null?ogEntity.getId():null);
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
//			ReglaDistribucioDto regla = new ReglaDistribucioDto();
//			regla.setActiva(false);
//			regla.setNom("Regla Fake no Real");
//			regla.setData("05/01/2026");
//			resource.setRegla(regla);
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
			List<PermisDto> permisosGrup = null;
			if (entity.isComu()) {
				permisosGrup = permisosHelper.findPermisos(entity.getId(), MetaExpedientOrganGestorEntity.class);
			} else {
				permisosGrup = permisosHelper.findPermisos(entity.getId(), MetaNodeEntity.class);
			}
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
    
    private MetaDadaResource toMetaDadaResource(MetaDadaDto metaDadaDto) {
		
    	MetaDadaResource mdadaRes = new MetaDadaResource();
		
    	mdadaRes.setImportacioId(metaDadaDto.getId());
		mdadaRes.setCodi(metaDadaDto.getCodi());
		mdadaRes.setNom(metaDadaDto.getNom());
		mdadaRes.setTipus(metaDadaDto.getTipus());
		mdadaRes.setMultiplicitat(metaDadaDto.getMultiplicitat());
		
		mdadaRes.setValorString(metaDadaDto.getValorString());
		mdadaRes.setValorSencer(metaDadaDto.getValorSencer());
		mdadaRes.setValorImport(metaDadaDto.getValorImport());
		mdadaRes.setValorFlotant(metaDadaDto.getValorFlotant());
		if (metaDadaDto.getValorData()!=null) {
			mdadaRes.setValorData(Utils.dateJavaToLocalDateTime(metaDadaDto.getValorData()));
		}
		mdadaRes.setValorBoolea(metaDadaDto.getValorBoolea());
		
		mdadaRes.setEnviable(metaDadaDto.isEnviable());
		mdadaRes.setMetadadaArxiu(metaDadaDto.getMetadadaArxiu());
		mdadaRes.setDescripcio(metaDadaDto.getDescripcio());
		mdadaRes.setActiva(metaDadaDto.isActiva());
		mdadaRes.setNoAplica(metaDadaDto.isNoAplica());
		
		if (metaDadaDto.getDomini()!=null) {
			mdadaRes.setDomini(ResourceReference.toResourceReference(
					metaDadaDto.getDomini().getId(),
					metaDadaDto.getDomini().getCodi()));
		}
		return mdadaRes;
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
						
						/**
						 * DADES GENERALS
						 */
						target.setCodi(metaExpedientExport.getCodi());
						target.setTipusProcedimentServei(metaExpedientExport.getTipusProcedimentServei());
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

						/**
						 * META-DOCUMENTS
						 */
						List<MetaDocumentResource> metaDocumentsImportats = new ArrayList<MetaDocumentResource>();
						if (metaExpedientExport.getMetaDocuments()!=null) {
							for (MetaDocumentDto metaDocumentDto: metaExpedientExport.getMetaDocuments()) {
								MetaDocumentResource mdRes = new MetaDocumentResource();
								//Dades generals
								mdRes.setImportacioId(metaDocumentDto.getId());
								mdRes.setCodi(metaDocumentDto.getCodi());
								mdRes.setNom(metaDocumentDto.getNom());
								mdRes.setDescripcio(metaDocumentDto.getDescripcio());
								mdRes.setMultiplicitat(metaDocumentDto.getMultiplicitat());
								mdRes.setActiu(metaDocumentDto.isActiu());
								//Dades NTI
								mdRes.setNtiOrigen(metaDocumentDto.getNtiOrigen());
								mdRes.setNtiTipoDocumental(metaDocumentDto.getNtiTipoDocumental());
								mdRes.setNtiEstadoElaboracion(metaDocumentDto.getNtiEstadoElaboracion());
								//Dades firma
								mdRes.setFirmaPortafirmesActiva(metaDocumentDto.isFirmaPortafirmesActiva());
								mdRes.setPortafirmesFluxTipus(metaDocumentDto.getPortafirmesFluxTipus());
								if (MetaDocumentFirmaFluxTipusEnumDto.SIMPLE.equals(mdRes.getPortafirmesFluxTipus())) {
									mdRes.setPortafirmesSequenciaTipus(metaDocumentDto.getPortafirmesSequenciaTipus());
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
								}
								if (MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB.equals(mdRes.getPortafirmesFluxTipus())) {
									if (metaDocumentDto.getPortafirmesFluxosId()!=null) {
										List<ResourceReference<MetaDocumentFluxPortafibResource, Long>> fluxosFirma = new ArrayList<ResourceReference<MetaDocumentFluxPortafibResource, Long>>();
										for (String pfFluxId: metaDocumentDto.getPortafirmesFluxosId()) {
											fluxosFirma.add(ResourceReference.toResourceReference(
													0l, 
													pfFluxId));
										}
										mdRes.setFluxosFirma(fluxosFirma);
									}
								}
								
								//Firma Navegador
								mdRes.setFirmaPassarelaActiva(metaDocumentDto.isFirmaPassarelaActiva());
								//ViaFirma
								mdRes.setFirmaBiometricaActiva(metaDocumentDto.isFirmaBiometricaActiva());
								mdRes.setBiometricaLectura(metaDocumentDto.isBiometricaLectura());
								//PINBAL
								mdRes.setPinbalActiu(metaDocumentDto.isPinbalActiu());
								if (metaDocumentDto.isPinbalActiu() && metaDocumentDto.getPinbalServei()!=null) {
									mdRes.setPinbalServei(ResourceReference.toResourceReference(
											metaDocumentDto.getPinbalServei().getId(), 
											metaDocumentDto.getPinbalServei().getCodi()));
								}
								mdRes.setPinbalFinalitat(metaDocumentDto.getPinbalFinalitat());
								mdRes.setPinbalUtilitzarCifOrgan(metaDocumentDto.isPinbalUtilitzarCifOrgan());
								
								//Meta-dades del meta-document
								List<MetaDadaResource> metaDadesDocumentResource = new ArrayList<MetaDadaResource>();
								if (metaDocumentDto.getMetaDades()!=null) {
									for (MetaDadaDto metaDadaDto: metaDocumentDto.getMetaDades()) {
										metaDadesDocumentResource.add(toMetaDadaResource(metaDadaDto));
									}
								}
								mdRes.setMetaDadesImportacio(metaDadesDocumentResource);
								
								metaDocumentsImportats.add(mdRes);
							}
						}
						metaDocumentsImportats.sort(Comparator.comparing(MetaDocumentResource::getNom));
						target.setMetaDocumentsImportats(metaDocumentsImportats);
						
						/**
						 * META-DADES
						 */
						List<MetaDadaResource> metaDadesImportats = new ArrayList<MetaDadaResource>();
						if (metaExpedientExport.getMetaDades()!=null) {
							for (MetaDadaDto metaDadaDto: metaExpedientExport.getMetaDades()) {
								metaDadesImportats.add(toMetaDadaResource(metaDadaDto));
							}
						}
						metaDadesImportats.sort(Comparator.comparing(MetaDadaResource::getNom));
						target.setMetaDadesImportats(metaDadesImportats);
						
						/**
						 * ESTATS
						 */
						List<MetaExpedientEstatResource> estatsImportats = new ArrayList<MetaExpedientEstatResource>();
						if (metaExpedientExport.getEstats()!=null) {
							for (ExpedientEstatDto estatDto: metaExpedientExport.getEstats()) {
								MetaExpedientEstatResource estatResource = new MetaExpedientEstatResource();
								
								estatResource.setImportacioId(estatDto.getId());
								estatResource.setCodi(estatDto.getCodi());
								estatResource.setNom(estatDto.getNom());
								estatResource.setOrdre(estatDto.getOrdre());
								estatResource.setColor(estatDto.getColor());
								estatResource.setInicial(estatDto.isInicial());
								
								if (estatDto.getResponsableCodi()!=null) {
									UsuariResourceEntity usu = usuariResourceRepository.findById(estatDto.getResponsableCodi()).orElse(null);
									if (usu!=null) {
										estatResource.setResponsable(ResourceReference.toResourceReference(
												usu.getCodi(), 
												usu.getNom()));
									}
								}
								
								estatsImportats.add(estatResource);
							}
						}
						estatsImportats.sort(Comparator.comparing(MetaExpedientEstatResource::getNom));
						target.setEstatsImportats(estatsImportats);
						
						/**
						 * TASQUES
						 */
						List<MetaExpedientTascaResource> tasquesImportats = new ArrayList<MetaExpedientTascaResource>();
						if (metaExpedientExport.getTasques()!=null) {
							for (MetaExpedientTascaDto tascaDto: metaExpedientExport.getTasques()) {
								MetaExpedientTascaResource tascaResource = new MetaExpedientTascaResource();
								
								tascaResource.setCodi(tascaDto.getCodi());
								tascaResource.setNom(tascaDto.getNom());
								if (tascaDto.getResponsable()!=null) {
									UsuariResourceEntity usu = usuariResourceRepository.findById(tascaDto.getResponsable()).orElse(null);
									if (usu!=null) {
										tascaResource.setResponsable(ResourceReference.toResourceReference(
												usu.getCodi(), 
												usu.getNom()));
									}
								}
								tascaResource.setDuracio(tascaDto.getDuracio());
								tascaResource.setDescripcio(tascaDto.getDescripcio());
								tascaResource.setPrioritat(tascaDto.getPrioritat());
								if (tascaDto.getEstatIdCrearTasca()!=null) {
									tascaResource.setEstatCrearTasca(ResourceReference.toResourceReference(
											tascaDto.getEstatIdCrearTasca(), 
											"Tasca inicial id "+tascaDto.getEstatIdCrearTasca()));
								}
								if (tascaDto.getEstatIdFinalitzarTasca()!=null) {
									tascaResource.setEstatFinalitzarTasca(ResourceReference.toResourceReference(
											tascaDto.getEstatIdFinalitzarTasca(), 
											"Tasca final id "+tascaDto.getEstatIdFinalitzarTasca()));
								}
								
								if (tascaDto.getValidacions()!=null) {
									List<MetaExpedientTascaValidacioResource> validacionsImportacio = new ArrayList<MetaExpedientTascaValidacioResource>();
									for (MetaExpedientTascaValidacioDto tascaValidacioDto: tascaDto.getValidacions()) {
										MetaExpedientTascaValidacioResource validacioResource = new MetaExpedientTascaValidacioResource();
										
										validacioResource.setItemValidacio(tascaValidacioDto.getItemValidacio());
										validacioResource.setTipusValidacio(tascaValidacioDto.getTipusValidacio());
										validacioResource.setItemId(tascaValidacioDto.getItemId());
										validacioResource.setActiva(tascaValidacioDto.isActiva());
										
										validacionsImportacio.add(validacioResource);
									}
									tascaResource.setValidacionsImportacio(validacionsImportacio);
								}
								
								tasquesImportats.add(tascaResource);
							}
						}
						tasquesImportats.sort(Comparator.comparing(MetaExpedientTascaResource::getNom));
						target.setTasquesImportats(tasquesImportats);
						
						/**
						 * GRUPS
						 */
						List<GrupResource> grupsImportats = new ArrayList<GrupResource>();
						if (metaExpedientExport.getGrups()!=null) {
							for (GrupDto grupDto: metaExpedientExport.getGrups()) {
								GrupResource grupResource = new GrupResource();
								
								grupResource.setCodi(grupDto.getCodi());
								grupResource.setDescripcio(grupDto.getDescripcio());
								
								grupsImportats.add(grupResource);
							}
						}
						grupsImportats.sort(Comparator.comparing(GrupResource::getDescripcio));
						target.setGrupsImportats(grupsImportats);
						
						/**
						 * CARPETES PER DEFECTE
						 */
						List<MetaExpedientCarpetaResource> carpetesImportats = new ArrayList<MetaExpedientCarpetaResource>();
						if (metaExpedientExport.getCarpetes()!=null) {
							for (MetaExpedientCarpetaMinDto carpetaDto: metaExpedientExport.getCarpetes()) {
								MetaExpedientCarpetaResource carpetaResource = new MetaExpedientCarpetaResource();
								carpetaResource.setId(carpetaDto.getId());
								carpetaResource.setNom(carpetaDto.getNom());
								if (carpetaDto.getPare()!=null) {
									carpetaResource.setPare(ResourceReference.toResourceReference(
											carpetaDto.getPare().getId(),
											carpetaDto.getPare().getNom()));
								}
								carpetesImportats.add(carpetaResource);
							}
						}
						carpetesImportats.sort(Comparator.comparing(MetaExpedientCarpetaResource::getNom));
						target.setCarpetesImportats(carpetesImportats);
						
					} catch (Exception e) {
						excepcioLogHelper.addExcepcio("/metaExpedient/ImportarFitxerActionExecutor.onChange", e);
						throw new ActionExecutionException(getResourceClass(), null, fieldName, messageHelper.getMessage("message.common.action.error")+": "+e.getMessage());
					}
				} else if (MetaExpedientResource.ImportarFitxerFormAction.Fields.procediment.equals(fieldName)) {
					if (fieldValue!=null) {
						ResourceReference<OrganGestorResource, Long> resourceReference = (ResourceReference<OrganGestorResource, Long>) fieldValue;
						MetaExpedientEntity prc = metaExpedientRepository.findById(resourceReference.getId()).get();
						target.setCodi(prc.getCodi());
						target.setTipusClassificacio(prc.getTipusClassificacio());
						target.setClassificacio(prc.getClassificacio());
						if (prc.getOrganGestor()!=null) {
							target.setOrganGestor(ResourceReference.toResourceReference(prc.getOrganGestor().getId(), prc.getOrganGestor().getNom()));
						}
					}
				}
			}
		}

		@Override
		public Serializable exec(String code, MetaExpedientResourceEntity entity, ImportarFitxerFormAction params) throws ActionExecutionException {
			try {
				
				String entitatActualCodi = configHelper.getEntitatActualCodi();
				EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
				String organActualCodi	 = configHelper.getOrganActualCodi();
				OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitat.getId(), organActualCodi);
				
				if (params.getProcediment()==null) {
					
					//Comprovar que no existeix un altre expedient amb el mateix codi SIA
					List<MetaExpedientEntity> altresSIA = metaExpedientRepository.findByEntitatAndClassificacioOrderByNomAsc(
							entitat,
							params.getClassificacio());

					if (altresSIA!=null && altresSIA.size()>0) {
						throw new ActionExecutionException(
								getResourceClass(),
								null,
								code,
								messageHelper.getMessage("metaexpedient.import.form.validation.codisia.repetit"));
					}

					metaExpedientHelper.createFromImport(
							entitat.getId(),
							resourceToExportDto(params),
							configHelper.getRolActual(),
							ogEntity!=null?ogEntity.getId():null);
				} else {
					metaExpedientHelper.updateFromImport(
							entitat.getId(),
							resourceToExportDto(params),
							configHelper.getRolActual(),
							ogEntity!=null?ogEntity.getId():null);
				}
				
				return "{\"resultado\": \"OK\"}";
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/metaExpedient/ImportarFitxerActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), null, code, messageHelper.getMessage("message.common.action.error")+": "+e.getMessage());
			}
		}
    }
    
    private MetaDadaDto toMetaDadaDto(MetaDadaResource metaDadaResource) {
		MetaDadaDto mdadaDto = new MetaDadaDto();
		
		mdadaDto.setCodi(metaDadaResource.getCodi());
		mdadaDto.setNom(metaDadaResource.getNom());
		mdadaDto.setTipus(metaDadaResource.getTipus());
		mdadaDto.setMultiplicitat(metaDadaResource.getMultiplicitat());
		
		mdadaDto.setValorString(metaDadaResource.getValorString());
		mdadaDto.setValorSencer(metaDadaResource.getValorSencer());
		mdadaDto.setValorImport(metaDadaResource.getValorImport());
		mdadaDto.setValorFlotant(metaDadaResource.getValorFlotant());
		if (metaDadaResource.getValorData()!=null) {
			mdadaDto.setValorData(Utils.localDateTimeToDateJava(metaDadaResource.getValorData()));
		}
		mdadaDto.setValorBoolea(metaDadaResource.getValorBoolea());
		
		mdadaDto.setEnviable(metaDadaResource.isEnviable());
		mdadaDto.setMetadadaArxiu(metaDadaResource.getMetadadaArxiu());
		mdadaDto.setDescripcio(metaDadaResource.getDescripcio());
		mdadaDto.setActiva(metaDadaResource.isActiva());
		mdadaDto.setNoAplica(metaDadaResource.isNoAplica());
		
		if (metaDadaResource.getDomini()!=null) {
			DominiDto dominiDto = new DominiDto();
			dominiDto.setId(metaDadaResource.getDomini().getId());
			dominiDto.setCodi(metaDadaResource.getDomini().getDescription());
			mdadaDto.setDomini(dominiDto);
			//El valor de una meta-dada de tipus DOMINI, es el codi del domini
			mdadaDto.setValorString(metaDadaResource.getDomini().getDescription());
		}
		
		return mdadaDto;
    }

    private MetaExpedientExportDto resourceToExportDto(ImportarFitxerFormAction metaExpedientExport) {
    	
    	MetaExpedientExportDto target = new MetaExpedientExportDto();
    	
    	target.setId(metaExpedientExport.getProcediment()!=null?metaExpedientExport.getProcediment().getId():null);
    	target.setCodi(metaExpedientExport.getCodi());
    	
    	target.setTipusProcedimentServei(metaExpedientExport.getTipusProcedimentServei());
		target.setTipusClassificacio(metaExpedientExport.getTipusClassificacio());
		target.setClassificacio(metaExpedientExport.getClassificacio());
		target.setNom(metaExpedientExport.getNom());
		target.setDescripcio(metaExpedientExport.getDescripcio());
		target.setSerieDocumental(metaExpedientExport.getSerieDocumental());
		
		target.setPermetMetadocsGenerals(metaExpedientExport.isPermetMetadocsGenerals());
		target.setGestioAmbGrupsActiva(metaExpedientExport.isGestioAmbGrupsActiva());
		target.setInteressatObligatori(metaExpedientExport.isInteressatObligatori());
		target.setPermisDirecte(metaExpedientExport.isPermisDirecte());
    	
		if (!metaExpedientExport.isProcedimentComu()) {
			OrganGestorDto organGestor = new OrganGestorDto();
			organGestor.setId(metaExpedientExport.getOrganGestor().getId());
			organGestor.setNom(metaExpedientExport.getOrganGestor().getDescription());
			target.setOrganGestor(organGestor);
		} else {
			target.setOrganGestor(null);
		}
		target.setExpressioNumero(metaExpedientExport.getExpressioNumero());
		
		/**
		 * META-DOCUMENTS
		 */
		List<MetaDocumentDto> metaDocuments = new ArrayList<MetaDocumentDto>();
		if (metaExpedientExport.getMetaDocumentsImportats()!=null) {
			
			for (MetaDocumentResource metaDocumentResource: metaExpedientExport.getMetaDocumentsImportats()) {
				
				if (metaDocumentResource.isImportar()) {
					
					MetaDocumentDto metaDocumentDto = new MetaDocumentDto();
					//Dades generals
					metaDocumentDto.setCodi(metaDocumentResource.getCodi());
					metaDocumentDto.setNom(metaDocumentResource.getNom());
					metaDocumentDto.setDescripcio(metaDocumentResource.getDescripcio());
					metaDocumentDto.setMultiplicitat(metaDocumentResource.getMultiplicitat());
					metaDocumentDto.setActiu(metaDocumentResource.isActiu());
					//Dades NTI
					metaDocumentDto.setNtiOrigen(metaDocumentResource.getNtiOrigen());
					metaDocumentDto.setNtiTipoDocumental(metaDocumentResource.getNtiTipoDocumental());
					metaDocumentDto.setNtiEstadoElaboracion(metaDocumentResource.getNtiEstadoElaboracion());
					//Dades firma
					metaDocumentDto.setFirmaPortafirmesActiva(metaDocumentResource.isFirmaPortafirmesActiva());
					metaDocumentDto.setPortafirmesFluxTipus(metaDocumentResource.getPortafirmesFluxTipus());
					//Usuaris resposables de firma SIMPLE
					if (MetaDocumentFirmaFluxTipusEnumDto.SIMPLE.equals(metaDocumentDto.getPortafirmesFluxTipus())) {
						metaDocumentDto.setPortafirmesSequenciaTipus(metaDocumentResource.getPortafirmesSequenciaTipus());
						if (metaDocumentResource.getPortafirmesResponsables()!=null && metaDocumentResource.getPortafirmesResponsables().size()>0) {
							String[] portafirmesResponsables = metaDocumentResource.getPortafirmesResponsables().stream()
							        .map(ResourceReference::getId)
							        .toArray(String[]::new);
							metaDocumentDto.setPortafirmesResponsables(portafirmesResponsables);
						}
					}
					if (MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB.equals(metaDocumentDto.getPortafirmesFluxTipus())) {
						if (metaDocumentResource.getFluxosFirma()!=null && metaDocumentResource.getFluxosFirma().size()>0) {
							String[] fluxosFirma = metaDocumentResource.getFluxosFirma().stream()
							        .map(ResourceReference::getDescription)
							        .toArray(String[]::new);
							metaDocumentDto.setPortafirmesFluxosId(fluxosFirma);
						}
					}
					//Firma Navegador
					metaDocumentDto.setFirmaPassarelaActiva(metaDocumentResource.isFirmaPassarelaActiva());
					//ViaFirma
					metaDocumentDto.setFirmaBiometricaActiva(metaDocumentResource.isFirmaBiometricaActiva());
					metaDocumentDto.setBiometricaLectura(metaDocumentResource.isBiometricaLectura());
					//PINBAL
					metaDocumentDto.setPinbalActiu(metaDocumentResource.isPinbalActiu());
					if (metaDocumentResource.isPinbalActiu() && metaDocumentResource.getPinbalServei()!=null) {
						PinbalServeiDto pinbalServeiDto = new PinbalServeiDto();
						pinbalServeiDto.setId(metaDocumentResource.getPinbalServei().getId());
						pinbalServeiDto.setCodi(metaDocumentResource.getPinbalServei().getDescription());
						metaDocumentDto.setPinbalServei(pinbalServeiDto);
					}
					metaDocumentDto.setPinbalFinalitat(metaDocumentResource.getPinbalFinalitat());
					metaDocumentDto.setPinbalUtilitzarCifOrgan(metaDocumentResource.isPinbalUtilitzarCifOrgan());
					
					//Meta-dades del meta-document
					List<MetaDadaDto> metaDadesDocument = new ArrayList<MetaDadaDto>();
					if (metaDocumentResource.getMetaDadesImportacio()!=null) {
						for (MetaDadaResource metaDadaDocumentResource: metaDocumentResource.getMetaDadesImportacio()) {
							//La metadada del document, no ha de haver estat descartada de la importació
							//NO es necessari, les metadades dels documents van a part de les del procediment
//							if (!isMetadadaDescartada(metaExpedientExport.getMetaDadesImportats(), metaDadaDocumentResource.getId())) {
								metaDadesDocument.add(toMetaDadaDto(metaDadaDocumentResource));
//							}
						}
					}
					metaDocumentDto.setMetaDades(metaDadesDocument);
					
					metaDocuments.add(metaDocumentDto);
				} //Fi IF importar...
			} //Fi bucle documents
		} //Fi IF documents...
		
		target.setMetaDocuments(metaDocuments);
		
		/**
		 * META-DADES
		 */
		List<MetaDadaDto> metaDadesImportats = new ArrayList<MetaDadaDto>();
		if (metaExpedientExport.getMetaDadesImportats()!=null) {
			
			for (MetaDadaResource metaDadaResource: metaExpedientExport.getMetaDadesImportats()) {
			
				if (metaDadaResource.isImportar()) {
					metaDadesImportats.add(toMetaDadaDto(metaDadaResource));
				}
			}
		}
		target.setMetaDades(metaDadesImportats);
		
		/**
		 * ESTATS
		 */
		Set<ExpedientEstatDto> estatsImportats = new HashSet<ExpedientEstatDto>();
		List<Long> estatsExclosos = new ArrayList<Long>();
		if (metaExpedientExport.getEstatsImportats()!=null) {
			
			for (MetaExpedientEstatResource estatResource: metaExpedientExport.getEstatsImportats()) {
				
				if (estatResource.isImportar()) {
				
					ExpedientEstatDto estatDto = new ExpedientEstatDto();
					
					estatDto.setCodi(estatResource.getCodi());
					estatDto.setNom(estatResource.getNom());
					estatDto.setOrdre(estatResource.getOrdre());
					estatDto.setColor(estatResource.getColor());
					estatDto.setInicial(estatResource.isInicial());
					
					if (estatResource.getResponsable()!=null) {
						estatDto.setResponsableCodi(estatResource.getResponsable().getId());
					}
					
					estatsImportats.add(estatDto);
				} else {
					estatsExclosos.add(estatResource.getImportacioId());
				}
			}
		}
		target.setEstats(estatsImportats);
		
		/**
		 * TASQUES
		 */
		Set<MetaExpedientTascaDto> tasquesImportats = new HashSet<MetaExpedientTascaDto>();
		if (metaExpedientExport.getTasquesImportats()!=null) {
			
			for (MetaExpedientTascaResource tascaResource: metaExpedientExport.getTasquesImportats()) {
				
				if (tascaResource.isImportar()) {
				
					MetaExpedientTascaDto tascaDto = new MetaExpedientTascaDto();
					
					tascaDto.setCodi(tascaResource.getCodi());
					tascaDto.setNom(tascaResource.getNom());
					if (tascaResource.getResponsable()!=null) {
						tascaDto.setResponsable(tascaResource.getResponsable().getId());
					}
					tascaDto.setDuracio(tascaResource.getDuracio());
					tascaDto.setDescripcio(tascaResource.getDescripcio());
					tascaDto.setPrioritat(tascaResource.getPrioritat());
					
					//Al assginar estats a tasques, revisar que el estat no ha estat exclos de la importació
					if (tascaResource.getEstatCrearTasca()!=null) {
						if (!estatsExclosos.contains(tascaResource.getEstatCrearTasca().getId())) {
								tascaDto.setEstatIdCrearTasca(tascaResource.getEstatCrearTasca().getId());
								tascaDto.setEstatNomCrearTasca(tascaResource.getEstatCrearTasca().getDescription());
						}
					}
					if (tascaResource.getEstatFinalitzarTasca()!=null) {
						if (!estatsExclosos.contains(tascaResource.getEstatFinalitzarTasca().getId())) {
							tascaDto.setEstatIdFinalitzarTasca(tascaResource.getEstatFinalitzarTasca().getId());
							tascaDto.setEstatNomFinalitzarTasca(tascaResource.getEstatFinalitzarTasca().getDescription());
						}
					}
					
					if (tascaResource.getValidacionsImportacio()!=null) {
						List<MetaExpedientTascaValidacioDto> validacionsImportacio = new ArrayList<MetaExpedientTascaValidacioDto>();
						for (MetaExpedientTascaValidacioResource tascaValidacioResource: tascaResource.getValidacionsImportacio()) {
							
							//El item de la validació (document o metadada) no ha de haver estat exclòs de la importació
							if (!elementValidacioExclos(
									tascaValidacioResource.getItemValidacio(),
									tascaValidacioResource.getItemId(),
									metaExpedientExport.getMetaDocumentsImportats(),
									metaExpedientExport.getMetaDadesImportats())) {

								MetaExpedientTascaValidacioDto tascaValidacioDto = new MetaExpedientTascaValidacioDto();
								
								tascaValidacioDto.setItemValidacio(tascaValidacioResource.getItemValidacio());
								tascaValidacioDto.setTipusValidacio(tascaValidacioResource.getTipusValidacio());
								tascaValidacioDto.setItemId(tascaValidacioResource.getItemId());
								tascaValidacioDto.setActiva(tascaValidacioResource.isActiva());
								
								validacionsImportacio.add(tascaValidacioDto);
							}
						}
						tascaDto.setValidacions(validacionsImportacio);
					}
					
					tasquesImportats.add(tascaDto);
				}
			}
		}
		target.setTasques(tasquesImportats);
		
		/**
		 * GRUPS
		 */
		List<GrupDto> grupsImportats = new ArrayList<GrupDto>();
		if (metaExpedientExport.getGrupsImportats()!=null) {
			for (GrupResource grupResource: metaExpedientExport.getGrupsImportats()) {
				
				GrupDto grupDto = new GrupDto();
				
				grupDto.setCodi(grupResource.getCodi());
				grupDto.setDescripcio(grupResource.getDescripcio());
				
				grupsImportats.add(grupDto);
			}
		}
		target.setGrups(grupsImportats);
		target.setGrupsCount(grupsImportats.size());
		
		/**
		 * CARPETES PER DEFECTE
		 */
		List<MetaExpedientCarpetaMinDto> carpetesImportats = new ArrayList<MetaExpedientCarpetaMinDto>();
		if (metaExpedientExport.getCarpetesImportats()!=null) {
			for (MetaExpedientCarpetaResource carpetaMetaExpResource: metaExpedientExport.getCarpetesImportats()) {
				MetaExpedientCarpetaMinDto carpetaDto = new MetaExpedientCarpetaMinDto();
				carpetaDto.setId(carpetaMetaExpResource.getId());
				carpetaDto.setNom(carpetaMetaExpResource.getNom());
				if (carpetaMetaExpResource.getPare()!=null) {
					MetaExpedientCarpetaMinDto pare = new MetaExpedientCarpetaMinDto();
					pare.setId(carpetaMetaExpResource.getPare().getId());
					pare.setNom(carpetaMetaExpResource.getPare().getDescription());
					carpetaDto.setPare(pare);
				}
				
				carpetesImportats.add(carpetaDto);
			}
		}
		target.setCarpetes(carpetesImportats);
		
    	return target;
    }
    
    private boolean elementValidacioExclos(
    		ItemValidacioTascaEnum itemValidacio,
    		Long itemId,
    		List<MetaDocumentResource> metaDocumentsImportats,
    		List<MetaDadaResource> metaDadesImportats) {
    	switch (itemValidacio) {
		case DADA:
			if (metaDadesImportats!=null) {
				for (MetaDadaResource metaDada: metaDadesImportats) {
					if (metaDada.getImportacioId().equals(itemId) && !metaDada.isImportar()) {
						return true;
					}
				}
			}
			break;

		case DOCUMENT:
			if (metaDocumentsImportats!=null) {
				for (MetaDocumentResource metaDoc: metaDocumentsImportats) {
					if (metaDoc.getImportacioId().equals(itemId) && !metaDoc.isImportar()) {
						return true;
					}
				}
			}			
			break;
		}
    	return false;
    }
    
    private class CrearReglaActionExecutor implements ActionExecutor<MetaExpedientResourceEntity, Serializable, Serializable> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {
		}
	
		@Override
		public Serializable exec(String code, MetaExpedientResourceEntity entity, Serializable params) throws ActionExecutionException {
			try {
				return metaExpedientHelper.crearReglaDistribucio(entity.getId());
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/metaExpedient/"+entity.getId()+"/CrearReglaActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, messageHelper.getMessage("message.common.action.error")+": "+e.getMessage());
			}
		}
    }
    
    private class ActualitzarProcedimentsRolsacActionExecutor implements ActionExecutor<MetaExpedientResourceEntity, MassiveAction, Serializable> {

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {
		}

		@Override
		public Serializable exec(String code, MetaExpedientResourceEntity entity, MassiveAction params) throws ActionExecutionException {
			String entitatActualCodi = configHelper.getEntitatActualCodi();
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
			ProgresActualitzacioDto progresActualitzacioDto = new ProgresActualitzacioDto();
			List<MetaExpedientEntity> metaExpedients = new ArrayList<MetaExpedientEntity>();
			if (params.getIds()!=null) {
				
				for (Long idProc: params.getIds()) {
					metaExpedients.add(metaExpedientRepository.findById(idProc).get());
				}
				
				metaExpedientHelper.actualitzarProcediments(
						entitat,
						metaExpedients,
						LocaleContextHolder.getLocale(),
						progresActualitzacioDto);
			}
			return progresActualitzacioDto;
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
    			
    			String fileNom = mere.getCodi().replaceAll("[^a-zA-Z0-9-]", "_")+"_export";
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