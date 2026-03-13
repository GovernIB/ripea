package es.caib.ripea.service.resourceservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.groups.Default;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.SmartValidator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.InteressatEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.resourceentity.InteressatGrupResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.InteressatResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.InteressatGrupResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.InteressatResourceRepository;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.InteressatRepository;
import es.caib.ripea.plugin.dadesext.Municipi;
import es.caib.ripea.plugin.dadesext.Pais;
import es.caib.ripea.plugin.dadesext.Provincia;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.base.springfilter.FilterSpecification;
import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.EventHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.ExecucioMassivaHelper;
import es.caib.ripea.service.helper.ExpedientInteressatHelper;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.helper.MetaExpedientHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.helper.UnitatOrganitzativaHelper;
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
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ComunitatDto;
import es.caib.ripea.service.intf.dto.ElementTipusEnumDto;
import es.caib.ripea.service.intf.dto.EntregaPostalTipusEnum;
import es.caib.ripea.service.intf.dto.ExecucioMassivaContingutDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaTipusDto;
import es.caib.ripea.service.intf.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.InteressatImportacioTipusDto;
import es.caib.ripea.service.intf.dto.InteressatTipusEnum;
import es.caib.ripea.service.intf.dto.MunicipiDto;
import es.caib.ripea.service.intf.dto.NivellAdministracioDto;
import es.caib.ripea.service.intf.dto.PaisDto;
import es.caib.ripea.service.intf.dto.ProvinciaDto;
import es.caib.ripea.service.intf.dto.UnitatOrganitzativaDto;
import es.caib.ripea.service.intf.model.ContingutResource;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.model.InteressatGrupResource;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.model.InteressatResource.UnitatOrganitzativaFormFilter;
import es.caib.ripea.service.intf.model.NodeResource.MassiveAction;
import es.caib.ripea.service.intf.model.sse.ErrorsValidacioChangedEvent;
import es.caib.ripea.service.intf.resourceservice.InteressatResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.resourcehelper.InteressatResourceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InteressatResourceServiceImpl extends BaseMutableResourceService<InteressatResource, Long, InteressatResourceEntity> implements InteressatResourceService {

    private final UnitatOrganitzativaHelper unitatOrganitzativaHelper;
    private final ExpedientInteressatHelper expedientInteressatHelper;
    private final InteressatResourceHelper interessatResourceHelper;
    private final ExcepcioLogHelper excepcioLogHelper;
    private final ConfigHelper configHelper;
    private final PluginHelper pluginHelper;
    private final CacheHelper cacheHelper;
    private final EventHelper eventHelper;
    private final EntityComprovarHelper entityComprovarHelper;
    private final ExecucioMassivaHelper execucioMassivaHelper;
    private final MessageHelper messageHelper;
    private final MetaExpedientHelper metaExpedientHelper;
    private final EntitatRepository entitatRepository;
    private final InteressatRepository interessatRepository;
    private final InteressatResourceRepository interessatResourceRepository;
    private final InteressatGrupResourceRepository interessatGrupResourceRepository;
    private final SmartValidator validator;

    @PostConstruct
    public void init() {

        register(InteressatResource.PERSPECTIVE_GRUPS_CODE, new GrupsPerspectiveApplicator());
        register(InteressatResource.PERSPECTIVE_REPRESENTANT_CODE, new RespresentantPerspectiveApplicator());
        register(InteressatResource.PERSPECTIVE_ADRESSA_CODE, new AdressaPerspectiveApplicator());
        register(InteressatResource.PERSPECTIVE_PROCEDIMENT_CODE, new ProcedimentPerspectiveApplicator());
        register(InteressatResource.ACTION_EXPORTAR_CODE, new ExportarReportGenerator());
        register(InteressatResource.ACTION_IMPORTAR_CODE, new ImportarInteressatsActionExecutor());
        register(InteressatResource.ACTION_GUARDAR_ARXIU, new GuardarArxiuActionExecutor());
        register(InteressatResource.ACTION_DELETE_INTERESSAT, new DeleteInteressatActionExecutor());
        register(InteressatResource.ACTION_DELETE_REPRESENTANT, new DeleteRepresentantActionExecutor());
        register(InteressatResource.ACTION_GESTIONAR_GRUPS, new GestionarGrupsActionExecutor());

        register(InteressatResource.Fields.tipus, new TipusOnchangeLogicProcessor());
        register(InteressatResource.Fields.organCodi, new UnitatsOrganitzativesOnchangeLogicProcessor());
        register(InteressatResource.Fields.documentNum, new NumDocOnchangeLogicProcessor());

        register(InteressatResource.Fields.municipi, new MunicipiFieldOptionsProvider());
        register(InteressatResource.Fields.provincia, new ProvinciaFieldOptionsProvider());
        register(InteressatResource.Fields.pais, new PaisFieldOptionsProvider());
        register(InteressatResource.Fields.organCodi, new UnitatsOrganitzativesOptionsProvider());

        register(InteressatResource.FILTER_CODE, new FilterOnchangeLogicProcessor());
    }

    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {

    	String entitatActualCodi = configHelper.getEntitatActualCodi();
        String rolActual		 = configHelper.getRolActual();
    	EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
    	
        Filter filtreBase = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(InteressatResource.Fields.expedient + "." +ContingutResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
        );
        
        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
    	if (mapaNamedQueries.size()>0) {
    		
    		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientHelper.findPermesosAccioMassiva(entitat.getId(), rolActual);
    		
    		if (metaExpedientsPermesos==null || metaExpedientsPermesos.size()==0) {
				//Sense permisos
				return FilterBuilder.equal("id", 0).generate();
			}
    		
    		Filter filtreMetaExpedientsPermesos	= null;
    		Filter filtreInteressatNoPropagat	= null;
    		Filter filtreExpedientNoEsborrat	= null;
    		if (mapaNamedQueries.containsKey("MASSIU_PENDENT_ARXIU")) {
				List<Long> metaExpedientsPermesosIds = new ArrayList<Long>();			
    			
				for (MetaExpedientEntity mex: metaExpedientsPermesos) {
					metaExpedientsPermesosIds.add(mex.getId());
				}
				
    			String procedimentId = InteressatResource.Fields.expedient + "." + ExpedientResource.Fields.metaExpedient + ".id";
		    	List<String> permesosClausulesIn = Utils.getIdsEnGruposMil(metaExpedientsPermesosIds);
		        for (String aux: permesosClausulesIn) {
			        if (aux != null && !aux.isEmpty()) {
			        	filtreMetaExpedientsPermesos = FilterBuilder.or(filtreMetaExpedientsPermesos, Filter.parse(procedimentId + " IN (" + aux + ")"));
			        }
		        }
		        
		        String expedientNoEsborratField = InteressatResource.Fields.expedient + "." + ContingutResource.Fields.esborrat;
		        filtreExpedientNoEsborrat = FilterBuilder.equal(expedientNoEsborratField, 0);
		        
		        filtreInteressatNoPropagat = FilterBuilder.equal(InteressatResource.Fields.arxiuPropagat, 0);
    		}
    		
    		Filter resultat = FilterBuilder.and(
	        		filtreBase,
	        		filtreMetaExpedientsPermesos,
	        		filtreInteressatNoPropagat,
	        		filtreExpedientNoEsborrat);
    		
    		return resultat.generate();
    	}
        
        return filtreBase.generate();
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
                    if (nivells != null) {
                        for (NivellAdministracioDto nvl : nivells) {
                            resultat.add(new FieldOption(nvl.getCodi().toString(), nvl.getDescripcio()));
                        }
                    }
                    break;
                case UnitatOrganitzativaFormFilter.Fields.comunitatAutonoma:
                    List<ComunitatDto> comunitats = cacheHelper.findComunitats();
                    if (comunitats != null) {
                        for (ComunitatDto cmnt : comunitats) {
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
                case UnitatOrganitzativaFormFilter.Fields.municipiUo:
                    resultat = new MunicipiFieldOptionsProvider().getOptions(fieldName, requestParameterMap);
                    break;
            }
            return resultat;
        }

        @Override
        public void onChange(Serializable id, UnitatOrganitzativaFormFilter previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, UnitatOrganitzativaFormFilter target) {
        }
    }

    private class TipusOnchangeLogicProcessor implements OnChangeLogicProcessor<InteressatResource> {
        @Override
        public void onChange(Serializable id, InteressatResource previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, InteressatResource target) {
            if (fieldValue != null) {
                switch ((InteressatTipusEnum) fieldValue) {
                    case InteressatPersonaFisicaEntity:
                    case InteressatPersonaJuridicaEntity:
                        target.setDocumentTipus(InteressatDocumentTipusEnumDto.NIF);
                        target.setOrganCodi(null);
//                        target.setPais(previous.getPais());
//	                    target.setProvincia(previous.getProvincia());
//	                    target.setMunicipi(previous.getMunicipi());
                        break;
                    case InteressatAdministracioEntity:
                        target.setAdressaTipus(EntregaPostalTipusEnum.SENSE_NORMALITZAR);
                        target.setDocumentTipus(InteressatDocumentTipusEnumDto.CODI_ORIGEN);
                        target.setDocumentNum(null);
                        target.setCodiPostal(null);
                        target.setAdresa(null);
                        target.setPais(null); //Posam la adreça null ja que es carregarà automaticament al seleccionar una unitat administrativa
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
            if (fieldValue != null) {
                UnitatOrganitzativaDto uoDto = unitatOrganitzativaHelper.findAmbCodiAndAdressafisica(fieldValue.toString());
                target.setNom(Utils.abbreviate(uoDto.getDenominacio(), 30));
                target.setPais(uoDto.getCodiPais());
                target.setProvincia(uoDto.getCodiProvincia());
                target.setMunicipi(uoDto.getLocalitat());
                target.setCodiPostal(uoDto.getCodiPostal());
                target.setAdresa(uoDto.getAdressa());
                target.setDocumentNum(uoDto.getNifCif());
                target.setEmail(null);
                target.setTelefon(null);
                target.setObservacions(null);
            }
        }
    }

    public class PaisFieldOptionsProvider implements FieldOptionsProvider {
        public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
            List<PaisDto> paisos = cacheHelper.findPaisos();
            List<FieldOption> resultat = new ArrayList<FieldOption>();
            if (paisos != null) {
                for (PaisDto pais : paisos) {
                    resultat.add(new FieldOption(pais.getCodi(), pais.getNom()));
                }
            }
            resultat.sort(Comparator.comparing(FieldOption::getDescription));
            return resultat;
        }
    }

    public class UnitatsOrganitzativesOptionsProvider implements FieldOptionsProvider {

        private String getFromMap(String param, Map<String, String[]> requestParameterMap) {
            return (requestParameterMap.containsKey(param) && requestParameterMap.get(param).length > 0)
                    ? requestParameterMap.get(param)[0]
                    : "";
        }

        public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {

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
                    String municipi = getFromMap(UnitatOrganitzativaFormFilter.Fields.municipiUo, mutableMap);
                    Boolean arrel = Boolean.parseBoolean(getFromMap(UnitatOrganitzativaFormFilter.Fields.unitatArrel, mutableMap));
                    uos = pluginHelper.unitatsOrganitzativesFindByFiltre(codiDir3, denominacio, nivellAdm, comunitat, provincia, municipi, arrel);
                }
                if (uos != null) {
                    for (UnitatOrganitzativaDto uo : uos) {
                        resultat.add(new FieldOption(uo.getCodi(), uo.getDenominacio()));
                    }
                }
            }
            resultat.sort(Comparator.comparing(FieldOption::getDescription));
            return resultat;
        }
    }

    public class ProvinciaFieldOptionsProvider implements FieldOptionsProvider {
        public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {

            String[] requestParam = requestParameterMap.get(InteressatResource.Fields.pais);
            String paisCodi = requestParam != null ? requestParam[0] : "";

            List<FieldOption> resultat = new ArrayList<FieldOption>();
            List<ProvinciaDto> provincies = null;

            if (paisCodi != null && paisCodi.equals("724")) {
                provincies = cacheHelper.findProvincies();
            }

            String[] requestParamCA = requestParameterMap.get(InteressatResource.UnitatOrganitzativaFormFilter.Fields.comunitatAutonoma);
            if (requestParamCA != null && requestParamCA.length > 0 && Utils.hasValue(requestParamCA[0])) {
                provincies = cacheHelper.findProvinciesPerComunitat(requestParamCA[0]);
            }

            if (provincies != null) {
                for (ProvinciaDto prov : provincies) {
                    resultat.add(new FieldOption(prov.getCodi(), prov.getNom()));
                }
            }

            resultat.sort(Comparator.comparing(FieldOption::getDescription));
            return resultat;
        }
    }

    public class MunicipiFieldOptionsProvider implements FieldOptionsProvider {
        public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {

            String[] requestParam = requestParameterMap.get(InteressatResource.Fields.provincia);
            String provinciaCodi = requestParam != null ? requestParam[0] : "";

            if (!Utils.hasValue(provinciaCodi)) {
                String[] provinciaFilter = requestParameterMap.get(InteressatResource.UnitatOrganitzativaFormFilter.Fields.provincia);
                provinciaCodi = provinciaFilter != null ? provinciaFilter[0] : "";
            }

            List<FieldOption> resultat = new ArrayList<FieldOption>();
            if (Utils.hasValue(provinciaCodi)) {
                List<MunicipiDto> municipis = cacheHelper.findMunicipisPerProvincia(provinciaCodi);
                if (municipis != null) {
                    for (MunicipiDto municipi : municipis) {
                        if ("municipiUo".equals(fieldName)) {
                            resultat.add(new FieldOption(municipi.getCodi() + "-" + municipi.getCodiEntitatGeografica(), municipi.getNom()));
                        } else {
                            resultat.add(new FieldOption(municipi.getCodi(), municipi.getNom()));
                        }
                    }
                }
            }
            resultat.sort(Comparator.comparing(FieldOption::getDescription));
            return resultat;
        }
    }

    @Override
    protected void afterConversion(InteressatResourceEntity entity, InteressatResource resource) {
        resource.setHasRepresentats(!entity.getRepresentats().isEmpty());
    }

    @Override
    public InteressatResource create(InteressatResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
    	InteressatResource ir = interessatResourceHelper.create(resource);
		afterDbChange(resource.getExpedient().getId());
		return ir;
    }
    
    @Override
    public InteressatResource update(Long id, InteressatResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
    	try {
    		InteressatResource ir = interessatResourceHelper.update(resource);
    		afterDbChange(resource.getExpedient().getId());
    		return ir;
    	} catch (Exception ex) {
    		log.error("Error update InteressatResource", ex);
    		return resource;
    	}
    }

    private void afterDbChange(Long expedientId) {
    	//Esborram cache de validacions del expedient
		cacheHelper.evictErrorsValidacioPerNode(expedientId); // Primero hace evict
		ErrorsValidacioChangedEvent evce = new ErrorsValidacioChangedEvent(
				expedientId,
				cacheHelper.findErrorsValidacioPerNode(expedientId));
		eventHelper.notifyErrorsValidacio(evce); // Luego notifica con datos frescos	
    }

    private class RespresentantPerspectiveApplicator implements PerspectiveApplicator<InteressatResourceEntity, InteressatResource> {
        @Override
        public void applySingle(String code, InteressatResourceEntity entity, InteressatResource resource) throws PerspectiveApplicationException {
            if (entity.getRepresentant() != null) {
                resource.setRepresentantInfo(objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getRepresentant()), InteressatResource.class));
            }
        }
    }

    private class ProcedimentPerspectiveApplicator implements PerspectiveApplicator<InteressatResourceEntity, InteressatResource> {
		@Override
		public void applySingle(String code, InteressatResourceEntity entity, InteressatResource resource) throws PerspectiveApplicationException {
			resource.setMetaExpedient(ResourceReference.toResourceReference(
					entity.getExpedient().getMetaExpedient().getId(),
					entity.getExpedient().getMetaExpedient().getNom()));
		}
    }
    
    private class AdressaPerspectiveApplicator implements PerspectiveApplicator<InteressatResourceEntity, InteressatResource> {

        private void carregaDadesAdressa(InteressatResourceEntity entity, InteressatResource resource) {
            if (Utils.hasValue(resource.getPais())) {
                Pais pais = pluginHelper.dadesExternesPaisFindByCodi(resource.getPais());
                resource.setPaisNom(pais != null ? pais.getNom() : "");
            }
            if (Utils.hasValue(resource.getProvincia())) {
                Provincia prov = pluginHelper.dadesExternesProvinciesFindByCodi(resource.getProvincia());
                resource.setProvinciaNom(prov != null ? prov.getNom() : "");

                if (Utils.hasValue(resource.getMunicipi())) {
                    Municipi muni = pluginHelper.dadesExternesMunicipisFindByCodi(resource.getProvincia(), resource.getMunicipi());
                    resource.setMunicipiNom(muni != null ? muni.getNom() : "");
                }
            }
        }

        @Override
        public void applySingle(String code, InteressatResourceEntity entity, InteressatResource resource) throws PerspectiveApplicationException {
            carregaDadesAdressa(entity, resource);
            if (entity.getRepresentant() != null) {
                carregaDadesAdressa(entity.getRepresentant(), resource);
            }
        }
    }

    private class GrupsPerspectiveApplicator implements PerspectiveApplicator<InteressatResourceEntity, InteressatResource> {

        @Override
        public void applySingle(String code, InteressatResourceEntity entity, InteressatResource resource)
                throws PerspectiveApplicationException {

            // Si el interesado pertenece a grupos, los mapeamos
            if (entity.getGrups() != null && !entity.getGrups().isEmpty()) {
                List<ResourceReference<InteressatGrupResource, Long>> grups = entity.getGrups().stream()
                        .map(grupEntity ->
                                ResourceReference.<InteressatGrupResource, Long>toResourceReference(
                                        grupEntity.getId(),
                                        grupEntity.getNom()
                                )
                        )
                        .collect(Collectors.toList());
                resource.setGrups(grups);
            } else {
                // Si no pertenece a ningún grupo lo dejamos vacío
                resource.setGrups(Collections.emptyList());
            }
        }
    }

    private class NumDocOnchangeLogicProcessor implements OnChangeLogicProcessor<InteressatResource> {

        public static final String NOT_REPRESENT_HIMSELF = "NOT_REPRESENT_HIMSELF";

        @Override
        public void onChange(Serializable id, InteressatResource previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, InteressatResource target) {

            if (fieldValue != null && fieldValue.toString().length() == 9 && !fieldValue.toString().equals(previous.getDocumentNum())) {

                InteressatResourceEntity interessatExistent = interessatResourceRepository.findByExpedientIdAndDocumentNum(previous.getExpedient().getId(), fieldValue.toString()).orElse(null);

                if (interessatExistent != null) {

                    //Controlar que el interessat no es representa a ell mateix
                    if (
                            (previous.getRepresentat() != null && Objects.equals(previous.getRepresentat().getId(), interessatExistent.getId()))
                                    || (previous.getRepresentant() != null && Objects.equals(previous.getRepresentant().getId(), interessatExistent.getId()))
                    ) {
                        if (answers.containsKey(NOT_REPRESENT_HIMSELF)) {
                            target.setDocumentNum(null);
                        } else {
                            throw new AnswerRequiredException(InteressatResource.class, NOT_REPRESENT_HIMSELF, messageHelper.getMessage("es.caib.ripea.service.intf.resourcevalidation.InteressatValid.representHimself"));
                        }
                    } else if (!interessatExistent.getId().equals(previous.getId())) {
                        //Controlar que no estam introduint un interessat repetit
//                    	target.setId(interessatExistent.getId());
                        target.setDocumentTipus(interessatExistent.getDocumentTipus());
                        target.setNom(interessatExistent.getNom());
                        target.setEmail(interessatExistent.getEmail());
                        target.setTelefon(interessatExistent.getTelefon());
                        target.setObservacions(interessatExistent.getObservacions());
                        target.setPreferenciaIdioma(interessatExistent.getPreferenciaIdioma());
                        target.setEntregaDeh(interessatExistent.getEntregaDeh());
                        target.setEntregaDehObligat(interessatExistent.getEntregaDehObligat());

                        target.setTipus(interessatExistent.getTipus());
                        switch (interessatExistent.getTipus()) {
                            case InteressatAdministracioEntity:
                                target.setOrganCodi(interessatExistent.getOrganCodi());
                                target.setOrganNom(interessatExistent.getOrganNom());
                                break;
                            case InteressatPersonaFisicaEntity:
                                target.setLlinatge1(interessatExistent.getLlinatge1());
                                target.setLlinatge2(interessatExistent.getLlinatge2());
                                break;
                            case InteressatPersonaJuridicaEntity:
                                target.setRaoSocial(interessatExistent.getRaoSocial());
                                break;
                        }

                        // Adreça
                        target.setPais(interessatExistent.getPais());
                        target.setProvincia(interessatExistent.getProvincia());
                        target.setMunicipi(interessatExistent.getMunicipi());
                        target.setCodiPostal(interessatExistent.getCodiPostal());
                        target.setAdresa(interessatExistent.getAdresa());
                        target.setAdressaTipus(interessatExistent.getAdressaTipus());
                        target.setAdressaTipusVia(interessatExistent.getAdressaTipusVia());

                        target.setAdressaNumCasa(interessatExistent.getAdressaNumCasa());
                        target.setAdresaQualificador(interessatExistent.getAdresaQualificador());
                        target.setAdresaPuntKm(interessatExistent.getAdresaPuntKm());
                        target.setAdresaApartatCorreus(interessatExistent.getAdresaApartatCorreus());
                        target.setAdresaPortal(interessatExistent.getAdresaPortal());
                        target.setAdresaEscala(interessatExistent.getAdresaEscala());
                        target.setAdresaPlanta(interessatExistent.getAdresaPlanta());
                        target.setAdresaPorta(interessatExistent.getAdresaPorta());
                        target.setAdresaBloc(interessatExistent.getAdresaBloc());
                        target.setAdresaComplement(interessatExistent.getAdresaComplement());
                        target.setAdresaPoblacio(interessatExistent.getAdresaPoblacio());
                    }
                }
            }
        }
    }

    private class ExportarReportGenerator implements ReportGenerator<InteressatResourceEntity, InteressatResource.ExportInteressatsFormAction, Serializable> {

        @Override
        public void onChange(Serializable id, InteressatResource.ExportInteressatsFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, InteressatResource.ExportInteressatsFormAction target) {
        }

        @Override
        public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Long expedientId = (Long) data.get(0);
            MassiveAction params = (MassiveAction) data.get(1);
            try {

                Set<Long> grupIdsSeleccionados = params.getIds().stream()
                        .filter(id -> interessatGrupResourceRepository.existsById(id))
                        .collect(Collectors.toSet());

                List<Object> exportList = new ArrayList<>();
                Set<Long> interessatIds = new HashSet<>();
                // Exportamos todos los grupos seleccionados con sus interesados
                for (Long grupId : grupIdsSeleccionados) {
                    InteressatGrupResourceEntity interessatGrupResourceEntity = interessatGrupResourceRepository.findById(grupId).orElseThrow();
                    InteressatGrupResource interessatGrupResource = objectMappingHelper.newInstanceMap(interessatGrupResourceEntity, InteressatGrupResource.class);

                    List<InteressatResource> interessats = interessatGrupResourceEntity.getInteressats().stream()
                            .map(interessatEntity -> {
                                InteressatResource interessatResource = objectMappingHelper.newInstanceMap(interessatEntity, InteressatResource.class);
                                new RespresentantPerspectiveApplicator().applySingle(InteressatResource.PERSPECTIVE_REPRESENTANT_CODE, interessatEntity, interessatResource);
                                new GrupsPerspectiveApplicator().applySingle(InteressatResource.PERSPECTIVE_GRUPS_CODE, interessatEntity, interessatResource);

                                return interessatResource;
                            })
                            .collect(Collectors.toList());

//            	    interessatGrupResource.setInteressatsDetallats(interessats);

                    for (InteressatResource interessat : interessats) {
                        if (interessat.getId() != null && interessatIds.add(interessat.getId()))
                            exportList.add(interessat);
                    }
                }

                // Ahora exportamos los interesados individuales, excluyendo los que ya están en grupos seleccionados
                interessatResourceRepository.findAllById(params.getIds()).forEach(interessatEntity -> {
                    boolean perteneceAGrupoSeleccionado = interessatEntity.getGrups().stream()
                            .anyMatch(g -> grupIdsSeleccionados.contains(g.getId()));

                    if (!perteneceAGrupoSeleccionado) {
                        InteressatResource interessatResource = objectMappingHelper.newInstanceMap(interessatEntity, InteressatResource.class);
                        new RespresentantPerspectiveApplicator().applySingle(InteressatResource.PERSPECTIVE_REPRESENTANT_CODE, interessatEntity, interessatResource);
                        new GrupsPerspectiveApplicator().applySingle(InteressatResource.PERSPECTIVE_GRUPS_CODE, interessatEntity, interessatResource);

                        exportList.add(interessatResource);
                    }
                });

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(baos, exportList);
                return new DownloadableFile("Interessats_expedient_" + expedientId + ".json", "application/json", baos.toByteArray());

//                ObjectMapper objectMapper = new ObjectMapper();
//                objectMapper.registerModule(new JavaTimeModule());
//                objectMapper.writerWithDefaultPrettyPrinter().writeValue(baos,
//                        interessatResourceRepository.findAllById(params.getIds()).stream()
//                                .map(interessatEntity -> {
//                                    InteressatResource interessatResource = objectMappingHelper.newInstanceMap(interessatEntity, InteressatResource.class);
//                                    new RespresentantPerspectiveApplicator().applySingle(InteressatResource.PERSPECTIVE_REPRESENTANT_CODE, interessatEntity, interessatResource);
//                                    return interessatResource;
//                                }).collect(Collectors.toList()));
//                return new DownloadableFile("Interessats_expedient_"+expedientId+".json", "application/json", baos.toByteArray());
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

    private class GestionarGrupsActionExecutor implements ActionExecutor<InteressatResourceEntity, InteressatResource.GestionarGrupsFrom, Serializable> {

        @Override
        public Serializable exec(String code, InteressatResourceEntity entity, InteressatResource.GestionarGrupsFrom params) throws ActionExecutionException {
            List<Long> ids = params.getGrups().stream().map(ResourceReference::getId).collect(Collectors.toList());
            List<InteressatGrupResourceEntity> grups = interessatGrupResourceRepository.findAllById(ids);
            entity.setGrups(grups);
            return objectMappingHelper.newInstanceMap(entity, InteressatResource.class);
        }

        @Override
        public void onChange(Serializable id, InteressatResource.GestionarGrupsFrom previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, InteressatResource.GestionarGrupsFrom target) {
        }
    }

    private class DeleteRepresentantActionExecutor implements ActionExecutor<InteressatResourceEntity, Serializable, Serializable> {

        @Override
        public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {
        }

        @Override
        public Serializable exec(String code, InteressatResourceEntity entity, Serializable params) throws ActionExecutionException {
            try {
                EntitatEntity entitat = entitatRepository.findByCodi(configHelper.getEntitatActualCodi());
                expedientInteressatHelper.deleteRepresentant(
                        entitat.getId(),
                        entity.getExpedient().getId(),
                        entity.getId(),
                        entity.getRepresentant().getId(),
                        configHelper.getRolActual());
            } catch (Exception e) {
                excepcioLogHelper.addExcepcio("/expedient/interessats/" + entity.getId() + "/DeleteRepresentantArxiuActionExecutor", e);
                String message = messageHelper.getMessage("message.common.action.error") + ": " + e.getMessage();
                throw new ActionExecutionException(getResourceClass(), entity.getRepresentant().getId(), code, message);
            }
            //Dada per mostrar al missatge de OK, retornar el entity convertit a InteressatResource dona error perque no el troba
            return "{ \"documentNum\": \"" + entity.getRepresentant().getDocumentNum() + "\" }";
        }
    }

    private class DeleteInteressatActionExecutor implements ActionExecutor<InteressatResourceEntity, Serializable, Serializable> {

        @Override
        public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {
        }

        @Override
        public Serializable exec(String code, InteressatResourceEntity entity, Serializable params) throws ActionExecutionException {
            try {
                EntitatEntity entitat = entitatRepository.findByCodi(configHelper.getEntitatActualCodi());
                expedientInteressatHelper.delete(
                        entitat.getId(),
                        entity.getExpedient().getId(),
                        entity.getId(),
                        configHelper.getRolActual());
                afterDbChange(entity.getExpedient().getId());
            } catch (Exception e) {
                excepcioLogHelper.addExcepcio("/expedient/interessats/" + entity.getId() + "/DeleteInteressatActionExecutor", e);
                String message = messageHelper.getMessage("message.common.action.error") + ": " + e.getMessage();
                throw new ActionExecutionException(getResourceClass(), entity.getId(), code, message);
            }
            //Dada per mostrar al missatge de OK, retornar el entity convertit a InteressatResource dona error perque no el troba
            return "{ \"documentNum\": \"" + entity.getDocumentNum() + "\" }";
        }
    }

    private class GuardarArxiuActionExecutor implements ActionExecutor<InteressatResourceEntity, MassiveAction, Serializable> {

        @Override
        public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}

        @Override
        public Serializable exec(String code, InteressatResourceEntity entity, MassiveAction params) throws ActionExecutionException {
        	
        	try {
        		
				if (params.isMassivo()) {
					String entitatActual = configHelper.getEntitatActualCodi();
					EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatActual, false, false, false, true, false);
					List<ExecucioMassivaContingutDto> elementsMassiva = execucioMassivaHelper.getMassivaContingutFromIds(params.getIds());
					ExecucioMassivaDto execMassDto = new ExecucioMassivaDto(
							ExecucioMassivaTipusDto.CUSTODIAR_ELEMENTS_PENDENTS,
							new Date(),
							null,
							configHelper.getRolActual());
					execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, elementsMassiva, ElementTipusEnumDto.INTERESSAT);	        		
	        		
	        	} else {
	        		
	        		entity = interessatResourceRepository.findById(params.getIds().get(0)).get();
	                Exception errorGuardant = expedientInteressatHelper.guardarInteressatsArxiu(entity.getExpedient().getId());
	                if (errorGuardant != null) {
	                	excepcioLogHelper.addExcepcio(
	                			"/interessat/GuardarArxiuActionExecutor",
	                			errorGuardant,
	                			Utils.getIdsSeparatsComa(params.getIds()),
	                			"massiu="+params.isMassivo());
	                    throw new ActionExecutionException(getResourceClass(), entity.getId(), code, errorGuardant.getMessage());
	                }
	        	}
            } catch (Exception e) {
            	excepcioLogHelper.addExcepcio(
            			"/interessat/GuardarArxiuActionExecutor",
            			e,
            			Utils.getIdsSeparatsComa(params.getIds()),
            			"massiu="+params.isMassivo());
                String message = messageHelper.getMessage("message.common.action.error") + ": " + e.getMessage();
                throw new ActionExecutionException(getResourceClass(), entity.getId(), code, message);
            }
            return objectMappingHelper.newInstanceMap(entity, InteressatResource.class);
        }
    }

    private class ImportarInteressatsActionExecutor implements ActionExecutor<InteressatResourceEntity, InteressatResource.ImportarInteressatsFormAction, Serializable> {

        private Map<String, String> validate(InteressatResource interessatResource) {
            DataBinder dataBinder = new DataBinder(interessatResource);
            BindingResult bindingResult = dataBinder.getBindingResult();

            validator.validate(
                    interessatResource,
                    bindingResult,
                    0,
                    Resource.OnCreate.class,
                    Default.class);

            return bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        }

        private static final String NOT_COMPATIBLE = "NOT_COMPATIBLE";

        @Override
        public void onChange(Serializable id, InteressatResource.ImportarInteressatsFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, InteressatResource.ImportarInteressatsFormAction target) {

            if (InteressatResource.ImportarInteressatsFormAction.Fields.fitxerJsonInteressats.equals(fieldName)) {
                List<InteressatResource> listaInteressatsFitxer = new ArrayList<InteressatResource>();
                if (fieldValue != null) {
                    if (!answers.containsKey(NOT_COMPATIBLE)) {
                        try {
                            if (previous.getTipusImportacio().equals(InteressatImportacioTipusDto.JSON)) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                objectMapper.registerModule(new JavaTimeModule());
                                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                                List<InteressatResource> interesatsIndependents = objectMapper.readValue(
                                        ((FileReference) fieldValue).getContent(),
                                        new TypeReference<List<InteressatResource>>() {
                                        }
                                );

                                List<InteressatGrupResource> grupsFitxer = objectMapper.readValue(
                                        ((FileReference) fieldValue).getContent(),
                                        new TypeReference<List<InteressatGrupResource>>() {
                                        }
                                );

                                List<InteressatResource> interesatsDeGrups = grupsFitxer.stream()
                                        .filter(g -> g.getInteressatsDetallats() != null)
                                        .flatMap(g -> g.getInteressatsDetallats().stream())
                                        .collect(Collectors.toList());

                                listaInteressatsFitxer.addAll(interesatsIndependents);
                                listaInteressatsFitxer.addAll(interesatsDeGrups);

                            } else {
                                InputStream excelStream = new ByteArrayInputStream(((FileReference) fieldValue).getContent());
                                listaInteressatsFitxer = interessatResourceHelper.extreureInteressatsExcel(excelStream);
                            }
                        } catch (Exception e) {
                            excepcioLogHelper.addExcepcio("/expedient/interessats/ImportarInteressatsActionExecutor/" + previous.getTipusImportacio() + ".onChange", e);
                            String message = messageHelper.getMessage("interessat.import.reject") + ": " + e.getMessage();
                            throw new AnswerRequiredException(
                            		InteressatResource.ImportarInteressatsFormAction.class, 
                            		NOT_COMPATIBLE,
                            		message);
                        }
                    }

                    //Abans de retornar la llista de interessats, comprovam si existeixen al expedient actual
                    //En cas de no tenir-lo, afegim ID: Uncaught Error: MUI X: The Data Grid component requires all rows to have a unique `id` property.
                    if (!listaInteressatsFitxer.isEmpty()) {
                        //Només fem la consulta en cas necessari
                        List<InteressatEntity> interessatsExpActual = interessatRepository.findByExpedientId(previous.getExpedient().getId());
                        long idAssignat = 1L;
                        for (InteressatResource interessatResource : listaInteressatsFitxer) {
                            if (interessatsExpActual != null) {
                                for (InteressatEntity interessatExp : interessatsExpActual) {
                                    if (interessatExp.getDocumentNum().equalsIgnoreCase(interessatResource.getDocumentNum())) {
                                        interessatResource.setJaExistentExpedient(true);
                                        break;
                                    }
                                }
                            }
                            if (interessatResource.getId() == null) {
                                interessatResource.setId(idAssignat++);
                            }
                            interessatResource.setExpedient(previous.getExpedient());
                            interessatResource.setErrors(validate(interessatResource));
                        }
                    }
                }
                target.setInteressatsFitxer(listaInteressatsFitxer);
            }
        }

        @Override
        public Serializable exec(String code, InteressatResourceEntity entity, InteressatResource.ImportarInteressatsFormAction params) throws ActionExecutionException {
            try {
                String mssg = interessatResourceHelper.importarInteressats(
                        params.getExpedient().getId(),
                        params.getInteressatsPerImportar().stream()
                                .filter(i -> validate(i).isEmpty())
                                .collect(Collectors.toList())
                );
                return "{ \"mssg\": \"" + mssg + "\" }";
            } catch (Exception e) {
                excepcioLogHelper.addExcepcio("/expedient/" + params.getExpedient().getId() + "/ImportarInteressatsActionExecutor", e);
                String message = messageHelper.getMessage("message.common.action.error") + ": " + e.getMessage();
                throw new ActionExecutionException(getResourceClass(), params.getExpedient().getId(), code, message);
            }
        }
    }

}