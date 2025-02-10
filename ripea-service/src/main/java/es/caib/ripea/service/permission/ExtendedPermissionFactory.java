/**
 *
 */
package es.caib.ripea.service.permission;

import org.springframework.security.acls.domain.DefaultPermissionFactory;
import org.springframework.security.acls.model.Permission;

/**
 * Factory per a la instanciació de permisos
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExtendedPermissionFactory extends DefaultPermissionFactory {

	public ExtendedPermissionFactory() {
		registerPublicPermissions(ExtendedPermission.class);
	}


	public ExtendedPermissionFactory(Class<? extends Permission> permissionClass) {
		registerPublicPermissions(permissionClass);
	}

}
