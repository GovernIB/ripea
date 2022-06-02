package es.caib.ripea.plugin;

import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is used to take properties value from properties file
 * It is used by Plugins
 * In Tomcat we take properties manually from tomcat properties file specified in APPSERV_PROPS_PATH (ripea.properties)
 * In Jboss properties are loaded to System automatically from jboss properties (jboss-service.xml)
 * 
 */
public class PropertiesHelper extends Properties {

	private static final String APPSERV_PROPS_PATH = "es.caib.ripea.properties.path";

	private static PropertiesHelper instance = null;

	
	private PropertiesHelper(Properties defaults) {
		super(defaults);
	}

	public static PropertiesHelper getProperties() {
		return getProperties(null);
	}
	public static PropertiesHelper getProperties(String path) {
		String propertiesPath = path;
		if (propertiesPath == null) {
			propertiesPath = System.getProperty(APPSERV_PROPS_PATH);//in jboss is always null
		}
		if (instance == null) {
			instance = new PropertiesHelper(System.getProperties());//in jboss we load from System
			if (propertiesPath != null) { //in tomcat we load from propertiesPath
				logger.info("Llegint les propietats de l'aplicació del path: " + propertiesPath);
				try {
					if (propertiesPath.startsWith("classpath:")) {
						instance.load(
								PropertiesHelper.class.getClassLoader().getResourceAsStream(
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
					logger.error("No s'han pogut llegir els properties", ex);
				}
			}
		}
		return instance;
	}

	public String getProperty(String key) {
		return super.getProperty(key);
	}
	public String getProperty(String key, String defaultValue) {
		String val = getProperty(key);
		return (val == null) ? defaultValue : val;
	}

	public boolean getAsBoolean(String key) {
		return new Boolean(getProperty(key)).booleanValue();
	}
	public int getAsInt(String key) {
		return new Integer(getProperty(key)).intValue();
	}
	public long getAsLong(String key) {
		return new Long(getProperty(key)).longValue();
	}
	public float getAsFloat(String key) {
		return new Float(getProperty(key)).floatValue();
	}
	public double getAsDouble(String key) {
		return new Double(getProperty(key)).doubleValue();
	}


	public Properties findAll() {
		return findByPrefix(null);
	}
	public Properties findByPrefix(String prefix) {
		Properties properties = new Properties();
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
		return properties;
	}

	private static final Logger logger = LoggerFactory.getLogger(PropertiesHelper.class);
	private static final long serialVersionUID = 1L;

}
