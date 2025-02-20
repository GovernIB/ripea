package es.caib.ripea.service.base.helper;

import es.caib.ripea.service.intf.config.BaseConfig;
import org.springframework.lang.Nullable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Mètodes per a la comprovació de permisos.
 *
 * @author Límit Tecnologies
 */
@Component
public class PermissionHelper {

	/**
	 * Comprova els permisos per a accedir a un recurs.
	 *
	 * @param auth
	 *            l'objecte d'autenticació que s'utilitzarà per a comprovar els permisos.
	 * @param targetId
	 *            l'id del recurs (pot ser null).
	 * @param targetType
	 *            la classe del recurs.
	 * @param permission
	 *            el permís a comprovar (si és null voldrà dir que es comprovarà qualsevol permís).
	 * @return true si l'usuari actual te accés al recurs o false en cas contrari.
	 */
	public boolean checkResourcePermission(
			Authentication auth,
			@Nullable Serializable targetId,
			String targetType,
			@Nullable BasePermission permission) {
		// TODO tot el tema de controlar permisos de RIPEA
		boolean hasRoleConsulta = isCurrentUserInRole(auth, BaseConfig.ROLE_ADMIN);
		boolean hasRoleAdmin = isCurrentUserInRole(auth, BaseConfig.ROLE_ADMIN);
		boolean readPermissionAllowed = isReadOperation(permission) && hasRoleConsulta;
		boolean writePermissionAllowed = isWriteOperation(permission) && hasRoleAdmin;
		return readPermissionAllowed || writePermissionAllowed;
	}

	/**
	 * Comprova els permisos per a accedir a un recurs.
	 *
	 * @param targetId
	 *            l'id del recurs (pot ser null).
	 * @param targetType
	 *            la classe del recurs.
	 * @param permission
	 *            el permís a comprovar (si és null voldrà dir que es comprovarà qualsevol permís).
	 * @return true si l'usuari actual te accés al recurs o false en cas contrari.
	 */
	public boolean checkResourcePermission(
			@Nullable Serializable targetId,
			String targetType,
			@Nullable BasePermission permission) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return checkResourcePermission(
				auth,
				targetId,
				targetType,
				permission);
	}

	private boolean isCurrentUserInRole(Authentication auth, String role) {
		boolean isInRole = false;
		for (GrantedAuthority ga: auth.getAuthorities()) {
			if (ga != null && ga.getAuthority().equals(role)) {
				isInRole = true;
				break;
			}
		}
		return isInRole;
	}

	private boolean isReadOperation(BasePermission permission) {
		if (permission != null) {
			return BasePermission.READ.equals(permission);
		} else {
			// Si el permís es null s'assumeix que s'està verificant l'accés al recurs per a qualsevol permís
			return true;
		}
	}

	private boolean isWriteOperation(BasePermission permission) {
		if (permission != null) {
			return BasePermission.WRITE.equals(permission) ||
					BasePermission.CREATE.equals(permission) ||
					BasePermission.DELETE.equals(permission);
		} else {
			// Si el permís es null s'assumeix que s'està verificant l'accés al recurs per a qualsevol permís
			return true;
		}
	}

}
