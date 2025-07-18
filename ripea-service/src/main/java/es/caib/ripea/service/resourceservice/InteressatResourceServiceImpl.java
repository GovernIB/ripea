package es.caib.ripea.service.resourceservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.InteressatEntity;
import es.caib.ripea.persistence.entity.resourceentity.InteressatResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.InteressatResourceRepository;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.persistence.repository.InteressatRepository;
import es.caib.ripea.plugin.dadesext.Municipi;
import es.caib.ripea.plugin.dadesext.Pais;
import es.caib.ripea.plugin.dadesext.Provincia;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.base.springfilter.FilterSpecification;
import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.ExpedientInteressatHelper;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.helper.UnitatOrganitzativaHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.base.exception.ResourceNotDeletedException;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.base.model.FieldOption;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.ReportFileType;
import es.caib.ripea.service.intf.dto.ComunitatDto;
import es.caib.ripea.service.intf.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.InteressatDto;
import es.caib.ripea.service.intf.dto.InteressatImportacioTipusDto;
import es.caib.ripea.service.intf.dto.InteressatTipusEnum;
import es.caib.ripea.service.intf.dto.MunicipiDto;
import es.caib.ripea.service.intf.dto.NivellAdministracioDto;
import es.caib.ripea.service.intf.dto.PaisDto;
import es.caib.ripea.service.intf.dto.ProvinciaDto;
import es.caib.ripea.service.intf.dto.UnitatOrganitzativaDto;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.model.InteressatResource.UnitatOrganitzativaFormFilter;
import es.caib.ripea.service.intf.resourceservice.InteressatResourceService;
import es.caib.ripea.service.intf.utils.Utils;
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
public class InteressatResourceServiceImpl extends BaseMutableResourceService<InteressatResource, Long, InteressatResourceEntity> implements InteressatResourceService {

	private final UnitatOrganitzativaHelper unitatOrganitzativaHelper;
    private final ExpedientInteressatHelper expedientInteressatHelper;
    private final EntityComprovarHelper entityComprovarHelper;
    private final ExcepcioLogHelper excepcioLogHelper;
    private final ConfigHelper configHelper;
    private final PluginHelper pluginHelper;
    private final CacheHelper cacheHelper;
    private final MessageHelper messageHelper;

    private final ExpedientRepository expedientRepository;
    private final InteressatRepository interessatRepository;
    private final InteressatResourceRepository interessatResourceRepository;

    @PostConstruct
    public void init() {
        register(InteressatResource.Fields.documentNum, new NumDocOnchangeLogicProcessor());
        register(InteressatResource.PERSPECTIVE_REPRESENTANT_CODE, new RespresentantPerspectiveApplicator());
        register(InteressatResource.PERSPECTIVE_ADRESSA_CODE, new AdressaPerspectiveApplicator());
        register(InteressatResource.ACTION_EXPORTAR_CODE, new ExportarReportGenerator());
        register(InteressatResource.ACTION_IMPORTAR_CODE, new ImportarInteressatsActionExecutor());
        register(InteressatResource.ACTION_GUARDAR_ARXIU, new GuardarArxiuActionExecutor());
        
        register(InteressatResource.Fields.tipus, new TipusOnchangeLogicProcessor());
        register(InteressatResource.Fields.organCodi, new UnitatsOrganitzativesOnchangeLogicProcessor());
        
        register(InteressatResource.Fields.municipi, new MunicipiFieldOptionsProvider());
        register(InteressatResource.Fields.provincia, new ProvinciaFieldOptionsProvider());
        register(InteressatResource.Fields.pais, new PaisFieldOptionsProvider());
        register(InteressatResource.Fields.organCodi, new UnitatsOrganitzativesOptionsProvider());

        register(InteressatResource.FILTER_CODE, new FilterOnchangeLogicProcessor());
    }

