package es.caib.ripea.back.config;

import java.net.URL;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import es.caib.ripea.back.base.config.BaseMessageSourceConfig;
import es.caib.ripea.service.intf.config.BaseConfig;

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
//		URL resource = getClass().getClassLoader().getResource("ripea-messages_ca.properties");
		return "classpath:" + BaseConfig.APP_NAME + "-messages";
	}

}
