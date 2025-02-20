/**
 * 
 */
package es.caib.ripea.back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Configuració de Spring Security per a executar l'aplicació amb Spring Boot.
 * 
 * @author Limit Tecnologies
 */
public class BaseWebSecurityConfig {

	public static final String ROLE_PREFIX = "";
	public static final String LOGOUT_URL = "/usuari/logout";

	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults(ROLE_PREFIX);
	}

	@Bean
	public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}

	protected RequestMatcher[] publicRequestMatchers() {
		return new RequestMatcher[] {
				new AntPathRequestMatcher("/api"),
				new AntPathRequestMatcher("/api/**"),
				new AntPathRequestMatcher("/public/**"),
				new AntPathRequestMatcher("/api-docs"),
				new AntPathRequestMatcher("/api-docs/**/*"),
				new AntPathRequestMatcher("/css/**/*"),
				new AntPathRequestMatcher("/fonts/**/*"),
				new AntPathRequestMatcher("/img/**/*"),
				new AntPathRequestMatcher("/js/**/*"),
				new AntPathRequestMatcher("/webjars/**/*"),
		};
	}

	protected RequestMatcher[] superRequestMatchers() {
		return new RequestMatcher[] {
				new AntPathRequestMatcher("/entitat*"),
				new AntPathRequestMatcher("/config*"),
		};
	}
	
	protected RequestMatcher[] adminRequestMatchers() {
		return new RequestMatcher[] {
				new AntPathRequestMatcher("/permis*"),
				new AntPathRequestMatcher("/tipusDocumental*"),
		};
	}
	
	protected RequestMatcher[] procedimentRequestMatchers() {
		return new RequestMatcher[] {
				new AntPathRequestMatcher("/metaExpedient*"),
		};
	}
}
