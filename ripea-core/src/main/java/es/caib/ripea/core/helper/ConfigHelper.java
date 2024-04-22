package es.caib.ripea.core.helper;

import com.google.common.base.Strings;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.config.ConfigDto;
import es.caib.ripea.core.api.exception.NotDefinedConfigException;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.entity.config.ConfigEntity;
import es.caib.ripea.core.entity.config.ConfigGroupEntity;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.repository.config.ConfigGroupRepository;
import es.caib.ripea.core.repository.config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
public class ConfigHelper {

    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private ConfigGroupRepository configGroupRepository;

    private static ThreadLocal<EntitatDto> entitat = new ThreadLocal<>();   
    private static ThreadLocal<String> organCodi = new ThreadLocal<>();
    @Autowired
    private OrganGestorRepository organGestorRepository;

    public static ThreadLocal<EntitatDto> getEntitat() {
		return entitat;
	}
	public static void setEntitat(EntitatDto entitat) {
		ConfigHelper.entitat.set(entitat);
	}
	
    public static ThreadLocal<String> getOrganCodi() {
		return organCodi;
	}
	public static void setOrganCodi(String organCodi) {
		ConfigHelper.organCodi.set(organCodi);
	}

    // Propietats per òrgan (#1437 Permetre que les propietats per òrgan afectin als òrgans descendents)
    // Es carregaran en memòria les propietats per òrgan, ja que s'han de calcultar aquestes pels descendents
    // ===============================================================================================================================================
    // MAP: Entitat - Key - Organ - Valor
    private static Map<String, Map<String, Map<String, String>>> propietatsPerEntitatOrgan = new HashMap<>();

    private static boolean hasOrganProperty(String entitatCodi, String organCodi, String key) {
        return propietatsPerEntitatOrgan.containsKey(entitatCodi) &&
                propietatsPerEntitatOrgan.get(entitatCodi).containsKey(key) &&
                propietatsPerEntitatOrgan.get(entitatCodi).get(key).containsKey(organCodi);
    }
    private static String getOrganProperty(String entitatCodi, String organCodi, String key) {
        return hasOrganProperty(entitatCodi, organCodi, key) ? propietatsPerEntitatOrgan.get(entitatCodi).get(key).get(organCodi) : null;
    }

    public void resetPropietatsPerOrgan(String entitatCodi) {
        if (entitatCodi != null) {
            propietatsPerEntitatOrgan.remove(entitatCodi);
        }
    }

    @Transactional(readOnly = true)
    public void inicialitzaPropietatsPerOrgan(String entitatCodi) {

        if (entitatCodi == null) {
            return;
        }
        Map<String, Map<String, String>> propietatsPerOrgan = new HashMap<>();
        List<ConfigEntity> configuracionsPerOrgan = configRepository.findByEntitatCodiAndConfigurableOrganActiuTrueAndOrganCodiIsNotNull(entitatCodi);

        for (ConfigEntity config : configuracionsPerOrgan) {
            String subKey = getSubKey(config);
            if (subKey == null) {
                continue;
            }
            String globalKey = ConfigDto.prefix + subKey;

            // Si ja s'ha emplenat continuem amb el següent
            if (propietatsPerOrgan.containsKey(globalKey) && propietatsPerOrgan.get(globalKey).containsKey(config.getOrganCodi())) {
                continue;
            }

            Map<String, String> propertiesMap = new HashMap<>();
            String prefix = ConfigDto.prefix + "." + entitatCodi + ".";
            String valor = getValue(config);
            propertiesMap.put(config.getOrganCodi(), valor);
            propertiesMap.putAll(getValorsPerFills(config.getOrganCodi(), prefix, subKey, valor, config.isConfigurableOrgansDescendents()));
            propietatsPerOrgan.put(globalKey, propertiesMap);
        }
        propietatsPerEntitatOrgan.put(entitatCodi, propietatsPerOrgan);

    }

