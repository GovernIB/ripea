package es.caib.ripea.core.helper;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

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
    @Autowired
    private EntitatRepository entitatRepository;
    
    
    private static ThreadLocal<EntitatDto> entitat = new ThreadLocal<>();    
    
    public static ThreadLocal<EntitatDto> getEntitat() {
		return entitat;
	}


	public static void setEntitat(EntitatDto entitat) {
		ConfigHelper.entitat.set(entitat);
	}
    
    
    /** Mètode que revisa després d'iniciar Distribucio que totes les entitats tinguin una entrada
     * per cada propietat configurable a nivell d'entitat.
     */
    @PostConstruct
    @Transactional
    public void postConstruct() {
		// Recuperar totes les propietats configurables que no siguin d'entitat
    	List<ConfigEntity> listConfigEntity = configRepository.findConfigurablesAmbEntitatNull();
    	List<ConfigEntity> llistatPropietatsConfigurables = configRepository.findConfigurables();
	    List<EntitatEntity> llistatEntitats = entitatRepository.findAll();
	    int propietatsNecessaries = listConfigEntity.size() * (llistatEntitats.size() + 1);
		// Mirar que la propietat existeixi per a la entitat, si no crear-la amb el valor null
	    if (llistatPropietatsConfigurables.size() != propietatsNecessaries) {
		    for (ConfigEntity cGroup : listConfigEntity) {
		    	int lengthKey = cGroup.getKey().length();
		    	for (EntitatEntity entitat : llistatEntitats) {
		    		if (cGroup.getEntitatCodi() == null) {
		        		String cercarPropietat = cGroup.getKey().substring(0, 14) + entitat.getCodi() + cGroup.getKey().substring(13, lengthKey);
		        		ConfigEntity configEntity = configRepository.findPerKey(cercarPropietat);
		        		if (configEntity == null) {
		        			ConfigEntity novaPropietat = new ConfigEntity();
			        		novaPropietat.setDescription(cGroup.getDescription());
			        		novaPropietat.setEntitatCodi(entitat.getCodi());
			        		novaPropietat.setGroupCode(cGroup.getGroupCode());
			        		novaPropietat.setJbossProperty(cGroup.isJbossProperty());
			        		novaPropietat.setKey(cercarPropietat);
			        		novaPropietat.setPosition(cGroup.getPosition());
			        		novaPropietat.setConfigurable(cGroup.isConfigurable());			        		
//			        		novaPropietat.setTypeCode(cGroup.getTypeCode());
			        		
		                    logger.info("Guardant la propietat: " + novaPropietat.getKey());		        		
			        		configRepository.save(novaPropietat);
		        		}
		    		}
		    	}
		    }	
	    }
    }
    
    

 

	@Transactional(readOnly = true)
	public String getConfig(String entitatActualCodi, String keyGeneral) {
		
		if (entitatActualCodi == null) {
			EntitatDto entitatActual = ConfigHelper.entitat.get();
			if (entitatActual != null)
				entitatActualCodi = entitatActual.getCodi();
		}
		
		logger.debug("Entitat actual per les propietats : " + entitatActualCodi);

		String keyPerEntitat = convertirKeyGeneralToKeyPropietat(entitatActualCodi, keyGeneral);

		ConfigEntity configPerEntitat = configRepository.findOne(keyPerEntitat);

		if (configPerEntitat != null) {
			String valueConfigPerEntitat = getConfig(configPerEntitat);
			if (valueConfigPerEntitat != null) {
				return valueConfigPerEntitat;
			} else {
				ConfigEntity configGeneral = configRepository.findOne(keyGeneral);
				String valueConfigGeneral = getConfig(configGeneral);
				if (valueConfigGeneral != null) {
					return valueConfigGeneral;
				} else {
					String valueEntitat = getJBossProperty(keyPerEntitat);
					if (valueEntitat != null) {
						return valueEntitat;
					} else {
						return getJBossProperty(keyGeneral);
					}
				}
			}
		} else {
			String valueEntitat = getJBossProperty(keyPerEntitat);
			if (valueEntitat != null) {
				return valueEntitat;
			} else {
				return getJBossProperty(keyGeneral);
			}
		}
	}

    @Transactional(readOnly = true)
    public String getConfig(String keyGeneral)  {
		return this.getConfig(null, keyGeneral);
	}
    

    
	@Transactional(readOnly = true)
	public static String getEntitatActualCodi() {
		EntitatDto entitatActual = ConfigHelper.entitat.get();
		if (entitatActual != null) {
			return entitatActual.getCodi();
		} else {
			return null;
		}
	}
	
	private String convertirKeyGeneralToKeyPropietat (String entitatActualCodi, String key) {
		if (entitatActualCodi != null) {
			String keyReplace = key.replace(".", "_");
			String[] splitKey = keyReplace.split("_");
			String keyEntitat = "";
			for (int i=0; i<splitKey.length; i++) {
				if (i == (splitKey.length - 1)) {
					keyEntitat = keyEntitat + splitKey[i];
				}else if (i == 2){
					keyEntitat = keyEntitat + splitKey[i] + "." + entitatActualCodi + ".";
				}else {				
					keyEntitat = keyEntitat + splitKey[i] + ".";
				}
			}
			key = keyEntitat;
		}
		return key;
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
		} else {
			String value = getConfig(configEntity);
			if (value != null) {
				return value;
			} else {
				ConfigEntity conf = findGeneralConfigForEntitatConfig(configEntity);
				if (conf != null) {
					return getConfig(conf);
				} else {
					return null;
				}
				
			}
		}
    }
    
    private ConfigEntity findGeneralConfigForEntitatConfig(ConfigEntity configEntity) throws NotDefinedConfigException {
		String generalKey = configEntity.getKey().replace(configEntity.getEntitatCodi() + ".", "");
		return configRepository.findOne(generalKey);
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
                        if (prefix == null || keystr.startsWith(prefix)) {
                            properties.put(
                                    keystr,
                                    getProperty(keystr));
                        }
                    }
                }
            }
            return properties;
        }
    }

    public void crearConfigsEntitat(String codiEntitat) {

        List<ConfigEntity> configs = configRepository.findByEntitatCodiIsNull();
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
    
    
	private static final Logger logger = LoggerFactory.getLogger(ConfigHelper.class);
}
