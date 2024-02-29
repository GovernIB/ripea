package es.caib.ripea.core.service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;

import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.config.ConfigDto;
import es.caib.ripea.core.api.dto.config.ConfigGroupDto;
import es.caib.ripea.core.api.dto.config.OrganConfigDto;
import es.caib.ripea.core.api.exception.NotDefinedConfigException;
import es.caib.ripea.core.api.service.ConfigService;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.entity.config.ConfigEntity;
import es.caib.ripea.core.entity.config.ConfigGroupEntity;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ConfigHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.repository.config.ConfigGroupRepository;
import es.caib.ripea.core.repository.config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Classe que implementa els metodes per consultar i editar les configuracions de l'aplicació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ConfigGroupRepository configGroupRepository;
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;
    @Autowired
    private EntitatRepository entitatRepository;
    @Autowired
    private PluginHelper pluginHelper;
    @Autowired
    private ConfigHelper configHelper;
    @Autowired
    private OrganGestorRepository organGestorRepository;
    @Autowired
    private PaginacioHelper paginacioHelper;
    @Autowired
    private CacheHelper cacheHelper;

    @Override
    @Transactional
    public ConfigDto updateProperty(ConfigDto property) {
        log.info(String.format("Actualització valor propietat %s a %s ", property.getKey(), property.getValue()));
        ConfigEntity configEntity = configRepository.findOne(property.getKey());
        configEntity.update(!"null".equals(property.getValue()) ? property.getValue() : null);
//        pluginHelper.reloadProperties(configEntity.getGroupCode());
        pluginHelper.resetPlugins();
       // cacheHelper.clearAllCaches();
        
        cacheHelper.evictMostrarLogsEmail();
        cacheHelper.evictMostrarLogsPermisos();
        cacheHelper.evictMostrarLogsGrups();
        cacheHelper.evictMostrarLogsRendimentDescarregarAnotacio();
        cacheHelper.evictMostrarLogsCercadorAnotacio();
        cacheHelper.evictMostrarLogsRendiment();
        cacheHelper.evictMostrarLogsCreacioContingut();
        cacheHelper.evictMostrarLogsIntegracio();
        cacheHelper.evictMostrarLogsSegonPla();

        return conversioTipusHelper.convertir(configEntity, ConfigDto.class);
    }
    
    
    @Override
    @Transactional
    public void createPropertyOrgan(OrganConfigDto property) {
        log.info(String.format("Creant propietat organ: %s, key:  %s, value: %s ", property.getOrganGestorId(), property.getKey(), property.getValue()));
        
        String suffix = property.getKey().replace(ConfigDto.prefix, "");
        
        OrganGestorEntity organGestor = organGestorRepository.findOne(property.getOrganGestorId());
		String keyOrgan = ConfigDto.prefix + "." + organGestor.getEntitat().getCodi() + "." + organGestor.getCodi() + suffix;
        
        ConfigEntity configEntity = configRepository.findOne(property.getKey());
        
        if (configRepository.findOne(keyOrgan) == null ) {
            ConfigEntity nova = new ConfigEntity();
            nova.crearConfigNova(keyOrgan, organGestor.getEntitat().getCodi(), organGestor.getCodi(), configEntity);
            nova.setValue(property.getValue());
            configRepository.save(nova);
        } else {
        	throw new RuntimeException("La configuració per aquest òrgan ja esta creat");
        }
        
        
//        pluginHelper.reloadProperties(configEntity.getGroupCode());
        pluginHelper.resetPlugins();
    }
    
    @Override
    @Transactional
    public void modificarPropertyOrgan(OrganConfigDto property) {
        log.info(String.format("Modificant propietat organ: %s, key:  %s, value: %s ", property.getOrganGestorId(), property.getKey(), property.getValue()));
        
        ConfigEntity confOrgan = configRepository.findOne(property.getKey());
        if (confOrgan != null) {
        	confOrgan.setValue(property.getValue());
        }
        
//        pluginHelper.reloadProperties(confOrgan.getGroupCode());
        pluginHelper.resetPlugins();
    }
    
    @Override
    @Transactional
    public void deletePropertyOrgan(String key) {
        log.info(String.format("Esborrant propietat organ:  %s, ", key));
        
        ConfigEntity confOrgan = configRepository.findOne(key);
        if (confOrgan != null) {
        	configRepository.delete(confOrgan);
        }
        
//        pluginHelper.reloadProperties(confOrgan.getGroupCode());
        pluginHelper.resetPlugins();
    }
    
    

    @Override
    @Transactional(readOnly = true)
    public List<ConfigGroupDto> findAll() {

        log.info("Consulta totes les propietats");
        List<ConfigGroupEntity> groups = configGroupRepository.findByParentCodeIsNull(new Sort(Sort.Direction.ASC, "position"));
        List<ConfigGroupDto> configGroupDtoList =  conversioTipusHelper.convertirList(groups, ConfigGroupDto.class);
        for (ConfigGroupDto cGroup: configGroupDtoList) {
            processPropertyValues(cGroup);
        }
        return configGroupDtoList;
    }
    
    @Override
    @Transactional(readOnly = true)
    public ConfigDto findConfig(String key) {

    	ConfigEntity config = configRepository.findOne(key);
    	return conversioTipusHelper.convertir(config, ConfigDto.class);

    }
    
    
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<OrganConfigDto> findConfigsOrgans(
			String key,
			PaginacioParamsDto paginacioParams) {


			Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
			ordenacioMap.put("organGestorCodiNom", new String[] {"organCodi"});

			String suffix = key.replace(ConfigDto.prefix, "");
			
			return paginacioHelper.toPaginaDto(
					configRepository.findConfOrgansByKey(
							ConfigDto.prefix,
							suffix,
							paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap)),
					OrganConfigDto.class);
		
		
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public OrganConfigDto findConfigOrgan(
			String key) {

		return conversioTipusHelper.convertir(
				configRepository.findByKey(key),
				OrganConfigDto.class);
		
	}
	

    @Override
    @Transactional
    public List<String> syncFromJBossProperties() {
        log.info("Sincronitzant les propietats amb JBoss");
        Properties properties = ConfigHelper.JBossPropertiesHelper.getProperties().findAll();
        List<String> editedProperties = new ArrayList<>();
        List<String> propertiesList = new ArrayList<>(properties.stringPropertyNames());
        Collections.sort(propertiesList);
        for (String key : propertiesList) {
            String value = properties.getProperty(key);
            log.info(key + " : " + value);
            ConfigEntity configEntity = configRepository.findOne(key);
            if (configEntity != null) {
                configEntity.update(value);
//                pluginHelper.reloadProperties(configEntity.getGroupCode());
                if (configEntity.getKey().endsWith(".class")){
                    pluginHelper.resetPlugins();
                }
                editedProperties.add(configEntity.getKey());
            }
        }
        return editedProperties;
    }

    
    @Override
    @Transactional
    public void configurableEntitat(String key, boolean configurable) {
        log.info(String.format("Actualització valor configurable entitat de propietat %s a %s ", key, configurable));
        ConfigEntity configEntity = configRepository.findOne(key);
        configEntity.updateConfigurableEntitat(configurable);
        
        List<EntitatEntity> entitats = entitatRepository.findAll();
        if (configurable) {
        	configHelper.crearConfigPerEntitats(configEntity, entitats);
		} else {
			configHelper.removeConfigPerEntitats(configEntity, entitats);
		}
        
        pluginHelper.resetPlugins();

    }
    
    
    @Override
    @Transactional
    public void configurableOrgan(String key, boolean configurable) {
        log.info(String.format("Actualització valor configurable organ de propietat %s a %s ", key, configurable));
        ConfigEntity configEntity = configRepository.findOne(key);
        configEntity.updateConfigurableOrgan(configurable);
        
        String suffix = key.replace(ConfigDto.prefix, "");
        
		if (!configurable) {
			List<ConfigEntity> confs = configRepository.findConfOrgansByKey(
					ConfigDto.prefix,
					suffix);
			for (ConfigEntity config : confs) {
				configRepository.delete(config);
			}
		}
        
        pluginHelper.resetPlugins();
    }

    

    
    

    
    
    @Override
    @Transactional
    public List<ConfigDto> findEntitatsConfigByKey(String key) {

        if (Strings.isNullOrEmpty(key)) {
            return new ArrayList<>();
        }
        String suffix = key.replace(ConfigDto.prefix, "");
        return conversioTipusHelper.convertirList(configRepository.findLikeKeyEntitatNotNullAndConfigurable(ConfigDto.prefix, suffix), ConfigDto.class);
    }
    
    
    
    @Override
    @Transactional(readOnly = true)
    public String getConfigValue(String configKey) throws NotDefinedConfigException {
    	return configHelper.getConfig(configKey);
    }

    public void processPropertyValues(ConfigGroupDto cGroup) {
        for (ConfigDto config: cGroup.getConfigs()) {
            if ("PASSWORD".equals(config.getTypeCode())){
                config.setValue("*****");
            } else if (config.isJbossProperty()) {
                // Les propietats de Jboss es llegeixen del fitxer de properties i si no estan definides prenen el valor especificat a la base de dades.
                config.setValue(ConfigHelper.JBossPropertiesHelper.getProperties().getProperty(config.getKey(), config.getValue()));
            }
        }

        if (cGroup.getInnerConfigs() != null && !cGroup.getInnerConfigs().isEmpty()) {
            for (ConfigGroupDto child : cGroup.getInnerConfigs()) {
                processPropertyValues(child);
            }
        }
    }


    
    @Override
    @Transactional
    public void crearPropietatsConfigPerEntitats() {

        List<ConfigEntity> configs = configRepository.findByEntitatCodiIsNullAndConfigurableIsTrue();
        List<EntitatEntity> entitats = entitatRepository.findAll();
        for (ConfigEntity config : configs) {
        	configHelper.crearConfigPerEntitats(config, entitats);
        }
    }

    @Override
    @Transactional
    public void actualitzarPropietatsJBossBdd() {

        List<ConfigEntity> configs = configRepository.findJBossConfigurables();
        for(ConfigEntity config : configs) {
            String property = ConfigHelper.JBossPropertiesHelper.getProperties().getProperty(config.getKey());
            config.setValue(property);
            configRepository.save(config);
        }
    }

}