    private Map<String, String> getValorsPerFills(String organCodi, String prefix, String sufix, String valor, boolean aplicaDescencents) {
        Map<String, String> propertiesMap = new HashMap<>();
        OrganGestorEntity organGestor = organGestorRepository.findByCodi(organCodi);

        if (aplicaDescencents) {
            for (OrganGestorEntity fill : organGestor.getFills()) {
                ConfigEntity config = configRepository.findByKey(prefix + fill.getCodi() + sufix);
                if (config != null && config.isConfigurableOrganActiu()) {
                    propertiesMap.put(fill.getCodi(), getValue(config));
                    if (config.isConfigurableOrgansDescendents()) {
                        valor = getValue(config);
                    }
                    propertiesMap.putAll(getValorsPerFills(fill.getCodi(), prefix, sufix, valor, aplicaDescencents));
                } else {
                    propertiesMap.put(fill.getCodi(), valor);
                    propertiesMap.putAll(getValorsPerFills(fill.getCodi(), prefix, sufix, valor, aplicaDescencents));
                }
            }
        }
        return propertiesMap;
    }

    private String getSubKey(ConfigEntity config) {
        String prefix = ConfigDto.prefix;

        if (config.isConfigurableOrganActiu()) {
            prefix += "." + config.getEntitatCodi() + "." + config.getOrganCodi();
        } else if (config.isConfigurableEntitatActiu()) {
            prefix += "." + config.getEntitatCodi();
        }

        String[] split = config.getKey().split(prefix);
        return split.length > 1 ? split[1] : null;
    }
    // ===============================================================================================================================================


    @Transactional(readOnly = true)
    public String getConfig(String keyGeneral, String valorDefecte) {

        String valor = getConfig(keyGeneral);
        return !Strings.isNullOrEmpty(valor) ? valor : valorDefecte;
    }

    @Transactional(readOnly = true)
    public String getConfig(String keyGeneral)  {

    	String entitatCodi  = getEntitatActualCodi();
        String organCodi = getOrganActualCodi();
		String value = null;
		ConfigEntity config = configRepository.findOne(keyGeneral);
		if (config == null) {
            return getJBossProperty(keyGeneral);
        }

        Map<String, Map<String, String>> propietatsPerOrgan = propietatsPerEntitatOrgan.get(entitatCodi);
        if (propietatsPerOrgan == null) {
            inicialitzaPropietatsPerOrgan(entitatCodi);
            propietatsPerOrgan = propietatsPerEntitatOrgan.get(entitatCodi);
        }

        // Propietats per òrgan
        if (config.isConfigurableOrganActiu() && !Strings.isNullOrEmpty(organCodi) && propietatsPerOrgan.containsKey(keyGeneral)) {
            Map<String, String> propietatPerOrgans = propietatsPerOrgan.get(keyGeneral);
            if (propietatPerOrgans != null && propietatPerOrgans.containsKey(organCodi)) {
                return propietatPerOrgans.get(organCodi);
            }
        }
//        if (config.isConfigurableOrganActiu() && !Strings.isNullOrEmpty(organCodi)) {
//            // Propietat a nivell d'organ
//            String keyOrgan = getKeyOrgan(entitatCodi, getOrganActualCodi(), keyGeneral);
//			if (keyOrgan != null) {
//	            ConfigEntity configOrganEntity = configRepository.findOne(keyOrgan);
//	            if (configOrganEntity != null) {
//	                value = getValue(configOrganEntity);
//	            }
//			}
//        } else
        if (config.isConfigurableEntitatActiu() && !Strings.isNullOrEmpty(entitatCodi)) {
            // Propietat a nivell d'entitat
            String keyEntitat = getKeyEntitat(entitatCodi, keyGeneral);
            ConfigEntity configEntitatEntity = configRepository.findOne(keyEntitat);
            if (configEntitatEntity != null) {
                value = getValue(configEntitatEntity);
            }
        }
        if (value == null) {
            // Propietat global
            value = getValue(config);
        }
		return value;
	}
    
    
	
