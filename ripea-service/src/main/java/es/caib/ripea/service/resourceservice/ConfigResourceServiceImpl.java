package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.config.ConfigEntity;
import es.caib.ripea.persistence.entity.resourceentity.config.ConfigResourceEntity;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.persistence.repository.config.ConfigRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.model.ConfigResource;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.resourceservice.ConfigResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigResourceServiceImpl extends BaseMutableResourceService<ConfigResource, String, ConfigResourceEntity> implements ConfigResourceService {

	private final ConfigRepository configRepository;
	private final EntitatRepository entitatRepository;
	private final OrganGestorRepository organGestorRepository;
	private final ConfigHelper configHelper;
	private final ExcepcioLogHelper excepcioLogHelper;
	private final MessageHelper messageHelper;
	
    @PostConstruct
    public void init() {
    	register(ConfigResource.ACTION_SYNC_JBOSS, new SyncJbossActionExecutor());
    	
    	register(ConfigResource.Fields.entitat, new ConfigOnchangeLogicProcessor());
    	register(ConfigResource.Fields.organ, new ConfigOnchangeLogicProcessor());
    }
	
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
    	Filter filtreFront = (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null;
    	Filter filtreEspecifiques = null;
    	
        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
    	if (mapaNamedQueries.size()>0) {
    		if (mapaNamedQueries.containsKey("QUERY_ESPECIFIQUES")) {
    			String codiKeyBase = mapaNamedQueries.get("QUERY_ESPECIFIQUES");
    			String[] aux = codiKeyBase.split("es.caib.ripea.");
    			Filter filtrePrefixe = Filter.parse(ConfigResource.Fields.key + "~'es.caib.ripea.%'");
    			Filter filtreSufixe = Filter.parse(ConfigResource.Fields.key + "~'%"+aux[1]+"'");
    			Filter filtreEntitatNotNull = Filter.parse(ConfigResource.Fields.entitatCodi + " IS NOT NULL");
    			Filter filtreOrganNotNull = Filter.parse(ConfigResource.Fields.organCodi + " IS NOT NULL");
    			
    			Filter filtreAux = FilterBuilder.or(filtreEntitatNotNull, filtreOrganNotNull);
    			
    			filtreEspecifiques = FilterBuilder.and(filtrePrefixe, filtreSufixe, filtreAux);
    		}
    	}
    	
    	Filter filtreResultat = FilterBuilder.and(filtreFront, filtreEspecifiques);
    	
    	return filtreResultat!=null?filtreResultat.generate():null;
    }
    
    private class ConfigOnchangeLogicProcessor implements OnChangeLogicProcessor<ConfigResource> {
		@Override
		public void onChange(Serializable id, ConfigResource previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, ConfigResource target) {
			
			//Primero reseteamos la propiedad key a su valor base, quitando los valores de entidad y organo que pudiera tener
			String keyBase = getKeyBase(previous.getKey(), previous.getEntitatCodi(), previous.getOrganCodi());
			String[] aux = keyBase.split("es.caib.ripea.");
			
			//Han cambiado la entidad, puede haber organo gestor seleccionado o no, en todo caso hay que resetear el órgano gestor
			if (ConfigResource.Fields.entitat.equals(fieldName)) {
				if (fieldValue!=null) {
					EntitatEntity ee = entitatRepository.findById(((ResourceReference<EntitatResource, Long>)fieldValue).getId()).get();
					target.setKey("es.caib.ripea."+ee.getCodi()+"."+aux[1]);
                    target.setEntitatCodi(ee.getCodi());
				} else {
                    target.setEntitatCodi(null);
                }
				//Reseteamos el organo gestor
				target.setOrgan(null);
			} else if (ConfigResource.Fields.organ.equals(fieldName)) {
				if (fieldValue!=null) {
					OrganGestorEntity oge = organGestorRepository.findById(((ResourceReference<EntitatResource, Long>)fieldValue).getId()).get();
					target.setKey("es.caib.ripea."+oge.getEntitat().getCodi()+"."+oge.getCodi()+"."+aux[1]);
                    target.setOrganCodi(oge.getCodi());
                } else {
                    target.setOrganCodi(null);
                }
			}			
		}
    }
    
    private String getKeyBase(String keyActual, String entitatCodi, String organCodi) {
		String keyBase = keyActual+"";
		if (Utils.hasValue(entitatCodi)) {
			keyBase = keyBase.replace("."+entitatCodi+".", ".");
		}
		if (Utils.hasValue(organCodi)) {
			keyBase = keyBase.replace("."+organCodi+".", ".");
		}
		return keyBase;
    }
    
    @Override
    protected void afterConversion(ConfigResourceEntity entity, ConfigResource resource) {
    	if (Utils.hasValue(entity.getEntitatCodi())) {
    		EntitatEntity ee = entitatRepository.findByCodi(entity.getEntitatCodi());
    		resource.setEntitat(ResourceReference.toResourceReference(ee.getId(), ee.getNom()));
    	
	    	if (Utils.hasValue(entity.getOrganCodi())) {
	    		OrganGestorEntity oge = organGestorRepository.findByEntitatCodiAndCodi(entity.getEntitatCodi(), entity.getOrganCodi());
	    		resource.setEntitat(ResourceReference.toResourceReference(oge.getId(), oge.getNom()));
	    	}
    	}
    }
    
    @Override
    public ConfigResource create(ConfigResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
    	//El metodo create solo sirve para añadir propiedades especificas por entidad u organo gestor.
    	//No para crear propiedades base nuevas.
    	ConfigEntity conf = configRepository.findById(resource.getKey()).orElse(null);
    	if (conf==null) {
    		conf = new ConfigEntity();
    		conf.setKey(resource.getKey());
    	}
    	
    	conf.setConfigurable(true);
    	conf.setValue(resource.getValue());
    	conf.setDescription(resource.getDescription());
    	
    	if (resource.getEntitat()!=null) {
    		conf.setEntitatCodi(resource.getEntitatCodi());
    	}
    	
    	if (resource.getOrgan()!=null) {
    		conf.setOrganCodi(resource.getOrganCodi());
    		conf.setConfigurableOrgan(true);
    		conf.setConfigurableOrgansDescendents(true);
    	} else {
    		conf.setConfigurableOrgan(false);
    		conf.setConfigurableOrgansDescendents(false);
    	}
    	
    	String keyBase = getKeyBase(resource.getKey(), resource.getEntitatCodi(), resource.getOrganCodi());
    	ConfigEntity confBase = configRepository.findById(keyBase).orElse(null);
    	
    	conf.setType(confBase.getType());
    	conf.setGroupCode(confBase.getGroupCode());
    	
    	configRepository.save(conf);
    	
    	return resource;
    }

    private class SyncJbossActionExecutor implements ActionExecutor<ConfigResourceEntity, Serializable, Serializable> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

		@Override
		public Serializable exec(String code, ConfigResourceEntity entity, Serializable params) throws ActionExecutionException {
			try {
				List<String> propietatsActualitzades = configHelper.syncFromJBossProperties();
				return propietatsActualitzades!=null?propietatsActualitzades.size():0;
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/config/"+entity.getId()+"/SyncJbossActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, messageHelper.getMessage("message.common.action.error")+": "+e.getMessage());
			}
		}    	
    }
}