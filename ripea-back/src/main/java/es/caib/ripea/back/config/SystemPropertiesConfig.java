/**
 * 
 */
package es.caib.ripea.back.config;

import es.caib.ripea.service.intf.config.BaseConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuració de les propietats de l'aplicació a partir de les propietats de
 * sistema (System.getProperty).
 * 
 * @author Límit Tecnologies
 */
@Configuration
@PropertySource(ignoreResourceNotFound = true, value = {
	"file://${" + BaseConfig.APP_PROPERTIES + "}",
	"file://${" + BaseConfig.APP_SYSTEM_PROPERTIES + "}"})
public class SystemPropertiesConfig {

}
