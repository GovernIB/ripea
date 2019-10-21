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
	private static final Logger logger = LoggerFactory.getLogger(AplicacioHelper.class);
	
}
