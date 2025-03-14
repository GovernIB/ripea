/**
 *
 */
package es.caib.ripea.back.base.config;

import es.caib.ripea.service.intf.config.BaseConfig;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Configuración del MessageSource de l'aplicació.
 *
 * @author Límit Tecnologies
 */
public abstract class BaseMessageSourceConfig {

	protected Locale getDefaultLocale() {
		return Locale.forLanguageTag(BaseConfig.DEFAULT_LOCALE);
	}

	protected String getBasename() {
		return "classpath:" + BaseConfig.APP_NAME + "-messages";
	}

	protected String[] getBasenames() {
		return null;
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		String basename = getBasename();
		if (basename != null) {
			messageSource.setBasename(basename);
		}
		String[] basenames = getBasenames();
		if (basenames != null) {
			messageSource.setBasenames(basenames);
		}
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
		messageSource.setDefaultLocale(getDefaultLocale());
		messageSource.setFallbackToSystemLocale(false);
		return messageSource;
	}

}
