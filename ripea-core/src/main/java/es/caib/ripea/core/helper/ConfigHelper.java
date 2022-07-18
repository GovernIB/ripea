package es.caib.ripea.core.helper;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.config.ConfigDto;
import es.caib.ripea.core.api.exception.NotDefinedConfigException;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.config.ConfigEntity;
import es.caib.ripea.core.entity.config.ConfigGroupEntity;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.config.ConfigGroupRepository;
import es.caib.ripea.core.repository.config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ConfigHelper {

    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private ConfigGroupRepository configGroupRepository;
    
    private static ThreadLocal<EntitatDto> entitat = new ThreadLocal<>();    
    
    public static ThreadLocal<EntitatDto> getEntitat() {
		return entitat;
	}

	public static void setEntitat(EntitatDto entitat) {
		ConfigHelper.entitat.set(entitat);
	}

    @Transactional(readOnly = true)
    public String getConfig(String keyGeneral, String valorDefecte) {

        String valor = getConfig(keyGeneral);
        return !Strings.isNullOrEmpty(valor) ? valor : valorDefecte;
    }

    @Transactional(readOnly = true)
    public String getConfig(String keyGeneral)  {
    	
    	String entitatCodi  = getEntitatActualCodi();
		String value = null;
		ConfigEntity configEntity = configRepository.findOne(keyGeneral);
		if (configEntity == null) {
            return getJBossProperty(keyGeneral);
        }
        // Propietat trobada en db
        if (configEntity.isConfigurable() && !Strings.isNullOrEmpty(entitatCodi)) {
            // Propietat a nivell d'entitat
            String keyEntitat = crearEntitatKey(entitatCodi, keyGeneral);
            ConfigEntity configEntitatEntity = configRepository.findOne(keyEntitat);
            if (configEntitatEntity != null) {
                value = getConfig(configEntitatEntity);
            }
        }
        if (value == null) {
            // Propietat global
            value = getConfig(configEntity);
        }
		return value;
	}

    @Transactional(readOnly = true)
    public Properties getGroupProperties(String codeGroup) {
        Properties properties = new Properties();
        ConfigGroupEntity configGroup = configGroupRepository.findOne(codeGroup);
        fillGroupProperties(configGroup, properties);
        return properties;
    }

    private void fillGroupProperties(ConfigGroupEntity configGroup, Properties outProperties) {
        if (configGroup == null) {
            return;
        }
        for (ConfigEntity config : configGroup.getConfigs()) {
			String conf = getConfigGeneralIfConfigEntitatNull(config);
			if (conf != null) {
				outProperties.put(config.getKey(), conf);
			} else {
				log.debug("Propietat: " + config.getKey() + " es null");
			}
        }

        if (configGroup.getInnerConfigs() != null) {
            for (ConfigGroupEntity child : configGroup.getInnerConfigs()) {
                fillGroupProperties(child, outProperties);
            }
        }
    }

    public boolean getAsBoolean(String key) {
        return Boolean.parseBoolean(getConfig(key));
    }
    public int getAsInt(String key) {
        return new Integer(getConfig(key));
    }
    public long getAsLong(String key) {
        return new Long(getConfig(key));
    }
    public float getAsFloat(String key) {
        return new Float(getConfig(key));
    }

    public String getJBossProperty(String key) {
        return JBossPropertiesHelper.getProperties().getProperty(key);
    }
    public String getJBossProperty(String key, String defaultValue) {
        return JBossPropertiesHelper.getProperties().getProperty(key, defaultValue);
    }

    @Transactional(readOnly = true)
    public Properties getAllEntityProperties(String entitatCodi) {

        Properties properties = new Properties();
//        List<ConfigEntity> configs = !Strings.isNullOrEmpty(entitatCodi) ? configRepository.findConfigEntitaCodiAndGlobals(entitatCodi) : configRepository.findByEntitatCodiIsNull();
        List<ConfigEntity> configs = configRepository.findByEntitatCodiIsNull();
        for (ConfigEntity config: configs) {
            String value = !Strings.isNullOrEmpty(entitatCodi) ? getConfigKeyByEntitat(entitatCodi, config.getKey()) : getConfig(config);
            if (value != null) {
                properties.put(config.getKey(), value);
            }
        }
        return properties;
    }

    @Transactional(readOnly = true)
    public String getEntitatActualCodi() {

        return entitat != null && entitat.get() != null ? entitat.get().getCodi() : null;
    }

    @Transactional(readOnly = true)
    public String getConfigKeyByEntitat(String entitatCodi, String property) {

        String key = crearEntitatKey(entitatCodi, property);
        ConfigEntity configEntity = configRepository.findOne(key);
        if (configEntity != null && (configEntity.isJbossProperty() && configEntity.getValue() == null || configEntity.getValue() != null)) {
            String config = getConfig(configEntity);
            if (!Strings.isNullOrEmpty(config)) {
                return config;
            }
        }
        configEntity = configRepository.findOne(property);
        if (configEntity != null) {
            return getConfig(configEntity);
        }
        log.error("No s'ha trobat la propietat -> key global: " + property + " key entitat: " + key);
        throw new NotDefinedConfigException(property);
    }

    public String crearEntitatKey(String entitatCodi, String key) {

        if (Strings.isNullOrEmpty(entitatCodi) || Strings.isNullOrEmpty(key)) {
            String msg = "Codi entitat " + entitatCodi + " i/o key " + key + " no contenen valor";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        String [] split = key.split(ConfigDto.prefix);
        if (split == null) {
            String msg = "Format no reconegut per la key: " + key;
            log.error(msg);
            throw new RuntimeException(msg);
        }
        return split.length < 2 ? split.length == 0 ? null : split[0] : (ConfigDto.prefix + "." + entitatCodi + split[1]);
    }

    private String getConfig(ConfigEntity configEntity) throws NotDefinedConfigException {

        if (configEntity.isJbossProperty()) {
            // Les propietats de Jboss es llegeixen del fitxer de properties i si no estan definides prenen el valor especificat per defecte a la base de dades.
            return getJBossProperty(configEntity.getKey(), configEntity.getValue());
        }
        return configEntity.getValue();
    }
    
    private String getConfigGeneralIfConfigEntitatNull(ConfigEntity configEntity) throws NotDefinedConfigException {
    	
		if (configEntity.getEntitatCodi() == null) {
			return getConfig(configEntity);
		}
        String value = getConfig(configEntity);
        if (value != null) {
            return value;
        }
        ConfigEntity conf = findGeneralConfigForEntitatConfig(configEntity);
        return conf != null ? getConfig(conf) : null;
    }
    
    private ConfigEntity findGeneralConfigForEntitatConfig(ConfigEntity configEntity) throws NotDefinedConfigException {
		String generalKey = configEntity.getKey().replace(configEntity.getEntitatCodi() + ".", "");
		return configRepository.findOne(generalKey);
    }


    public void crearConfigsEntitat(String codiEntitat) {

        List<ConfigEntity> configs = configRepository.findByEntitatCodiIsNullAndConfigurableIsTrue();
        ConfigDto dto = new ConfigDto();
        dto.setEntitatCodi(codiEntitat);
        ConfigEntity nova;
        List<ConfigEntity> confs = new ArrayList<>();
        for (ConfigEntity config : configs) {
            if (config.isConfigurable()) {
                dto.setKey(config.getKey());
                String key = dto.crearEntitatKey();
                nova = new ConfigEntity();
                nova.crearConfigNova(key, codiEntitat, config);
                confs.add(nova);
            }
        }
        configRepository.save(confs);
    }

    public void deleteConfigEntitat(String codiEntitat) {
        configRepository.deleteByEntitatCodi(codiEntitat);
    }
    

    /**
     * 
     * This class is used to take properties value from properties file
     * Name of the class is incorrect, should be sth like FilePropertiesHelper
     * In Tomcat we take properties manually from tomcat properties file specified in APPSERV_PROPS_PATH (ripea.properties)
     * In Jboss properties are loaded to System automatically from jboss properties (jboss-service.xml)
     *
     */
    @Slf4j
    public static class JBossPropertiesHelper extends Properties {

        private static final String APPSERV_PROPS_PATH = "es.caib.ripea.properties.path"; //in jboss is null

        private static JBossPropertiesHelper instance = null;

        private boolean llegirSystem = true;

        public static JBossPropertiesHelper getProperties() {
            return getProperties(null);
        }
        public static JBossPropertiesHelper getProperties(String path) {
            String propertiesPath = path;
            if (propertiesPath == null) {
                propertiesPath = System.getProperty(APPSERV_PROPS_PATH); 
            }
            if (instance == null) {
                instance = new JBossPropertiesHelper();
                if (propertiesPath != null) {
                    instance.llegirSystem = false; //in jboss we don't enter here
                    log.info("Llegint les propietats de l'aplicació del path: " + propertiesPath);
                    try {
                        if (propertiesPath.startsWith("classpath:")) {
                            instance.load(
                                    JBossPropertiesHelper.class.getClassLoader().getResourceAsStream(
                                            propertiesPath.substring("classpath:".length())));
                        } else if (propertiesPath.startsWith("file://")) {
                            FileInputStream fis = new FileInputStream(
                                    propertiesPath.substring("file://".length()));
                            instance.load(fis);
                        } else {
                            FileInputStream fis = new FileInputStream(propertiesPath);
                            instance.load(fis);
                        }
                    } catch (Exception ex) {
                        log.error("No s'han pogut llegir els properties", ex);
                    }
                }
            }
            return instance;
        }

        public String getProperty(String key) {
            if (llegirSystem)
                return System.getProperty(key); //jboss
            else
                return super.getProperty(key); //tomcat
        }
        public String getProperty(String key, String defaultValue) {
            String val = getProperty(key);
            return (val == null) ? defaultValue : val;
        }

        public boolean isLlegirSystem() {
            return llegirSystem;
        }
        public void setLlegirSystem(boolean llegirSystem) {
            this.llegirSystem = llegirSystem;
        }


        public Properties findAll() {
            return findByPrefixProperties(null);
        }

        public Map<String, String> findByPrefix(String prefix) {
            Map<String, String> properties = new HashMap<String, String>();
            if (llegirSystem) {
                for (Object key: System.getProperties().keySet()) {
                    if (key instanceof String) {
                        String keystr = (String)key;
                        if (keystr.startsWith(prefix)) {
                            properties.put(
                                    keystr,
                                    System.getProperty(keystr));
                        }
                    }
                }
            } else {
                for (Object key: this.keySet()) {
                    if (key instanceof String) {
                        String keystr = (String)key;
                        if (keystr.startsWith(prefix)) {
                            properties.put(
                                    keystr,
                                    getProperty(keystr));
                        }
                    }
                }
            }
            return properties;
        }

        public Properties findByPrefixProperties(String prefix) {
            Properties properties = new Properties();
            if (llegirSystem) {
                for (Object key: System.getProperties().keySet()) {
                    if (key instanceof String) {
                        String keystr = (String)key;
                        if (prefix == null || keystr.startsWith(prefix)) {
                            properties.put(keystr, System.getProperty(keystr));
                        }
                    }
                }
            } else {
                for (Object key: this.keySet()) {
                    if (key instanceof String) {
                        String keystr = (String)key;
                        if (prefix == null || keystr.startsWith(prefix)) {
                            properties.put(keystr, getProperty(keystr));
                        }
                    }
                }
            }
            return properties;
        }
    }
    
	private static final Logger logger = LoggerFactory.getLogger(ConfigHelper.class);
}
