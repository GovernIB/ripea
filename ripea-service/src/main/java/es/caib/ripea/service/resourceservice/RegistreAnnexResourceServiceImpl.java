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

import es.caib.plugins.arxiu.api.Document;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientPeticioResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.RegistreAnnexResourceEntity;
import es.caib.ripea.persistence.repository.ExecucioMassivaContingutRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.persistence.repository.RegistreAnnexRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.ExecucioMassivaHelper;
import es.caib.ripea.service.helper.ExpedientHelper;
import es.caib.ripea.service.helper.ExpedientPeticioHelper;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.helper.PermisosPerAnotacions;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.base.model.ReportFileType;
import es.caib.ripea.service.intf.dto.CodiValorDto;
import es.caib.ripea.service.intf.dto.ElementTipusEnumDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaTipusDto;
import es.caib.ripea.service.intf.model.ContingutResource;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.model.RegistreAnnexResource;
import es.caib.ripea.service.intf.model.RegistreAnnexResource.ReintentarAnnexPendentForm;
import es.caib.ripea.service.intf.model.RegistreResource;
import es.caib.ripea.service.intf.resourceservice.RegistreAnnexResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistreAnnexResourceServiceImpl extends BaseMutableResourceService<RegistreAnnexResource, Long, RegistreAnnexResourceEntity> implements RegistreAnnexResourceService {

	private final PluginHelper pluginHelper;
	private final ConfigHelper configHelper;
	private final MessageHelper messageHelper;
	private final ExpedientHelper expedientHelper;
	private final ExcepcioLogHelper excepcioLogHelper;
	private final EntityComprovarHelper entityComprovarHelper;
	private final ExpedientPeticioHelper expedientPeticioHelper;
	private final ExecucioMassivaHelper execucioMassivaHelper;
	private final ExecucioMassivaContingutRepository execucioMassivaContingutRepository;

	private final RegistreAnnexRepository registreAnnexRepository;
	private final OrganGestorRepository organGestorRepository;

    @PostConstruct
    public void init() {
    	register(RegistreAnnexResource.REPORT_DOWNLOAD_ANNEX, new DescarregarAnnexReportGenerator());
        register(RegistreAnnexResource.PERSPECTIVE_FIRMES, new AnnexFirmesPerspectiveApplicator());
        register(RegistreAnnexResource.PERSPECTIVE_REGISTRE, new AnnexRegistrePerspectiveApplicator());
        register(RegistreAnnexResource.PERSPECTIVE_EN_PROCES_ADJUNTAR_ANNEXOS_CODE, new EnProcesAdjuntarAnnexosPerspectiveApplicator());
        register(RegistreAnnexResource.ACTION_REINTENTAR_CODE, new ReintentarArxiuActionExecutor());
    }
    
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
    	String entitatActualCodi = configHelper.getEntitatActualCodi();
        String rolActual		 = configHelper.getRolActual();
    	EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
    	
    	Filter filtreIdsPermesos = null;
        Filter filtreBase = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(RegistreAnnexResource.Fields.registre + "." +ContingutResource.Fields.entitat + "." + EntitatResource.Fields.codi,
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
        );
        
        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
    	if (mapaNamedQueries.size()>0 && mapaNamedQueries.containsKey("MASSIU_PENDENT_PROCESSAR")) {
    		
    		OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitat.getId(), configHelper.getOrganActualCodi());
    		
    		PermisosPerAnotacions permisosPerAnotacions = expedientPeticioHelper.findPermisosPerAnotacions(
    				entitat.getId(),
    				null,
    				rolActual,
                    ogEntity!=null ?ogEntity.getId() :null);
    		
    		List<Long> idsAnexesPendentsProcessar = registreAnnexRepository.findIdsPendentsProcesar(
    				entitat,
    				rolActual, 
					permisosPerAnotacions.getProcedimentsPermesos(),
					permisosPerAnotacions.getAdminOrganCodisOrganAmbDescendents(),
					permisosPerAnotacions.isAdminOrganHasPermisAdminComu(),
					true, "", //filtre nom
					true, "", //filtre numero
					true, null, //filtre data inici
					true, null, //filtre data fi
					true, null, //filtre metaExpedient
					true, null //filtre expedient
			);
        	
    		if (idsAnexesPendentsProcessar.isEmpty()) {
    			return FilterBuilder.equal("id", 0).generate();
    		} else {
    		
		    	List<String> permesosClausulesIn = Utils.getIdsEnGruposMil(idsAnexesPendentsProcessar);
		    	if (permesosClausulesIn!=null) {
			        for (String aux: permesosClausulesIn) {
				        if (aux != null && !aux.isEmpty()) {
				        	filtreIdsPermesos = FilterBuilder.or(filtreIdsPermesos, Filter.parse("id IN (" + aux + ")"));
				        }
			        }
		    	}
		    	
		    	Filter filtreResultat = FilterBuilder.and(
		    			(currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
		    			filtreIdsPermesos);
		    	return filtreResultat.generate();
    		}
    	}
    	
    	return filtreBase.generate();
    }
    
    private class AnnexFirmesPerspectiveApplicator implements PerspectiveApplicator<RegistreAnnexResourceEntity, RegistreAnnexResource> {
        @Override
        public void applySingle(String code, RegistreAnnexResourceEntity entity, RegistreAnnexResource resource) throws PerspectiveApplicationException {
        	try {
        		resource.setFirmes(pluginHelper.validaSignaturaObtenirFirmes(entity.getUuid(), false));
        	} catch (Exception ex) {
        		//No s'han pogut obtenir les firmes del document en aquets moments, no ha de impedir que es mostri el document.
        	}
        }
    }

    private class AnnexRegistrePerspectiveApplicator implements PerspectiveApplicator<RegistreAnnexResourceEntity, RegistreAnnexResource> {
        @Override
        public void applySingle(String code, RegistreAnnexResourceEntity entity, RegistreAnnexResource resource) throws PerspectiveApplicationException {
        	if (entity.getRegistre() != null) {
                resource.setRegistreInfo(objectMappingHelper.newInstanceMap(entity.getRegistre(), RegistreResource.class));
                List<ExpedientPeticioResourceEntity> expedientPeticioList = entity.getRegistre().getExpedientPeticions();
                if (!expedientPeticioList.isEmpty()) {
                    resource.setExpedientInfo(objectMappingHelper.newInstanceMap(Hibernate.unproxy(expedientPeticioList.get(0).getExpedient()), ExpedientResource.class));
                }
            }
        }
    }
    
    private class EnProcesAdjuntarAnnexosPerspectiveApplicator implements PerspectiveApplicator<RegistreAnnexResourceEntity, RegistreAnnexResource> {
        @Override
        public void applySingle(String code, RegistreAnnexResourceEntity entity, RegistreAnnexResource resource) throws PerspectiveApplicationException {
            execucioMassivaContingutRepository
                    .findFirstByElementIdAndElementTipusAndExecucioMassivaTipusAndExecucioMassivaDataFiNull(
                            entity.getId(), ElementTipusEnumDto.ANNEX, ExecucioMassivaTipusDto.ADJUNTAR_ANNEXOS_PENDENTS)
                    .ifPresent(contingut -> resource.setExecucioMassivaAdjuntarAnnexosId(contingut.getExecucioMassiva().getId()));
        }
    }

    private class DescarregarAnnexReportGenerator implements ReportGenerator<RegistreAnnexResourceEntity, Serializable, Serializable> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

        @Override
        public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
        	Document document = pluginHelper.arxiuDocumentConsultar(null, data.get(0).toString(), null, true, true);
            return new DownloadableFile(
            		document.getNom(),
            		document.getContingut().getTipusMime(),
            		document.getContingut().getContingut());
        }
		
		@Override
		public List<Serializable> generateData(String code, RegistreAnnexResourceEntity entity, Serializable params) throws ReportGenerationException {
            List<Serializable> parametres = new ArrayList<Serializable>();
            parametres.add(entity.getUuid());
            return parametres;
		}
    }
    
    private class ReintentarArxiuActionExecutor implements ActionExecutor<RegistreAnnexResourceEntity, ReintentarAnnexPendentForm, Serializable> {

		@Override
		public void onChange(Serializable id, ReintentarAnnexPendentForm previous, String fieldName, Object fieldValue,Map<String, AnswerValue> answers, String[] previousFieldNames, ReintentarAnnexPendentForm target) {}

		@Override
		public Serializable exec(String code, RegistreAnnexResourceEntity entity, ReintentarAnnexPendentForm params) throws ActionExecutionException {
			try {
				
				if (!params.isMassivo()) {
					expedientHelper.retryCreateDocFromAnnex(
							params.getIds().get(0),
							params.getMetaDocument().getId(),
							configHelper.getRolActual());
				} else {
					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setTipus(ExecucioMassivaTipusDto.ADJUNTAR_ANNEXOS_PENDENTS);
					dto.setContingutIds(params.getIds());
					dto.setIdentificadorAuxiliar(params.getMetaDocument().getId());
					dto.setRolActual(configHelper.getRolActual());
					
			        String entitatActualCodi = configHelper.getEntitatActualCodi();
			        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
					
					execucioMassivaHelper.crearExecucioMassiva(entitat.getId(), dto, ElementTipusEnumDto.ANNEX);
				}

				int numElem = params!=null && params.getIds()!=null?params.getIds().size():0;
				return "{\"num\": \""+numElem+"\"}";
				
			} catch (Exception e) {
				String ids = Utils.getIdsSeparatsComa(params.getIds());
				excepcioLogHelper.addExcepcio("/annexPeticio/ReintentarArxiuActionExecutor",e,ids,"massiu="+params.isMassivo());
				String message = messageHelper.getMessage("message.common.action.error")+": "+e.getMessage();
				throw new ActionExecutionException(getResourceClass(), ids, code, message);
			}
		}
    }
}