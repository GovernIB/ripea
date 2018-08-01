/**
 * 
 */
package es.caib.ripea.war.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.authority.mapping.MappableAttributesRetriever;
import org.springframework.security.web.authentication.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;

/**
 * Proveeix els detalls de preautenticació per a Spring Security
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RolesBasedPreAuthenticatedWebAuthenticationDetailsSource extends J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource {

	private boolean rolesConfigured = false;

	MappableAttributesRetriever mappableAttributesRetriever;

	public RolesBasedPreAuthenticatedWebAuthenticationDetailsSource() {
		super();
	}

	@Override
	protected Collection<String> getUserRoles(HttpServletRequest request) {
		if (!rolesConfigured) {
			j2eeMappableRoles = mappableAttributesRetriever.getMappableAttributes();
			rolesConfigured = true;
		}
		Set<String> j2eeUserRolesList = new HashSet<String>();
        for (String role: j2eeMappableRoles) {
        	if (request.isUserInRole(role)) {
            	j2eeUserRolesList.add(role);
            }
        }
        return j2eeUserRolesList;
    }
	@Override
	public void setMappableRolesRetriever(MappableAttributesRetriever mappableAttributesRetriever) {
		this.mappableAttributesRetriever = mappableAttributesRetriever;
		this.j2eeMappableRoles = new HashSet<String>();
	}

}
