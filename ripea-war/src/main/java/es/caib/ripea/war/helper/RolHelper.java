/**
 * 
 */
package es.caib.ripea.war.helper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.ripea.core.api.dto.EntitatDto;

/**
 * Utilitat per a gestionar el canvi de rol de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RolHelper {

	private static final String ROLE_SUPER = "IPA_SUPER";
	private static final String ROLE_ADMIN = "IPA_ADMIN";
	private static final String ROLE_ADMIN_ORGAN = "IPA_ORGAN_ADMIN";
	private static final String ROLE_USER = "tothom";

	private static final String REQUEST_PARAMETER_CANVI_ROL = "canviRol";
	private static final String SESSION_ATTRIBUTE_ROL_ACTUAL = "RolHelper.rol.actual";

	public static void processarCanviRols(HttpServletRequest request) {
		String canviRol = request.getParameter(REQUEST_PARAMETER_CANVI_ROL);
		if (canviRol != null && canviRol.length() > 0) {
			LOGGER.debug("Processant canvi rol (rol=" + canviRol + ")");
			if (ROLE_ADMIN_ORGAN.equals(canviRol) && isUsuariActualTeOrgans(request)) {
				request.getSession().setAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL, canviRol);
			} else if (request.isUserInRole(canviRol)) {
				request.getSession().setAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL, canviRol);
			}
		}
	}

	public static String getRolActual(HttpServletRequest request) {
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		List<String> rolsDisponibles = getRolsUsuariActual(request);
		if (rolActual == null || !rolsDisponibles.contains(rolActual)) {
			if (request.isUserInRole(ROLE_USER) && rolsDisponibles.contains(ROLE_USER)) {
				rolActual = ROLE_USER;
			} else if (request.isUserInRole(ROLE_ADMIN) && rolsDisponibles.contains(ROLE_ADMIN)) {
				rolActual = ROLE_ADMIN;
			} else if (isUsuariActualTeOrgans(request) && rolsDisponibles.contains(ROLE_ADMIN_ORGAN)) {
				rolActual = ROLE_ADMIN_ORGAN;
			} else if (request.isUserInRole(ROLE_SUPER) && rolsDisponibles.contains(ROLE_SUPER)) {
				rolActual = ROLE_SUPER;
			}
			if (rolActual != null) {
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_ROL_ACTUAL,
						rolActual);
			}
		}
		LOGGER.debug("Obtenint rol actual (rol=" + rolActual + ")");
		return rolActual;
	}

	public static boolean isRolActualSuperusuari(HttpServletRequest request) {
		return ROLE_SUPER.equals(getRolActual(request));
	}
	public static boolean isRolActualAdministrador(HttpServletRequest request) {
		return ROLE_ADMIN.equals(getRolActual(request));
	}
	public static boolean isRolActualAdministradorOrgan(HttpServletRequest request) {
		return ROLE_ADMIN_ORGAN.equals(getRolActual(request));
	}

	public static boolean isRolActualUsuari(HttpServletRequest request) {
		return ROLE_USER.equals(getRolActual(request));
	}

	public static List<String> getRolsUsuariActual(HttpServletRequest request) {
		LOGGER.debug("Obtenint rols disponibles per a l'usuari actual");
		List<String> rols = new ArrayList<String>();
		if (request.isUserInRole(ROLE_SUPER)) {
			rols.add(ROLE_SUPER);
		}
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		if (entitatActual != null) {
			if (entitatActual.isUsuariActualAdministration() && request.isUserInRole(ROLE_ADMIN)) {
				rols.add(ROLE_ADMIN);
			}
			if (entitatActual.isUsuariActualRead() && request.isUserInRole(ROLE_ADMIN_ORGAN) && isUsuariActualTeOrgans(request)) {
				rols.add(ROLE_ADMIN_ORGAN);
			}
			if (entitatActual.isUsuariActualRead() && request.isUserInRole(ROLE_USER)) {
				rols.add(ROLE_USER);
			}
			
		}
		return rols;
	}

	public static void esborrarRolActual(HttpServletRequest request) {
		request.getSession().removeAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
	}

	public static String getRequestParameterCanviRol() {
		return REQUEST_PARAMETER_CANVI_ROL;
	}

	private static boolean isUsuariActualTeOrgans(HttpServletRequest request) {
		return EntitatHelper.isUsuariActualTeOrgans(request);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RolHelper.class);

}
