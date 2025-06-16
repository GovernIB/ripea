package es.caib.ripea.service.resourceservice;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import es.caib.ripea.service.intf.base.model.FieldOption;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.Document;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientPeticioResourceEntity;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.EventHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.ExpedientPeticioHelper;
import es.caib.ripea.service.helper.PermisosPerAnotacions;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.base.model.ReportFileType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.ArxiuEstatEnumDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioEstatViewEnumDto;
import es.caib.ripea.service.intf.dto.NtiTipoDocumentoEnumDto;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource.AcceptarAnotacioForm;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource.RebutjarAnotacioForm;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
import es.caib.ripea.service.intf.model.RegistreAnnexResource;
import es.caib.ripea.service.intf.model.RegistreInteressatResource;
import es.caib.ripea.service.intf.model.RegistreResource;
import es.caib.ripea.service.intf.registre.RegistreAnnexFirmaTipusEnum;
import es.caib.ripea.service.intf.registre.RegistreAnnexNtiEstadoElaboracionEnum;
import es.caib.ripea.service.intf.registre.RegistreAnnexNtiOrigenEnum;
import es.caib.ripea.service.intf.resourceservice.ExpedientPeticioResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientPeticioResourceServiceImpl extends BaseMutableResourceService<ExpedientPeticioResource, Long, ExpedientPeticioResourceEntity> implements ExpedientPeticioResourceService {

	private final ConfigHelper configHelper;
	private final PluginHelper pluginHelper;
	private final EventHelper eventHelper;
	private final ExcepcioLogHelper excepcioLogHelper;
	private final ExpedientPeticioHelper expedientPeticioHelper;
	private final EntityComprovarHelper entityComprovarHelper;
	
	private final OrganGestorRepository organGestorRepository;
	
    @PostConstruct
    public void init() {
        register(ExpedientPeticioResource.PERSPECTIVE_REGISTRE_CODE, new RegistrePerspectiveApplicator());
        register(ExpedientPeticioResource.PERSPECTIVE_ESTAT_VIEW_CODE, new EstatViewPerspectiveApplicator());
        register(ExpedientPeticioResource.REPORT_DOWNLOAD_JUSTIFICANT, new DescarregarJustificantReportGenerator());
        register(ExpedientPeticioResource.ACTION_REBUTJAR_ANOTACIO, new RebutjarAnotacioActionExecutor());
        register(ExpedientPeticioResource.ACTION_ACCEPTAR_ANOTACIO, new AcceptarAnotacioActionExecutor());
        register(ExpedientPeticioResource.ACTION_ESTAT_DISTRIBUCIO, new CanviEstatDistribucioActionExecutor());        
        register(ExpedientPeticioResource.Fields.metaExpedient, new MetaExpedientOnchangeLogicProcessor());
    }

    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
        String entitatActualCodi = configHelper.getEntitatActualCodi();
        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
        
		Filter filtreBase = (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null;
        Filter filtreEntitat = FilterBuilder.equal(
        		ExpedientPeticioResource.Fields.registre + "." + RegistreResource.Fields.entitatCodi, 
        		entitat!=null?entitat.getUnitatArrel():"................................................................................");
        
        Filter filtrePermesos = null;
        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
    	if (mapaNamedQueries.size()>0) {
    		if (mapaNamedQueries.containsKey("LLISTAT_ANOTACIONS")) {
    			
    			String organActualCodi	 = configHelper.getOrganActualCodi();
    			String rolActual		 = configHelper.getRolActual();
    			
    			boolean isAdmin 		= "IPA_ADMIN".equals(rolActual);
    			boolean isAdminOrgan 	= "IPA_ORGAN_ADMIN".equals(rolActual);
//    			boolean isDissenyOrgan 	= "IPA_DISSENY".equals(rolActual);
//    			boolean isSuper 		= "IPA_SUPER".equals(rolActual);

    			//Admin no aplica filtres de permisos
    			if (!isAdmin) {
    			
	    			OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitat.getId(), organActualCodi);
					PermisosPerAnotacions permisosPerAnotacions = expedientPeticioHelper.findPermisosPerAnotacions(
							entitat.getId(),
							rolActual,
							ogEntity!=null?ogEntity.getId():null);
		
					//Aplica filtres de permisos per organ
					if (isAdminOrgan) {
						
				        String ogId = ExpedientPeticioResource.Fields.registre + "." + RegistreResource.Fields.destiCodi;
				        Filter filtreOrgansPermesos = null;
				        List<String> grupsOrgansPermesosClausulesIn = permisosPerAnotacions.getIdsOrganGestorsGruposMil();
				        if (grupsOrgansPermesosClausulesIn!=null) {
					        for (String aux: grupsOrgansPermesosClausulesIn) {
						        if (aux != null && !aux.isEmpty()) {
					        		filtreOrgansPermesos = FilterBuilder.or(filtreOrgansPermesos, Filter.parse(ogId + " IN (" + aux + ")"));
						        }
					        }
				        }
				        
				        filtrePermesos = filtreOrgansPermesos;
						
					} else { //Aplica filtres de permisos per procediment
						
				        String prId = ExpedientPeticioResource.Fields.metaExpedient + ".id";
				        Filter filtreProcedimentsPermesos = null;
				        List<String> grupsProcsPermesosClausulesIn = permisosPerAnotacions.getIdsProcedimentsGruposMil();
				        if (grupsProcsPermesosClausulesIn!=null) {
					        for (String aux: grupsProcsPermesosClausulesIn) {
						        if (aux != null && !aux.isEmpty()) {
					        		filtreProcedimentsPermesos = FilterBuilder.or(filtreProcedimentsPermesos, Filter.parse(prId + " IN (" + aux + ")"));
						        }
					        }
				        }
				        
				        String grId = ExpedientPeticioResource.Fields.grup + ".id";
				        Filter filtregrupsPermesos = null;
				        List<String> grupsgrupsPermesosClausulesIn = permisosPerAnotacions.getIdsGrupsGruposMil();
				        if (grupsgrupsPermesosClausulesIn!=null) {
					        for (String aux: grupsgrupsPermesosClausulesIn) {
						        if (aux != null && !aux.isEmpty()) {
						        	filtregrupsPermesos = FilterBuilder.or(filtregrupsPermesos, Filter.parse(grId + " IN (" + aux + ")"));
						        }
					        }
				        }
				        
				        String grAct = ExpedientPeticioResource.Fields.metaExpedient +"."+ MetaExpedientResource.Fields.gestioAmbGrupsActiva;
				        Filter notGestioGrupsActiva = FilterBuilder.equal(grAct, false);
				        Filter filterGEstioGrupsActius = FilterBuilder.or(notGestioGrupsActiva, filtregrupsPermesos);
				        
				        filtrePermesos = FilterBuilder.and(filtreProcedimentsPermesos, filterGEstioGrupsActius);
					}
    			}
    		}
    	}
        
        return FilterBuilder.and(filtreBase, filtreEntitat, filtrePermesos).generate();
    }
    
    @Override
	public ExpedientPeticioResource update(Long id, ExpedientPeticioResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
    	try {
    		expedientPeticioHelper.canviarProcediment(resource.getId(), resource.getMetaExpedient().getId(), resource.getGrup().getId());
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/anotacio/"+resource.getId()+"/update", ex);
    	}
    	return null;
    }
    
    private class MetaExpedientOnchangeLogicProcessor implements OnChangeLogicProcessor<ExpedientPeticioResource> {
		@Override
		public void onChange(Serializable id, ExpedientPeticioResource previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, ExpedientPeticioResource target) {
			if (fieldValue != null) {
				
			} else {
				target.setGrup(null);
			}
		}
    }
    
    private class RegistrePerspectiveApplicator implements PerspectiveApplicator<ExpedientPeticioResourceEntity, ExpedientPeticioResource> {
        @Override
        public void applySingle(String code, ExpedientPeticioResourceEntity entity, ExpedientPeticioResource resource) throws PerspectiveApplicationException {
            resource.setRegistreInfo(objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getRegistre()), RegistreResource.class));

            resource.getRegistreInfo().setInteressats(
                    entity.getRegistre().getInteressats().stream()
                            .map(interessat -> {
                                RegistreInteressatResource interessatResource = objectMappingHelper.newInstanceMap(interessat, RegistreInteressatResource.class);
                                return ResourceReference.<RegistreInteressatResource, Long>toResourceReference(interessatResource.getId(), interessatResource.getCodiNom());
                            })
                            .collect(Collectors.toList())
            );

            if (resource.getRegistreInfo().getJustificantArxiuUuid()!=null && Boolean.parseBoolean(configHelper.getConfig(PropertyConfig.INCORPORAR_JUSTIFICANT))) {
            	RegistreAnnexResource justificant = new RegistreAnnexResource();
            	Document documentDetalls = pluginHelper.arxiuDocumentConsultar(
    					null, 
    					resource.getRegistreInfo().getJustificantArxiuUuid(), 
    					null, 
    					true, 
    					false);
            	justificant.setTitol(documentDetalls.getNom());
            	if (documentDetalls.getContingut()!=null) {
            		justificant.setTamany(documentDetalls.getContingut().getTamany());
            		justificant.setTipusMime(documentDetalls.getContingut().getTipusMime());
            	}
            	justificant.setObservacions(documentDetalls.getDescripcio());
            	//TODO: Millora: es fan conversions d'enumerats que no farien falta si la classe destí tengues com a tipus d'atribut la clase enum del origen
            	try {
            		ArxiuEstatEnumDto estatArxiu = ArxiuEstatEnumDto.valueOf(documentDetalls.getEstat().toString());
            		justificant.setAnnexArxiuEstat(estatArxiu);
            	} catch (Exception ex) {}            	
            	if (documentDetalls.getMetadades()!=null) {
            		String extensio = documentDetalls.getMetadades().getExtensio()!=null?documentDetalls.getMetadades().getExtensio().toString():".pdf";
            		justificant.setNom(documentDetalls.getNom()+extensio);
            		justificant.setNtiFechaCaptura(documentDetalls.getMetadades().getDataCaptura());
            		if (ContingutOrigen.ADMINISTRACIO.equals(documentDetalls.getMetadades().getOrigen())) {
            			justificant.setNtiOrigen(RegistreAnnexNtiOrigenEnum.ADMINISTRACIO);
            		} else {
            			justificant.setNtiOrigen(RegistreAnnexNtiOrigenEnum.CIUTADA);
            		}
            		try {
            			NtiTipoDocumentoEnumDto enumTD = NtiTipoDocumentoEnumDto.valueOf(documentDetalls.getMetadades().getTipusDocumental().name());
            			justificant.setNtiTipoDocumental(enumTD);
            		} catch (Exception ex) {}
            		justificant.setUuid(documentDetalls.getIdentificador());
            		if (documentDetalls.getFirmes() != null && !documentDetalls.getFirmes().isEmpty()) {
            			justificant.setFirmaPerfil(documentDetalls.getFirmes().get(0).getPerfil().name());
            			try {
            				RegistreAnnexFirmaTipusEnum enumTF = RegistreAnnexFirmaTipusEnum.valueOf(documentDetalls.getFirmes().get(0).getTipus().name());
            				justificant.setFirmaTipus(enumTF);
            			} catch (Exception ex) {}
            		}
            		try {
            			RegistreAnnexNtiEstadoElaboracionEnum enumEE = RegistreAnnexNtiEstadoElaboracionEnum.valueOf(documentDetalls.getMetadades().getEstatElaboracio().name());
            			justificant.setNtiEstadoElaboracion(enumEE);
            		} catch (Exception ex) {}
            	}
            	resource.getRegistreInfo().setJustificant(justificant);
            }
        }
    }

    private class EstatViewPerspectiveApplicator implements PerspectiveApplicator<ExpedientPeticioResourceEntity, ExpedientPeticioResource> {
        @Override
        public void applySingle(String code, ExpedientPeticioResourceEntity entity, ExpedientPeticioResource resource) throws PerspectiveApplicationException {
            if (resource.getEstat() != null) {
                switch (resource.getEstat()) {
                    case PENDENT:
                        resource.setEstatView(ExpedientPeticioEstatViewEnumDto.PENDENT);
                        break;
                    case PROCESSAT_PENDENT:
                    case PROCESSAT_NOTIFICAT:
                        resource.setEstatView(ExpedientPeticioEstatViewEnumDto.ACCEPTAT);
                        break;
                    case REBUTJAT:
                        resource.setEstatView(ExpedientPeticioEstatViewEnumDto.REBUTJAT);
                        break;
                }
            }
        }
    }
    
    private class AcceptarAnotacioActionExecutor implements ActionExecutor<ExpedientPeticioResourceEntity, ExpedientPeticioResource.AcceptarAnotacioForm, Serializable> {

        @Override
        public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
            List<FieldOption> reslult = new ArrayList<>();
            if (ExpedientPeticioResource.AcceptarAnotacioForm.Fields.tipusDocument.equals(fieldName)){
                reslult.add(new FieldOption("test", "Prueba"));
            }
            return reslult;
        }

        @Override
		public void onChange(Serializable id, AcceptarAnotacioForm previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, AcceptarAnotacioForm target) {}

		@Override
		public Serializable exec(String code, ExpedientPeticioResourceEntity entity, AcceptarAnotacioForm params) throws ActionExecutionException {
			try {
				if (ExpedientPeticioAccioEnumDto.CREAR.equals(params.getAccio())) {
					//TODO: ExpedientServiceImpl.create
				} else {
					//TODO: ExpedientServiceImpl.incorporar
				}
				return objectMappingHelper.newInstanceMap(entity, ExpedientPeticioResource.class);
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/anotacio/"+entity.getId()+"/AcceptarAnotacioActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, "Error al acceptar la anotació: "+e.getMessage());
			}
		}
    }
    
    private class RebutjarAnotacioActionExecutor implements ActionExecutor<ExpedientPeticioResourceEntity, ExpedientPeticioResource.RebutjarAnotacioForm, Serializable> {

		@Override
		public void onChange(Serializable id, RebutjarAnotacioForm previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, RebutjarAnotacioForm target) {}

		@Override
		public Serializable exec(String code, ExpedientPeticioResourceEntity entity, RebutjarAnotacioForm params) throws ActionExecutionException {
			try {
				expedientPeticioHelper.rebutjar(entity.getId(), params.getMotiu());
				try {
					eventHelper.notifyAnotacionsPendents(entity.getId());
				} catch (Exception ex) {}
				return objectMappingHelper.newInstanceMap(entity, ExpedientPeticioResource.class);
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/anotacio/"+entity.getId()+"/RebutjarAnotacioActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, "Error al rebutjar la anotació: "+e.getMessage());
			}
		}
    }
    
    private class CanviEstatDistribucioActionExecutor implements ActionExecutor<ExpedientPeticioResourceEntity, Serializable, Serializable> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

		@Override
		public Serializable exec(String code, ExpedientPeticioResourceEntity entity, Serializable params) throws ActionExecutionException {
			try {
				expedientPeticioHelper.reintentarCanviEstatDistribucio(entity.getId());
				return objectMappingHelper.newInstanceMap(entity, ExpedientPeticioResource.class);
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/anotacio/"+entity.getId()+"/RebutjarAnotacioActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, "Error al rebutjar la anotació: "+e.getMessage());
			}
		}
    }
    
    private class DescarregarJustificantReportGenerator implements ReportGenerator<ExpedientPeticioResourceEntity, Serializable, Serializable> {

    	@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
    		
    		DownloadableFile resultat = null;
    		
    		try {		
	    		
            	Document documentDetalls = pluginHelper.arxiuDocumentConsultar(
    					null, 
    					data.get(0).toString(), 
    					null, 
    					true, 
    					false);
            	
        		String extensio = documentDetalls.getMetadades().getExtensio()!=null?documentDetalls.getMetadades().getExtensio().toString():".pdf";
            	
            	resultat = new DownloadableFile(
            			documentDetalls.getNom()+extensio,
            			documentDetalls.getContingut().getTipusMime(),
            			documentDetalls.getContingut().getContingut());

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedientPeticio/"+data.get(1)+"/DescarregarJustificantReportGenerator", e);
				throw new ReportGenerationException(getResourceClass(), data.get(1).toString(), code, "S'ha produit un error al descarregar el jsutificant del registre.");
			}
            
            return resultat;
		}

		@Override
		public List<Serializable> generateData(String code, ExpedientPeticioResourceEntity entity, Serializable params) throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
			parametres.add(entity.getRegistre().getJustificantArxiuUuid());
			parametres.add(entity.getId());
			return parametres;
		}
    	
		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}
    }
}