    @Override
    public List<InteressatResource> findBySpringFilter(String springFilter) {
        FilterSpecification<InteressatResourceEntity> spec = new FilterSpecification<>(springFilter);
        return interessatResourceRepository.findAll(spec).stream()
                   .map(interesatEntity -> objectMappingHelper.newInstanceMap(interesatEntity, InteressatResource.class))
                   .collect(Collectors.toList());
    }

    private class FilterOnchangeLogicProcessor implements FilterProcessor<UnitatOrganitzativaFormFilter> {
        @Override
        public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
            List<FieldOption> resultat = new ArrayList<FieldOption>();
            switch (fieldName) {
                case UnitatOrganitzativaFormFilter.Fields.nivell:
        			List<NivellAdministracioDto> nivells = cacheHelper.findNivellAdministracio();
        			if (nivells!=null) {
        				for (NivellAdministracioDto nvl: nivells) {
        					resultat.add(new FieldOption(nvl.getCodi().toString(), nvl.getDescripcio()));
        				}
        			}                	
                	break;
                case UnitatOrganitzativaFormFilter.Fields.comunitatAutonoma:
        			List<ComunitatDto> comunitats = cacheHelper.findComunitats();
        			if (comunitats!=null) {
        				for (ComunitatDto cmnt: comunitats) {
        					resultat.add(new FieldOption(cmnt.getCodi(), cmnt.getNom()));
        				}
        			}
                    break;
                case UnitatOrganitzativaFormFilter.Fields.provincia:
                    Map<String, String[]> params = new HashMap<>();
                    params.put("pais", new String[]{"724"});
                    params.putAll(requestParameterMap);
                	resultat = new ProvinciaFieldOptionsProvider().getOptions(fieldName, params);
                    break;
                case UnitatOrganitzativaFormFilter.Fields.municipi:
                	resultat = new MunicipiFieldOptionsProvider().getOptions(fieldName, requestParameterMap);
                    break;
            }
            return resultat;
        }

