/**
 * 
 */
package es.caib.ripea.back.config;

import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuració de Spring Security per a executar l'aplicació amb Spring Boot.
 * 
 * @author Limit Tecnologies
 */
@Slf4j
@Configuration
@ConditionalOnNotWarDeployment
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class SpringBootWebSecurityConfig extends BaseWebSecurityConfig {

//	protected final JwtAuthConverter jwtAuthConverter;

//	public SpringBootWebSecurityConfig(JwtAuthConverter jwtAuthConverter) {
//		this.jwtAuthConverter = jwtAuthConverter;
//	}

//	@Bean
//	@Order(1)
//	public SecurityFilterChain oauth2ResourceSecurityFilterChain(HttpSecurity http) throws Exception {
//		http
//				.authorizeHttpRequests(authorize -> authorize
//						.requestMatchers(new MultiPackageRequestMatcher(
//								"es.caib.ripea.back.base.controller",
//								"es.caib.ripea.back.resourcecontroller"
//						)).authenticated()
//						.anyRequest().permitAll()
//				)
//				.oauth2ResourceServer(oauth2 -> {
//					if (jwtAuthConverter != null) {
//						oauth2.jwt().jwtAuthenticationConverter(jwtAuthConverter);
//					}
//				})
//				.logout(logout -> logout
//						.invalidateHttpSession(true)
//						.clearAuthentication(true)
//						.deleteCookies("OAuth_Token_Request_State", "JSESSIONID")
//						.logoutSuccessUrl("/")
//				)
//				.headers(headers -> headers.frameOptions().sameOrigin())
//				.csrf(csrf -> csrf.disable())
//				.cors();
//		return http.build();
//	}

	@Bean
//	@Order(2)
	public SecurityFilterChain oauth2LoginSecurityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(publicRequestMatchers()).permitAll()
                        .requestMatchers(superRequestMatchers()).hasRole(BaseConfig.ROLE_SUPER)
                        .requestMatchers(adminRequestMatchers()).hasRole(BaseConfig.ROLE_ADMIN)
                        .requestMatchers(procedimentRequestMatchers()).hasAnyRole(BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ORGAN_ADMIN, BaseConfig.ROLE_REVISIO, BaseConfig.ROLE_DISSENY)
						.anyRequest().authenticated()
				)
				.oauth2Login(oauth2 -> oauth2
						.userInfoEndpoint().userService(oauth2UserService())
				)
				.logout(logout -> logout
						.invalidateHttpSession(true)
						.clearAuthentication(true)
						.deleteCookies("OAuth_Token_Request_State", "JSESSIONID")
						//.addLogoutHandler(oauth2LogoutHandler())
						// .logoutUrl(LOGOUT_URL)
						.logoutSuccessUrl("/")
				)
				.headers(headers -> headers.frameOptions().sameOrigin())
				.csrf(csrf -> csrf.disable())
				.cors();
		return http.build();
	}

	private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
		final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
		return (userRequest) -> {
			OAuth2User oauth2User = delegate.loadUser(userRequest);
			OAuth2AccessToken accessToken = userRequest.getAccessToken();
			Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
			try {
				JWT parsedJwt = JWTParser.parse(accessToken.getTokenValue());
				JSONObject realmAccess = (JSONObject)parsedJwt.getJWTClaimsSet().getClaim("realm_access");
				if (realmAccess != null) {
					JSONArray roles = (JSONArray)realmAccess.get("roles");
					if (roles != null) {
						roles.stream().
						map(r -> new SimpleGrantedAuthority((String)r)).
						forEach(mappedAuthorities::add);
					}
					mappedAuthorities.add(new SimpleGrantedAuthority("tothom"));
				}
			} catch (ParseException ex) {
				log.warn("No s'han pogut obtenir els rols del token JWT", ex);
			}
			return new DefaultOAuth2User(
					mappedAuthorities,
					oauth2User.getAttributes(),
					userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());
		};
	}

}
