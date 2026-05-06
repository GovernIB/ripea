package es.caib.ripea.back.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Filtre TEMPORAL per a proves d'accessibilitat sense autenticació.
 * Simula un usuari autenticat a tres nivells:
 *  1. UserPrincipal al request → activa SessioHelper i J2eePreAuthenticatedProcessingFilter (Spring Security)
 *  2. Contexte de seguretat JBoss via PicketBox (reflexió) → desbloqueja @RolesAllowed("**") als EJBs
 *
 * ELIMINAR aquest filtre quan es reprengui l'autenticació real.
 */
@Slf4j
public class FakeAuthenticationFilter implements Filter {

    private static final String FAKE_USER = "rip_admin";
    private static final Set<String> FAKE_ROLES = new HashSet<>(Arrays.asList("tothom", "IPA_ADMIN"));

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 1. JBoss EJB security context (desbloqueja @RolesAllowed als EJBs)
        injectJBossSecurityContext();
        // 2. Spring Security context (J2eePreAuthenticatedProcessingFilter ja ha corregut amb anonymous)
        List<SimpleGrantedAuthority> authorities = FAKE_ROLES.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(FAKE_USER, null, authorities));
        try {
            // 3. Wrapper del request (per a la condició de SessioHelper i isUserInRole)
            chain.doFilter(new FakeAuthRequestWrapper((HttpServletRequest) request), response);
        } finally {
            SecurityContextHolder.clearContext();
            clearJBossSecurityContext();
        }
    }

    private void injectJBossSecurityContext() {
        try {
            Class<?> scFactoryClass = Class.forName("org.jboss.security.SecurityContextFactory");
            Class<?> scAssocClass   = Class.forName("org.jboss.security.SecurityContextAssociation");
            Class<?> simplePrincipalClass = Class.forName("org.jboss.security.SimplePrincipal");
            Class<?> scInterface    = Class.forName("org.jboss.security.SecurityContext");

            Principal principal = (Principal) simplePrincipalClass
                    .getConstructor(String.class).newInstance(FAKE_USER);

            Object sc = scFactoryClass
                    .getMethod("createSecurityContext", String.class)
                    .invoke(null, "other");

            Subject subject = new Subject();
            subject.getPrincipals().add(principal);

            Object util = sc.getClass().getMethod("getUtil").invoke(sc);
            util.getClass()
                    .getMethod("createSubjectInfo", Principal.class, Object.class, Subject.class)
                    .invoke(util, principal, null, subject);

            scAssocClass.getMethod("setSecurityContext", scInterface).invoke(null, sc);
        } catch (Exception e) {
            log.debug("PicketBox no disponible, el contexte de seguretat JBoss no s'ha injectat: {}", e.getMessage());
        }
    }

    private void clearJBossSecurityContext() {
        try {
            Class<?> scAssocClass = Class.forName("org.jboss.security.SecurityContextAssociation");
            Class<?> scInterface  = Class.forName("org.jboss.security.SecurityContext");
            scAssocClass.getMethod("setSecurityContext", scInterface).invoke(null, (Object) null);
        } catch (Exception e) {
            log.debug("No s'ha pogut netejar el contexte de seguretat JBoss: {}", e.getMessage());
        }
    }

    private static class FakeAuthRequestWrapper extends HttpServletRequestWrapper {
        FakeAuthRequestWrapper(HttpServletRequest request) {
            super(request);
        }
        @Override public Principal getUserPrincipal() { return () -> FAKE_USER; }
        @Override public boolean isUserInRole(String role) { return FAKE_ROLES.contains(role); }
        @Override public String getRemoteUser() { return FAKE_USER; }
    }

    @Override public void init(FilterConfig filterConfig) {}
    @Override public void destroy() {}
}
