package es.caib.ripea.back.config;

import es.caib.ripea.back.base.config.BaseHateoasMessageResolverConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuració del MessageResolver per a spring-hateoas.
 * 
 * @author Límit Tecnologies
 */
@Configuration
public class HateoasMessageResolverConfig extends BaseHateoasMessageResolverConfig {

	@Override
	protected String getBasename() {
		return "ripea-back-rest-messages";
	}

}
