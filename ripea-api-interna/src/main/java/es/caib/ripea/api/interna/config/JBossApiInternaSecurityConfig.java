package es.caib.ripea.api.interna.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAttributes2GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleMappableAttributesRetriever;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.j2ee.J2eePreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.extern.slf4j.Slf4j;
/**
 * Configuració de Spring Security per a desplegar l'aplicació sobre JBoss.
 * 
 * @author Limit Tecnologies
 */
@Slf4j
@Configuration
@EnableWebSecurity
@ConditionalOnWarDeployment
public class JBossApiInternaSecurityConfig extends BaseApiInternaSecurityConfig {

	@Value("${es.caib.ripea.security.mappableRoles:" +
			BaseConfig.ROLE_SUPER + "," +
			BaseConfig.ROLE_ADMIN + "," +
			BaseConfig.ROLE_DISSENY + "," +
			BaseConfig.ROLE_ORGAN_ADMIN + "," +
			BaseConfig.ROLE_BSTWS + "," +
			BaseConfig.ROLE_API_HIST + "," +
			BaseConfig.ROLE_REVISIO + "," +
			BaseConfig.ROLE_USER + "}")
	private String mappableRoles;
	
	@Value("${es.caib.ripea.security.nameAttributeKey:preferred_username}")
	private String nameAttributeKey;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.addFilterBefore(
				preAuthenticatedProcessingFilter(),
				BasicAuthenticationFilter.class);
		http.authenticationProvider(preauthAuthProvider());
		http.authorizeHttpRequests().
				requestMatchers(publicRequestMatchers()).permitAll().
				anyRequest().authenticated();
		http.headers().frameOptions().sameOrigin();
		http.csrf().disable();
		http.cors();
		return http.build();
	}

	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {
		final List<AuthenticationProvider> providers = new ArrayList<>(1);
		providers.add(preauthAuthProvider());
		return new ProviderManager(providers);
	}

	@Bean
	public PreAuthenticatedAuthenticationProvider preauthAuthProvider() {
		var preauthAuthProvider = new PreAuthenticatedAuthenticationProvider();
		preauthAuthProvider.setPreAuthenticatedUserDetailsService(preAuthenticatedGrantedAuthoritiesUserDetailsService());
		return preauthAuthProvider;
	}

	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults(ROLE_PREFIX);
	}

	@Bean
	public PreAuthenticatedGrantedAuthoritiesUserDetailsService preAuthenticatedGrantedAuthoritiesUserDetailsService() {
		return new PreAuthenticatedGrantedAuthoritiesUserDetailsService() {
			protected UserDetails createUserDetails(Authentication token, Collection<? extends GrantedAuthority> authorities) {
//				if (token.getDetails() instanceof KeycloakWebAuthenticationDetails) {
//					KeycloakWebAuthenticationDetails keycloakWebAuthenticationDetails = (KeycloakWebAuthenticationDetails)token.getDetails();
//					return new User(token.getName(), "N/A", true, true, true, true,
//							authorities, keycloakWebAuthenticationDetails.getKeycloakPrincipal());
//				}
				return new User(token.getName(), "N/A", true, true, true, true, authorities);
			}
		};
	}

	@Bean
	public J2eePreAuthenticatedProcessingFilter preAuthenticatedProcessingFilter() throws Exception {
		var preAuthenticatedProcessingFilter = new J2eePreAuthenticatedProcessingFilter();
		preAuthenticatedProcessingFilter.setAuthenticationDetailsSource(authenticationDetailsSource());
		preAuthenticatedProcessingFilter.setAuthenticationManager(authenticationManager());
		preAuthenticatedProcessingFilter.setContinueFilterChainOnUnsuccessfulAuthentication(false);
		return preAuthenticatedProcessingFilter;
	}

	@Bean
	public AuthenticationDetailsSource<HttpServletRequest, PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails> authenticationDetailsSource() {
		var authenticationDetailsSource = new J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource() {
			@Override
			public PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails buildDetails(HttpServletRequest context) {
				var j2eeUserRoles = getUserRoles(context);
				if (!j2eeUserRoles.contains("tothom")) {
					j2eeUserRoles.add("tothom");
				}
				logger.debug("Roles from ServletRequest for " + context.getUserPrincipal().getName() + ": " + j2eeUserRoles);
				PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails result;
				if (context.getUserPrincipal() instanceof KeycloakPrincipal) {
					var keycloakPrincipal = ((KeycloakPrincipal<?>)context.getUserPrincipal());
					Set<String> roles = new HashSet<>();
					roles.addAll(j2eeUserRoles);
					var realmAccess = keycloakPrincipal.getKeycloakSecurityContext().getToken().getRealmAccess();
					if (realmAccess != null && realmAccess.getRoles() != null) {
						logger.debug("Keycloak token realm roles: " + realmAccess.getRoles());
						roles.addAll(realmAccess.getRoles());
					}
					var resourceAccess = keycloakPrincipal.getKeycloakSecurityContext().getToken().getResourceAccess(
							keycloakPrincipal.getKeycloakSecurityContext().getToken().getIssuedFor());
					if (resourceAccess != null && resourceAccess.getRoles() != null) {
						logger.debug("Keycloak token resource roles: " + resourceAccess.getRoles());
						roles.addAll(resourceAccess.getRoles());
					}
					logger.debug("Creating WebAuthenticationDetails for " + keycloakPrincipal.getName() + " with roles " + roles);
					result = new KeycloakWebAuthenticationDetails(context, j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(roles), keycloakPrincipal);
				} else {
					logger.debug("Creating WebAuthenticationDetails for " + context.getUserPrincipal().getName() + " with roles " + j2eeUserRoles);
					result = new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(context, j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(j2eeUserRoles));
				}
				return result;
			}
		};
		var mappableAttributesRetriever = new SimpleMappableAttributesRetriever();
		mappableAttributesRetriever.setMappableAttributes(new HashSet<>(Arrays.asList(mappableRoles.split(","))));
		authenticationDetailsSource.setMappableRolesRetriever(mappableAttributesRetriever);
		var attributes2GrantedAuthoritiesMapper = new SimpleAttributes2GrantedAuthoritiesMapper();
		attributes2GrantedAuthoritiesMapper.setAttributePrefix(ROLE_PREFIX);
		authenticationDetailsSource.setUserRoles2GrantedAuthoritiesMapper(attributes2GrantedAuthoritiesMapper);
		return authenticationDetailsSource;
	}
//
//	@Bean
//	public LogoutHandler getLogoutHandler() {
//		return (request, response, authentication) -> {
//			try {
//				request.logout();
//			} catch (ServletException ex) {
//				log.error("Error al sortir de l'aplicació", ex);
//			}
//		};
//	}

	@SuppressWarnings("serial")
	public static class KeycloakWebAuthenticationDetails extends PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails {
		private KeycloakPrincipal<?> keycloakPrincipal;
		public KeycloakWebAuthenticationDetails(HttpServletRequest request, Collection<? extends GrantedAuthority> authorities, KeycloakPrincipal<?> keycloakPrincipal) {

			super(request, authorities);
			this.keycloakPrincipal = keycloakPrincipal;
		}
		public KeycloakPrincipal<?> getKeycloakPrincipal() {
			return keycloakPrincipal;
		}
	}
}