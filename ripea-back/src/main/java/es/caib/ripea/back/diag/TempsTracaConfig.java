package es.caib.ripea.back.diag;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import es.caib.ripea.service.intf.config.BaseConfig;

/**
 * Registre del filtre de la traça de temps de les peticions a l'API (veure
 * {@link es.caib.ripea.service.intf.utils.TempsTraca}). Els interceptors es registren a WebMvcConfig, perquè
 * l'ordre hi importa.
 */
@Configuration
public class TempsTracaConfig {

	@Bean
	public FilterRegistrationBean<TempsTracaFilter> tempsTracaFilter() {
		FilterRegistrationBean<TempsTracaFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new TempsTracaFilter());
		registrationBean.addUrlPatterns(BaseConfig.API_PATH + "/*");
		registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return registrationBean;
	}

}
