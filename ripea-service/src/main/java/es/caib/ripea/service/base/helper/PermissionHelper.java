package es.caib.ripea.service.base.helper;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

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
	 * @param targetId 	 l'id del recurs (pot ser null).
	 * @param targetType la classe del recurs.
	 * @param permission el permís a comprovar (si és null voldrà dir que es comprovarà qualsevol permís).
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

	/**
	 * Comprova els permisos per a accedir a un recurs.
	 *
	 * @param auth 		 l'objecte d'autenticació que s'utilitzarà per a comprovar els permisos.
	 * @param targetId 	 l'id del recurs (pot ser null).
	 * @param targetType la classe del recurs.
	 * @param permission el permís a comprovar (si és null voldrà dir que es comprovarà qualsevol permís).
	 * @return true si l'usuari actual te accés al recurs o false en cas contrari.
	 */
	public boolean checkResourcePermission(
			Authentication auth,
			@Nullable Serializable targetId,
			String targetType,
			@Nullable BasePermission permission) {

		// Crear una instància de Permissions a partir dels roles de l'usuari
		Permissions userPermissions = Permissions.fromAuthentication(auth);
		// Determina si es tenen permisos
		return hasPermission(userPermissions, permission);

	}

	/* Determina si els permisos de l'usuari inclouen el permís requerit.
	 * - userPermissions: permisos de l'usuari.
	 * - permission: permís requerit.
	 */
	private boolean hasPermission(Permissions userPermissions, @Nullable BasePermission permission) {
		// TODO: tot el tema de controlar permisos de RIPEA
		boolean readPermissionAllowed = isReadOperation(permission) && userPermissions.isConsulta();
		boolean writePermissionAllowed = isWriteOperation(permission) && userPermissions.isAdmin();

		return readPermissionAllowed || writePermissionAllowed;
	}


	private boolean isReadOperation(BasePermission permission) {
		// Si el permís es null s'assumeix que s'està verificant l'accés al recurs per a qualsevol permís
		return permission == null || BasePermission.READ.equals(permission);
	}

	private boolean isWriteOperation(BasePermission permission) {
		// Si el permís es null s'assumeix que s'està verificant l'accés al recurs per a qualsevol permís
		return permission == null ||
				BasePermission.WRITE.equals(permission) ||
				BasePermission.CREATE.equals(permission) ||
				BasePermission.DELETE.equals(permission);
	}

	/**
	 * Classe auxiliar per gestionar els permisos de l'usuari.
	 */
	@Getter
	@Builder
	public static class Permissions {
		private final boolean consulta;
		private final boolean admin;
		private final boolean superAdmin;
		private final boolean disseny;
		private final boolean organ;
		private final boolean revisio;
		private final boolean distribucio;
		private final boolean apiHist;

		/**
		 * Genera una instància de Permissions a partir de l'Authentication.
		 *
		 * @param auth L'objecte d'autenticació.
		 * @return Una instància de Permissions.
		 */
		public static Permissions fromAuthentication(Authentication auth) {
			Set<String> roles = auth.getAuthorities().stream()
					.map(GrantedAuthority::getAuthority)
					.collect(Collectors.toSet());

			// Utilitzar el builder de Lombok per construir l'objecte
			return Permissions.builder()
					.consulta(roles.contains(BaseConfig.ROLE_USER) || roles.contains(BaseConfig.ROLE_ADMIN))
					.admin(roles.contains(BaseConfig.ROLE_ADMIN))
					.superAdmin(roles.contains(BaseConfig.ROLE_SUPER))
					.disseny(roles.contains(BaseConfig.ROLE_DISSENY))
					.organ(roles.contains(BaseConfig.ROLE_ORGAN_ADMIN))
					.revisio(roles.contains(BaseConfig.ROLE_REVISIO))
					.distribucio(roles.contains(BaseConfig.ROLE_BSTWS))
					.apiHist(roles.contains(BaseConfig.ROLE_API_HIST))
					.build();
		}
	}


}
