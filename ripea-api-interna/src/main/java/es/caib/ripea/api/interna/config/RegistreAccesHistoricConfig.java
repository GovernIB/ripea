package es.caib.ripea.api.interna.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.historic.AccesHistoricDto;
import es.caib.ripea.service.intf.service.HistoricService;
import es.caib.ripea.service.intf.utils.RegistreAccesHistoricFilter;

/**
 * Registre d'accessos a l'API de consulta d'històrics ({@code /api/historic}), per saber si algun sistema extern la fa
 * servir. Es desa només si la propietat {@value PropertyConfig#HISTORIC_REGISTRE_ACCES_ACTIU} és activa.
 *
 * @author Limit Tecnologies
 */
@Configuration
public class RegistreAccesHistoricConfig {

	/** Ordre per defecte: darrere de Spring Security, amb l'usuari ja autenticat. */
	@Bean
	public FilterRegistrationBean<RegistreAccesHistoricFilter> registreAccesHistoricFilter(HistoricService historicService) {
		FilterRegistrationBean<RegistreAccesHistoricFilter> registrationBean = new FilterRegistrationBean<>();
		// L'API interna no té rol actual: els permisos són els rols de l'usuari de l'aplicació externa
		registrationBean.setFilter(new RegistreAccesHistoricFilter(historicService, AccesHistoricDto.APLICACIO_API_INTERNA, null));
		registrationBean.addUrlPatterns("/api/historic/*");
		return registrationBean;
	}

}