        @Override
        public void onChange(Serializable id, UnitatOrganitzativaFormFilter previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, UnitatOrganitzativaFormFilter target) {}
    }

    private class TipusOnchangeLogicProcessor implements OnChangeLogicProcessor<InteressatResource> {
        @Override
        public void onChange(Serializable id, InteressatResource previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, InteressatResource target) {
            if (fieldValue!=null) {
                switch ((InteressatTipusEnum)fieldValue){
                    case InteressatPersonaFisicaEntity:
                        target.setOrganCodi(null);
                        break;
                    case InteressatPersonaJuridicaEntity:
                        target.setDocumentTipus(InteressatDocumentTipusEnumDto.NIF);
                        target.setOrganCodi(null);
                        break;
                    case InteressatAdministracioEntity:
                        target.setDocumentTipus(InteressatDocumentTipusEnumDto.CODI_ORIGEN);
                        target.setDocumentNum(null);
                        target.setCodiPostal(null);
                        target.setAdresa(null);
	                    target.setPais(null); //Posam la adreça null ja que es carregarà automaticament al seleccionat una unitat adminsitrativa
	                    target.setProvincia(null);
	                    target.setMunicipi(null);
                        break;
                }
            }
        }
    }
    private class UnitatsOrganitzativesOnchangeLogicProcessor implements OnChangeLogicProcessor<InteressatResource> {
        @Override
        public void onChange(Serializable id, InteressatResource previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, InteressatResource target) {
            if (fieldValue!=null) {
                UnitatOrganitzativaDto uoDto = unitatOrganitzativaHelper.findAmbCodiAndAdressafisica(fieldValue.toString());
                target.setNom(Utils.abbreviate(uoDto.getDenominacio(), 30));
                target.setPais(uoDto.getCodiPais());
                target.setProvincia(uoDto.getCodiProvincia());
                target.setMunicipi(uoDto.getLocalitat());
                target.setCodiPostal(uoDto.getCodiPostal());
                target.setAdresa(uoDto.getAdressa());
                target.setDocumentNum(uoDto.getNifCif());
                target.setEmail("");
                target.setTelefon("");
                target.setObservacions("");
            } else {
            	target.setNom("");
                target.setPais("");
                target.setProvincia("");
                target.setMunicipi("");
                target.setCodiPostal("");
                target.setAdresa("");
                target.setDocumentNum("");
            }
        }
    }

    public class PaisFieldOptionsProvider implements FieldOptionsProvider {
		public List<FieldOption> getOptions(String fieldName, Map<String,String[]> requestParameterMap) {
			List<PaisDto> paisos = cacheHelper.findPaisos();
			List<FieldOption> resultat = new ArrayList<FieldOption>();
			if (paisos!=null) {
				for (PaisDto pais: paisos) {
					resultat.add(new FieldOption(pais.getCodi(), pais.getNom()));
				}
			}
			return resultat;
		}
	}
    
    public class UnitatsOrganitzativesOptionsProvider implements FieldOptionsProvider {
        
    	private String getFromMap(String param, Map<String,String[]> requestParameterMap){
            return (requestParameterMap.containsKey(param) && requestParameterMap.get(param).length>0)
                    ? requestParameterMap.get(param)[0]
                    : "";
        }

		public List<FieldOption> getOptions(String fieldName, Map<String,String[]> requestParameterMap) {
			
			//Evitar java.lang.IllegalStateException: No modifications are allowed to a locked ParameterMap
			Map<String, String[]> mutableMap = new HashMap<>(requestParameterMap);
			
			List<UnitatOrganitzativaDto> uos = null;
            boolean recuperarValors = Boolean.parseBoolean(getFromMap("isInteressatAdministracio", mutableMap));
            mutableMap.remove("isInteressatAdministracio");
            List<FieldOption> resultat = new ArrayList<FieldOption>();
            
            if (recuperarValors) {
				if (mutableMap.isEmpty()) {
					String entitatActual = configHelper.getEntitatActualCodi();
					if (Utils.hasValue(entitatActual)) {
						uos = cacheHelper.findUnitatsOrganitzativesPerEntitat(entitatActual).toDadesList();
					}
				} else {
					String codiDir3 = getFromMap(UnitatOrganitzativaFormFilter.Fields.nif, mutableMap);
					String denominacio = getFromMap(UnitatOrganitzativaFormFilter.Fields.nom, mutableMap);
					String nivellAdm = getFromMap(UnitatOrganitzativaFormFilter.Fields.nivell, mutableMap);
					String comunitat = getFromMap(UnitatOrganitzativaFormFilter.Fields.comunitatAutonoma, mutableMap);
					String provincia = getFromMap(UnitatOrganitzativaFormFilter.Fields.provincia, mutableMap);
					String municipi = getFromMap(UnitatOrganitzativaFormFilter.Fields.municipi, mutableMap);
	                Boolean arrel = Boolean.parseBoolean(getFromMap(UnitatOrganitzativaFormFilter.Fields.unitatArrel, mutableMap));
					uos = pluginHelper.unitatsOrganitzativesFindByFiltre(codiDir3, denominacio, nivellAdm, comunitat, provincia, municipi, arrel);
				}
				if (uos!=null) {
					for (UnitatOrganitzativaDto uo: uos) {
						resultat.add(new FieldOption(uo.getCodi(), uo.getDenominacio()));
					}
				}
            }
			return resultat;
		}
    }
    
    public class ProvinciaFieldOptionsProvider implements FieldOptionsProvider {
		public List<FieldOption> getOptions(String fieldName, Map<String,String[]> requestParameterMap) {
			
			String[] requestParam = requestParameterMap.get(InteressatResource.Fields.pais);
			String paisCodi = requestParam!=null?requestParam[0]:"";
			
			List<FieldOption> resultat = new ArrayList<FieldOption>();
			List<ProvinciaDto> provincies = null;
					
			if (paisCodi!=null && paisCodi.equals("724")) {
				provincies = cacheHelper.findProvincies();
			}
			
			String[] requestParamCA = requestParameterMap.get(InteressatResource.UnitatOrganitzativaFormFilter.Fields.comunitatAutonoma);
			if (requestParamCA!=null && requestParamCA.length>0 && Utils.hasValue(requestParamCA[0])) {
				provincies = cacheHelper.findProvinciesPerComunitat(requestParamCA[0]);
			}
			
			if (provincies!=null) {
				for (ProvinciaDto prov: provincies) {
					resultat.add(new FieldOption(prov.getCodi(), prov.getNom()));
				}
			}
				
			Collections.sort(resultat, Comparator.comparing(FieldOption::getDescription));
			return resultat;
		}
	}
    
    public class MunicipiFieldOptionsProvider implements FieldOptionsProvider {
		public List<FieldOption> getOptions(String fieldName, Map<String,String[]> requestParameterMap) {
			
			String[] requestParam = requestParameterMap.get(InteressatResource.Fields.provincia);
			String provinciaCodi = requestParam!=null?requestParam[0]:"";
			
			if (!Utils.hasValue(provinciaCodi)) {
				String[] provinciaFilter = requestParameterMap.get(InteressatResource.UnitatOrganitzativaFormFilter.Fields.provincia);
				provinciaCodi = provinciaFilter!=null?provinciaFilter[0]:"";
			}
			
			List<FieldOption> resultat = new ArrayList<FieldOption>();
			if (Utils.hasValue(provinciaCodi)) {
				List<MunicipiDto> municipis = cacheHelper.findMunicipisPerProvincia(provinciaCodi);
				if (municipis!=null) {
					for (MunicipiDto municipi: municipis) {
						resultat.add(new FieldOption(municipi.getCodi(), municipi.getNom()));
					}
				}
			}
			return resultat;
		}
	}
    
    @Override
    protected void afterConversion(InteressatResourceEntity entity, InteressatResource resource) {
        resource.setHasRepresentats(!entity.getRepresentats().isEmpty());
    }
    
    @Override
    protected void beforeCreateSave(InteressatResourceEntity entity, InteressatResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        if(resource.getRepresentat()!=null){
            Optional<InteressatResourceEntity> interessatResourceEntity = interessatResourceRepository.findById(resource.getRepresentat().getId());
            interessatResourceEntity.ifPresent((interessat)->interessat.setRepresentant(entity));
        }
    }
    
    @Override
    protected void afterCreateSave(InteressatResourceEntity entity, InteressatResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {
    	ExpedientEntity expedient 	= expedientRepository.findById(entity.getExpedient().getId()).get();
    	InteressatEntity interessat = interessatRepository.findById(entity.getId()).get();
    	expedientInteressatHelper.arxiuPropagarInteressats(expedient, interessat);
    	cacheHelper.evictErrorsValidacioPerNode(expedient);
    }

    @Override
    protected void beforeDelete(InteressatResourceEntity entity, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotDeletedException {
        if (entity.isEsRepresentant()) {
            entity.getRepresentats().forEach((representat) -> {
                representat.setRepresentant(null);
            });
        }
    }

    @Override
    protected void afterDelete(InteressatResourceEntity entity, Map<String, AnswerRequiredException.AnswerValue> answers) {
        InteressatResourceEntity representant = entity.getRepresentant();
        if (representant!=null && representant.isEsRepresentant() && representant.getRepresentats().isEmpty()){
            interessatResourceRepository.delete(representant);
        }
    }

    private class RespresentantPerspectiveApplicator implements PerspectiveApplicator<InteressatResourceEntity, InteressatResource> {
        @Override
        public void applySingle(String code, InteressatResourceEntity entity, InteressatResource resource) throws PerspectiveApplicationException {
            if (entity.getRepresentant() != null) {
                resource.setRepresentantInfo(objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getRepresentant()), InteressatResource.class));
            }
        }
    }
    
    private class AdressaPerspectiveApplicator implements PerspectiveApplicator<InteressatResourceEntity, InteressatResource> {
        @Override
        public void applySingle(String code, InteressatResourceEntity entity, InteressatResource resource) throws PerspectiveApplicationException {
        	if (Utils.hasValue(resource.getPais())) {
        		Pais pais = pluginHelper.dadesExternesPaisFindByCodi(resource.getPais());
        		resource.setPaisNom(pais!=null?pais.getNom():"");
        	}
        	if (Utils.hasValue(resource.getProvincia())) {
        		Provincia prov = pluginHelper.dadesExternesProvinciesFindByCodi(resource.getProvincia());
        		resource.setProvinciaNom(prov!=null?prov.getNom():"");
        		
            	if (Utils.hasValue(resource.getMunicipi())) {
            		Municipi muni = pluginHelper.dadesExternesMunicipisFindByCodi(resource.getProvincia(), resource.getMunicipi());
            		resource.setMunicipiNom(muni!=null?muni.getNom():"");
            	}
        	}
        }
    }
    
    private class NumDocOnchangeLogicProcessor implements OnChangeLogicProcessor<InteressatResource> {

        public static final String NOT_REPRESENT_HIMSELF = "NOT_REPRESENT_HIMSELF";

        @Override
        public void onChange(Serializable id, InteressatResource previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, InteressatResource target) {

            if (fieldValue!=null && fieldValue.toString().length()==9){
                Optional<InteressatResourceEntity> resource = interessatResourceRepository.findByExpedientIdAndDocumentNum(previous.getExpedient().getId(), fieldValue.toString());
                resource.ifPresent((interessatResourceEntity)-> {
                    if (
                            !answers.containsKey(NOT_REPRESENT_HIMSELF) &&
                            (previous.getRepresentat()!=null && Objects.equals(previous.getRepresentat().getId(), interessatResourceEntity.getId()))
                            || (previous.getRepresentant()!=null && Objects.equals(previous.getRepresentant().getId(), interessatResourceEntity.getId()))
                    ){
                        throw new AnswerRequiredException(InteressatResource.class, NOT_REPRESENT_HIMSELF, messageHelper.getMessage("es.caib.ripea.service.intf.resourcevalidation.InteressatValid.representHimself"));
                    }

//                    if (!Objects.equals(interessatResourceEntity.getId(), previous.getId())) {
//                        InteressatResource interessatResource = objectMappingHelper.newInstanceMap(interessatResourceEntity, InteressatResource.class);
//                        objectMappingHelper.map(interessatResource, target, "esRepresentant");
//                    }
                });
            }
        }
    }


    private class ExportarReportGenerator implements ReportGenerator<InteressatResourceEntity,InteressatResource.ExportInteressatsFormAction, Serializable> {

        @Override
        public void onChange(Serializable id, InteressatResource.ExportInteressatsFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, InteressatResource.ExportInteressatsFormAction target) {}

        @Override
        public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                Long expedientId = (Long)data.get(0);
                ExpedientResource.MassiveAction params = (ExpedientResource.MassiveAction)data.get(1);
//                entityComprovarHelper.comprovarExpedient(expedientId, true, true, false, false, false, configHelper.getRolActual());
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(baos, expedientInteressatHelper.findByIds(params.getIds()));
                DownloadableFile resultat = new DownloadableFile("Interessats_expedient_"+expedientId+".json", "application/json", baos.toByteArray());
                return resultat;
            } catch (Exception e) {
                excepcioLogHelper.addExcepcio("/expedient/ExportarInteressatsReportGenerator", e);
                throw new ReportGenerationException(getResourceClass(), null, code, "interessat.export.reject");
            } finally {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public List<Serializable> generateData(String code, InteressatResourceEntity entity, InteressatResource.ExportInteressatsFormAction params) throws ReportGenerationException {
            List<Serializable> parametres = new ArrayList<Serializable>();
            parametres.add(params.getExpedient().getId());
            parametres.add(params);
            return parametres;
        }
    }

    private class GuardarArxiuActionExecutor implements ActionExecutor<InteressatResourceEntity, Serializable, Serializable> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

		@Override
		public Serializable exec(String code, InteressatResourceEntity entity, Serializable params) throws ActionExecutionException {
			try {
				Exception errorGuardant = expedientInteressatHelper.guardarInteressatsArxiu(entity.getExpedient().getId());
				if (errorGuardant!=null) {
					excepcioLogHelper.addExcepcio("/expedient/interessat/"+entity.getId()+"GuardarArxiuActionExecutor.onChange", errorGuardant);
					throw new ActionExecutionException(getResourceClass(), entity.getId(), code, errorGuardant);
				}
            } catch (Exception e) {
                excepcioLogHelper.addExcepcio("/expedient/interessats/"+entity.getId()+"GuardarArxiuActionExecutor.onChange", e);
                throw new ActionExecutionException(getResourceClass(), entity.getId(), code, e.getMessage());
            }
			return objectMappingHelper.newInstanceMap(entity, InteressatResource.class);
		}
    	
    }

    private class ImportarInteressatsActionExecutor implements ActionExecutor<InteressatResourceEntity, InteressatResource.ImportarInteressatsFormAction, Serializable> {

        @Override
        public void onChange(Serializable id, InteressatResource.ImportarInteressatsFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, InteressatResource.ImportarInteressatsFormAction target) {
            try {
                if (previous.getTipusImportacio().equals(InteressatImportacioTipusDto.JSON)) {
                    if (fieldValue!=null) {
                        List<InteressatDto> listaInteressatsFitxer = new ArrayList<InteressatDto>();
                        if (InteressatResource.ImportarInteressatsFormAction.Fields.fitxerJsonInteressats.equals(fieldName)) {
                            ObjectMapper objectMapper = new ObjectMapper();
                            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                            listaInteressatsFitxer = objectMapper.readValue(
                                    ((FileReference)fieldValue).getContent(),
                                    new TypeReference<List<InteressatDto>>() {});
                        } else {
                            listaInteressatsFitxer = expedientInteressatHelper.extreureInteressatsExcel(
                                    new ByteArrayInputStream(((FileReference)fieldValue).getContent()),
                                    previous.getExpedient().getId());
                        }

                        //Abans de retornar la llista de interessats, comprovam si existeixen al expedient actual
                        if (!listaInteressatsFitxer.isEmpty()) {
                            //Només fem la consulta en cas necessari
                            List<InteressatEntity> interessatsExpActual = interessatRepository.findByExpedientId(previous.getExpedient().getId());
                            for (InteressatDto interessatDto: listaInteressatsFitxer) {
                                if (interessatsExpActual!=null) {
                                    for (InteressatEntity interessatExp: interessatsExpActual) {
                                        if (interessatExp.getDocumentNum().equalsIgnoreCase(interessatDto.getDocumentNum())) {
                                            interessatDto.setJaExistentExpedient(true);
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        target.setInteressatsFitxer(listaInteressatsFitxer);
                    } else {
                        target.setInteressatsFitxer(new ArrayList<>());
                    }
                }
            } catch (Exception e) {
                excepcioLogHelper.addExcepcio("/expedient/interessats/ImportarInteressatsActionExecutor.onChange", e);
            }
        }

        @Override
        public Serializable exec(String code, InteressatResourceEntity entity, InteressatResource.ImportarInteressatsFormAction params) throws ActionExecutionException {
            try {
                String rolActual = configHelper.getRolActual();
                String entitatActual = configHelper.getEntitatActualCodi();
                EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatActual, false, false, false, true, false);
                expedientInteressatHelper.importarInteressats(
                        entitatEntity.getId(),
                        params.getExpedient().getId(),
                        rolActual,
                        params.getInteressatsPerImportar());
            } catch (Exception e) {
                excepcioLogHelper.addExcepcio("/expedient/"+params.getExpedient().getId()+"/ImportarInteressatsActionExecutor", e);
                throw new ActionExecutionException(getResourceClass(), params.getExpedient().getId(), code, e.getMessage());
            }
            return null;
        }
    }

}