	public String getValueForOrgan(String entitatCodi, String organCodi, String keyGeneral) {

        Map<String, Map<String, String>> propietatsPerOrgan = propietatsPerEntitatOrgan.get(entitatCodi);
        if (propietatsPerOrgan == null) {
            inicialitzaPropietatsPerOrgan(entitatCodi);
        }
        return getOrganProperty(entitatCodi, organCodi, keyGeneral);

//        String keyOrgan = getKeyOrgan(entitatCodi, getOrganActualCodi(), keyGeneral);
//        ConfigEntity configOrganEntity = configRepository.findOne(keyOrgan);
//        if (configOrganEntity != null) {
//            return getValue(configOrganEntity);
//        } else {
//        	return null;
//        }
	}
	

    // Obtenir propietats per grup, sense tenir en compte entitat --> Actualment només s'utilitza per el plugin d'usuaris
    @Transactional(readOnly = true)
    public Properties getPropertiesByGroup(String codeGroup) {
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
    public Properties getAllPropertiesEntitatOrGeneral(String entitatCodi) {

        Properties properties = new Properties();
        List<ConfigEntity> configsGeneral = configRepository.findByEntitatCodiIsNull();
        for (ConfigEntity configGeneral: configsGeneral) {
            String value = getValueEntitatOrGeneral(entitatCodi, configGeneral.getKey());
            if (value != null) {
                properties.put(configGeneral.getKey(), value);
			}

        }
        return properties;
    }
    
    @Transactional(readOnly = true)
    public Properties getAllPropertiesOrganOrEntitatOrGeneral(String entitatCodi, String organCodi) {

        Properties properties = new Properties();
        List<ConfigEntity> configsGeneral = configRepository.findByEntitatCodiIsNull();
        for (ConfigEntity configGeneral: configsGeneral) {
            String value = getValueOrganOrEntitatOrGeneral(entitatCodi, organCodi, configGeneral.getKey());
            if (value != null) {
                properties.put(configGeneral.getKey(), value);
			}
        }
        return properties;
    }
    
    @Transactional(readOnly = true)
    public Properties getGroupPropertiesEntitatOrGeneral(List<String> groupCodes, String entitatCodi) {
    	Properties properties = new Properties();
    	for (String groupCode : groupCodes) {
    		properties.putAll(getGroupPropertiesEntitatOrGeneral(groupCode, entitatCodi));
		}
        return properties;
    }
    
    @Transactional(readOnly = true)
    public Properties getGroupPropertiesEntitatOrGeneral(String groupCode, String entitatCodi) {

        Properties properties = new Properties();
        List<ConfigEntity> configsGeneral = configRepository.findByEntitatCodiIsNullAndGroupCode(groupCode);
        for (ConfigEntity configGeneral: configsGeneral) {
            String value = getValueEntitatOrGeneral(entitatCodi, configGeneral.getKey());
            if (value != null) {
                properties.put(configGeneral.getKey(), value);
			}

        }
        return properties;
    }
    
    @Transactional(readOnly = true)
    public Properties getGroupPropertiesOrganOrEntitatOrGeneral(String groupCode, String entitatCodi, String organCodi) {

        Properties properties = new Properties();
        List<ConfigEntity> configsGeneral = configRepository.findByEntitatCodiIsNullAndGroupCode(groupCode);
        for (ConfigEntity configGeneral: configsGeneral) {
            String value = getValueOrganOrEntitatOrGeneral(entitatCodi, organCodi, configGeneral.getKey());
            if (value != null) {
                properties.put(configGeneral.getKey(), value);
			}
        }
        return properties;
    }
    
  

    @Transactional(readOnly = true)
    public String getEntitatActualCodi() {

        return entitat != null && entitat.get() != null ? entitat.get().getCodi() : null;
    }
    
    @Transactional(readOnly = true)
    public String getOrganActualCodi() {

        return organCodi != null ? organCodi.get() : null;
    }


    @Transactional(readOnly = true)
    public String getValueEntitatOrGeneral(String entitatCodi, String keyGeneral) {

        String keyEntitat = getKeyEntitat(entitatCodi, keyGeneral);
        ConfigEntity configEntitat = configRepository.findOne(keyEntitat);
        String value = getValue(configEntitat);
        if (Strings.isNullOrEmpty(value)) {
        	ConfigEntity configGeneral = configRepository.findOne(keyGeneral);
        	value = getValue(configGeneral);
        }
		return value;
    }
    
    @Transactional(readOnly = true)
    public String getValueOrganOrEntitatOrGeneral(String entitatCodi, String organCodi, String keyGeneral) {

//        String keyOrgan = getKeyOrgan(entitatCodi, organCodi, keyGeneral);
//        ConfigEntity configOrgan = configRepository.findOne(keyOrgan);
//        String value = getValue(configOrgan);

        String value = getValueForOrgan(entitatCodi, organCodi, keyGeneral);
	    if (Strings.isNullOrEmpty(value)) {
	        String keyEntitat = getKeyEntitat(entitatCodi, keyGeneral);
	        ConfigEntity configEntitat = configRepository.findOne(keyEntitat);
	        value = getValue(configEntitat);
	        if (Strings.isNullOrEmpty(value)) {
	        	ConfigEntity configGeneral = configRepository.findOne(keyGeneral);
	        	value = getValue(configGeneral);
	        }
        }
        return value;
    }

    public String getKeyEntitat(String entitatCodi, String key) {

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
    
    public String getKeyOrgan(String entitatCodi, String organCodi, String key) {
    	if (Utils.isEmpty(organCodi)) {
			return null;
		}
        if (Utils.isEmpty(entitatCodi) || Utils.isEmpty(key)) {
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
        return split.length < 2 ? split.length == 0 ? null : split[0] : (ConfigDto.prefix + "." + entitatCodi + "." + organCodi + split[1]);
    }

    
    
    public void crearConfigPerEntitats(ConfigEntity config,  List<EntitatEntity> entitats) {

        for (EntitatEntity entitat : entitats) {
            String key = getKeyEntitat(entitat.getCodi(), config.getKey());
            if (configRepository.findByKey(key) == null ) {
                ConfigEntity nova = new ConfigEntity();
                nova.crearConfigNova(key, entitat.getCodi(), null, config);
                configRepository.save(nova);
            }
        }
    }
    
    
    public void removeConfigPerEntitats(ConfigEntity config,  List<EntitatEntity> entitats) {

        for (EntitatEntity entitat : entitats) {
            String key = getKeyEntitat(entitat.getCodi(), config.getKey());
            ConfigEntity configEntitat = configRepository.findByKey(key);
            if (config != null ) {
                configRepository.delete(configEntitat);
            }
        }
    }

    private String getValue(ConfigEntity configEntity) throws NotDefinedConfigException {

		if (configEntity != null) {
	        if (configEntity.isJbossProperty()) {
	            // Les propietats de Jboss es llegeixen del fitxer de properties i si no estan definides prenen el valor especificat per defecte a la base de dades.
	            return getJBossProperty(configEntity.getKey(), configEntity.getValue());
	        } else {
	            return configEntity.getValue();
	        }
		} else {
			return null;
		}
    }
    
    private String getConfigGeneralIfConfigEntitatNull(ConfigEntity configEntity) throws NotDefinedConfigException {
    	
		if (configEntity.getEntitatCodi() == null) {
			return getValue(configEntity);
		}
        String value = getValue(configEntity);
        if (value != null) {
            return value;
        }
        ConfigEntity conf = findGeneralConfigForEntitatConfig(configEntity);
        return conf != null ? getValue(conf) : null;
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
                nova.crearConfigNova(key, codiEntitat, null, config);
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
