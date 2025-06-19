package es.caib.ripea.service.base.helper;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${es.caib.ripea.develope.mode:false}")
	private boolean developmentMode;

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
		return hasPermission(userPermissions, permission, targetType);

	}

	/* Determina si els permisos de l'usuari inclouen el permís requerit.
	 * - userPermissions: permisos de l'usuari.
	 * - permission: permís requerit.
	 */
	private boolean hasPermission(Permissions userPermissions, @Nullable BasePermission permission, String targetType) {
		if (developmentMode) return true;
//		boolean readPermissionAllowed = isReadOperation(permission) && userPermissions.isConsulta();
//		boolean writePermissionAllowed = isWriteOperation(permission) && userPermissions.isAdmin();
//		boolean resourcePermissionAllowed = isResourcePermissionAllowed(targetType, userPermissions);
//		return (readPermissionAllowed || writePermissionAllowed) && resourcePermissionAllowed;
		return isResourcePermissionAllowed(targetType, permission, userPermissions);
	}

	private boolean isResourcePermissionAllowed(String targetType, @Nullable BasePermission permission, Permissions userPermissions) {
		
		//Qualsevol usuari autenticat, ha de poder 
		if (isReadOperation(permission) && userPermissions.isConsulta()) return true;
		
		if (targetType!=null) {
			//Exclusius super admin
			if (targetType.endsWith(".EntitatResource")) { return userPermissions.isSuperAdmin(); }
			if (targetType.endsWith(".PinbalServeiResource")) { return userPermissions.isSuperAdmin(); }
			if (targetType.endsWith(".AvisResource")) { return userPermissions.isSuperAdmin(); }
			//Exclusius administradors
			if (targetType.endsWith(".OrganGestorResource")) { return userPermissions.isAdmin(); }
			if (targetType.endsWith(".GrupResource")) { return userPermissions.isAdmin(); }
			if (targetType.endsWith(".HistoricResource")) { return userPermissions.isAdmin(); }
			if (targetType.endsWith(".MetaExpedientResource")) { return userPermissions.isAdmin(); }
			if (targetType.endsWith(".MetaExpedientOrganGestorResource")) { return userPermissions.isAdmin(); }
			if (targetType.endsWith(".MetaExpedientTascaResource")) { return userPermissions.isAdmin(); }
			if (targetType.endsWith(".MetaExpedientTascaValidacioResource")) { return userPermissions.isAdmin(); }
			if (targetType.endsWith(".DominiResource")) { return userPermissions.isAdmin(); }
			if (targetType.endsWith(".TipusDocumentalResource")) { return userPermissions.isAdmin(); }
			//Modificació de procediments
			if (targetType.endsWith(".MetaDadaResource")) { return userPermissions.isAdmin() || userPermissions.isDisseny() || userPermissions.isRevisio(); }
			if (targetType.endsWith(".MetaDocumentResource")) { return userPermissions.isAdmin() || userPermissions.isDisseny() || userPermissions.isRevisio(); }
			if (targetType.endsWith(".MetaExpedientResource")) { return userPermissions.isAdmin() || userPermissions.isDisseny() || userPermissions.isRevisio(); }
			if (targetType.endsWith(".MetaExpedientTascaResource")) { return userPermissions.isAdmin() || userPermissions.isDisseny() || userPermissions.isRevisio(); }
			//Usuari
			if (targetType.endsWith(".UsuariResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".AlertaResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".CarpetaResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".ContingutLogResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".ContingutMovimentResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".ContingutResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".DadaResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".DocumentEnviamentAnnexResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".DocumentEnviamentInteressatResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".DocumentEnviamentResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".DocumentNotificacioResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".DocumentPortafirmesResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".DocumentPublicacioResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".DocumentResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".ExecucioMassivaContingutResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".ExecucioMassivaResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".ExpedientComentariResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".ExpedientEstatResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".ExpedientPeticioResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".ExpedientResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".ExpedientTascaComentariResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".ExpedientTascaResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".InteressatResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".MetaExpedientOrganGestorResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".MetaExpedientSequenciaResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".MetaNodeResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".NodeResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".RegistreAnnexResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".RegistreInteressatResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".RegistreResource")) { return userPermissions.isConsulta(); }
			if (targetType.endsWith(".ValidacioErrorResource")) { return userPermissions.isConsulta(); }
		}
		//Es retorna false per defecte, quant es crei un nou recurs, s'ha de venir aqui a colocar-lo en el nivell adequat.
		return false;
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
			if (auth == null)
				return Permissions.builder().build();

			Set<String> roles = auth.getAuthorities().stream()
					.map(GrantedAuthority::getAuthority)
					.collect(Collectors.toSet());

			// Utilitzar el builder de Lombok per construir l'objecte
			return Permissions.builder()
					.consulta(auth.isAuthenticated()) //Qualsevol usuari autenticat
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
