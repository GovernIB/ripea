package es.caib.ripea.back.base.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

@Getter
public class PreauthOidcUserDetails extends User implements OidcUser {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;
    private final Map<String, Object> attributes;
    private final Map<String, Object> claims;
    private final String nameAttributeKey;
    public PreauthOidcUserDetails(
            String jwtIdToken,
            String username,
            Instant issueTime,
            Instant expirationTime,
            Map<String, Object> claims,
            String nameAttributeKey,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, "N/A", true, true, true, true, authorities);
        this.idToken = new OidcIdToken(
                jwtIdToken,
                issueTime,
                expirationTime,
                claims);
        this.userInfo = new OidcUserInfo(claims);
        this.attributes = claims;
        this.claims = claims;
        this.nameAttributeKey = nameAttributeKey;
    }
    public String getName() {
        return getUsername();
    }
}
