/**
 * 
 */
package es.caib.ripea.service.intf.config;

/**
 * Propietats de configuració de l'aplicació.
 * 
 * @author Límit Tecnologies
 */
public class BaseConfig {

	public static final String APP_NAME = "ripea";
	public static final String DB_PREFIX = "ipa_";

	public static final String BASE_PACKAGE = "es.caib." + APP_NAME;

	public static final String APP_PROPERTIES = BASE_PACKAGE + ".properties";
	public static final String APP_SYSTEM_PROPERTIES = BASE_PACKAGE + ".system.properties";

	public static final String ROLE_SUPER = "IPA_SUPER";
	public static final String ROLE_ADMIN = "IPA_ADMIN";

	public static final String ROLE_USER = "tothom";

}
