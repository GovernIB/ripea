package es.caib.ripea.api.interna.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class BaseApiInternaSecurityConfig {

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
				new AntPathRequestMatcher("/"),
				new AntPathRequestMatcher("/index.html"),
				new AntPathRequestMatcher("/swagger-ui.html"),
				new AntPathRequestMatcher("/swagger-ui/**"),
				new AntPathRequestMatcher("/v3/api-docs"),
				new AntPathRequestMatcher("/v3/api-docs/**"),
				new AntPathRequestMatcher("/rest/portafib/v1/**/*"),
				new AntPathRequestMatcher("/notib/**/*")
		};
	}

}