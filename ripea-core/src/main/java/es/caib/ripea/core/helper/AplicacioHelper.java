package es.caib.ripea.core.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AplicacioHelper {

	public String propertyBaseUrl() {
		logger.debug("Consulta de la propietat base URL");
		return PropertiesHelper.getProperties().getProperty("es.caib.ripea.base.url");
	}
	
	public boolean propertyBooleanFindByKey(String key, boolean defaultValueIfNull) {
		logger.debug("Consulta del valor del propietat= " + key + ", valorPerDefecte=" + defaultValueIfNull);
		boolean booleanValue;
		String value = PropertiesHelper.getProperties().getProperty(key);
		if (value != null) {
			booleanValue = Boolean.parseBoolean(value);
		} else {
			booleanValue = defaultValueIfNull;
		}
		return booleanValue;
	}
	
	
	private static final Logger logger = LoggerFactory.getLogger(AplicacioHelper.class);
	
}
