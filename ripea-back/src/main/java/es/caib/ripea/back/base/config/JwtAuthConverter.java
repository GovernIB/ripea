package es.caib.ripea.back.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Classe de conversió entre un token JWT i un token d'autenticació de Spring Security.
 * 
 * @author Límit Tecnologies
 */
@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

	@Value("${jwt.auth.converter.principal-claim:preferred_username}")
	private String principalClaim;

	private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {
		Collection<GrantedAuthority> authorities = Stream.concat(
				jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
				extractJwtGrantedAuthorities(jwt).stream()).collect(Collectors.toSet());
		return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
	}

	private String getPrincipalClaimName(Jwt jwt) {
		String claimName = principalClaim != null ? principalClaim : JwtClaimNames.SUB;
		return jwt.getClaim(claimName);
	}

	@SuppressWarnings("unchecked")
	private Collection<? extends GrantedAuthority> extractJwtGrantedAuthorities(Jwt jwt) {
		Set<String> roles = new HashSet<>();
		// Recuperam els rols a nivell de REALM
		Map<String, Object> realmAccess = jwt.getClaim("realm_access");
		if (realmAccess != null && !realmAccess.isEmpty()) {
			List<String> realmRoles = ((List<String>)realmAccess.get("roles"));
			if (realmRoles != null && !realmRoles.isEmpty()) {
				roles.addAll(realmRoles);
			}
		}
		// Obtenim el clientId (al claim "azp")
		String clientId = jwt.getClaim("azp");
		// Recuperam els rols del client
		if (clientId != null && !clientId.isEmpty()) {
			Map<String, Object> resourceAccess = (Map<String, Object>)jwt.getClaims().get("resource_access");
			if (resourceAccess != null && !resourceAccess.isEmpty()) {
				Map<String, Object> clientAccess = (Map<String, Object>)resourceAccess.get(clientId);
				if (clientAccess != null && !clientAccess.isEmpty()) {
					List<String> clientRoles = ((List<String>)clientAccess.get("roles"));
					if (clientRoles != null && !clientRoles.isEmpty()) {
						roles.addAll(clientRoles);
					}
				}
			}
		}
		return mapRolesToGrantedAuthorities(roles);
	}

	private List<GrantedAuthority> mapRolesToGrantedAuthorities(Set<String> roles) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		if (!roles.isEmpty()) {
			grantedAuthorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
		}
		return grantedAuthorities;
	}



}
