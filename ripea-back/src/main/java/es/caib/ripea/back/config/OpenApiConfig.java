/**
 * 
 */
package es.caib.ripea.back.config;

import es.caib.ripea.back.base.config.BaseOpenApiConfig;

/**
 * Configuració de Springdoc OpenAPI.
 * 
 * @author Limit Tecnologies
 */
public class OpenApiConfig extends BaseOpenApiConfig {

	@Override
	protected boolean enableAuthComponent() {
		return false;
	}

}
