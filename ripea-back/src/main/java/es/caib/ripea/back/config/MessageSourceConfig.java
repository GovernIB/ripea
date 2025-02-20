package es.caib.ripea.back.config;

import es.caib.ripea.back.base.config.BaseMessageSourceConfig;
import es.caib.ripea.service.intf.config.BaseConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * Configuración del MessageSource de l'aplicació.
 * 
 * @author Límit Tecnologies
 */
@Configuration
public class MessageSourceConfig extends BaseMessageSourceConfig {

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		return localeResolver;
	}

	@Override
	protected String getBasename() {
		return "classpath:" + BaseConfig.APP_NAME + "-back-messages";
	}

}
