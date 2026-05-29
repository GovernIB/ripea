/**
 * 
 */
package es.caib.ripea.ejb.base;

import es.caib.ripea.service.intf.base.util.I18nUtil;
import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.metrics.jersey.JerseyServerMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

/**
 * Creació del context Spring per a la capa dels EJBs.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@EnableAutoConfiguration(exclude = {
		FreeMarkerAutoConfiguration.class,
		JerseyServerMetricsAutoConfiguration.class,
		ArtemisAutoConfiguration.class
})
@ComponentScan({
		BaseConfig.BASE_PACKAGE + ".service",
		BaseConfig.BASE_PACKAGE + ".persistence"
})
//@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
@PropertySource(ignoreResourceNotFound = true, value = {
		"classpath:application.properties",
		"file://${" + BaseConfig.APP_PROPERTIES + "}",
		"file://${" + BaseConfig.APP_SYSTEM_PROPERTIES + "}"})
public class EjbContextConfig {

	private static boolean initialized;
	private static ApplicationContext applicationContext;

	//Afegit synchronized per evitar errors de race condition al arrancar
	public static synchronized ApplicationContext getApplicationContext() {
		if (!initialized) {
			log.info("Starting EJB spring application...");
			try {
				applicationContext = new AnnotationConfigApplicationContext(EjbContextConfig.class);
				log.info("...EJB spring application started.");
			} finally {
				initialized = true;
			}
		}
		return applicationContext;
	}

	@Bean
	public LocaleResolver localeResolver() {
		AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
		localeResolver.setSupportedLocales(
				Arrays.asList(
						Locale.forLanguageTag("ca"),
						Locale.forLanguageTag("es")));
		localeResolver.setDefaultLocale(Locale.forLanguageTag("ca"));
		return localeResolver;
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:" + BaseConfig.APP_NAME + "-messages");
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
		messageSource.setFallbackToSystemLocale(false);
		return messageSource;
	}

}
