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
import es.caib.ripea.core.api.service.MetaExpedientService;

/**
 * Utilitat per a gestionar el canvi de rol de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RolHelper {

	private static final String ROLE_SUPER = "IPA_SUPER";
	private static final String ROLE_ADMIN = "IPA_ADMIN";
	private static final String ROLE_ADMIN_ORGAN = "IPA_ADMIN_ORGAN";
	private static final String ROLE_USER = "tothom";

	private static final String REQUEST_PARAMETER_CANVI_ROL = "canviRol";
	private static final String SESSION_ATTRIBUTE_ROLS = "RolHelper.rols";
	private static final String SESSION_ATTRIBUTE_ROL_ACTUAL = "RolHelper.rol.actual";

	public static void initialize(HttpServletRequest request, MetaExpedientService metaExpedientService) {
		String rolActual = (String) request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		if (rolActual == null) {
			request.getSession().setAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL, ROLE_USER);
		}

		List<String> rols = getRolsUsuariActual(request);
		if (rols == null) {
			rols = getRolsUsuariActual(request, metaExpedientService);
			request.getSession().setAttribute(SESSION_ATTRIBUTE_ROLS, rols);
		}
	}

	public static void buidarSessio(HttpServletRequest request) {
		request.getSession().removeAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		request.getSession().removeAttribute(SESSION_ATTRIBUTE_ROLS);
	}

	public static void processarCanviRols(HttpServletRequest request) {
		String canviRol = request.getParameter(REQUEST_PARAMETER_CANVI_ROL);
		if (canviRol != null && canviRol.length() > 0) {
			LOGGER.debug("Processant canvi rol (rol=" + canviRol + ")");
			if (ROLE_ADMIN_ORGAN.equals(canviRol)) {
				// TODO
				request.getSession().setAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL, canviRol);
			} else if (request.isUserInRole(canviRol)) {
				request.getSession().setAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL, canviRol);
			}
		}
	}

	public static String getRolActual(HttpServletRequest request) {
		return (String) request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
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

	@SuppressWarnings("unchecked")
	public static List<String> getRolsUsuariActual(HttpServletRequest request) {
		return (List<String>) request.getSession().getAttribute(SESSION_ATTRIBUTE_ROLS);
	}

	public static void esborrarRolActual(HttpServletRequest request) {
		request.getSession().removeAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
	}

	public static String getRequestParameterCanviRol() {
		return REQUEST_PARAMETER_CANVI_ROL;
	}

	private static List<String> getRolsUsuariActual(HttpServletRequest request,
	                                                MetaExpedientService metaExpedientService) {
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
			if (metaExpedientService != null) {
				if (metaExpedientService.hasAnyWithOrganGestor(entitatActual.getId())) {
					rols.add(ROLE_ADMIN_ORGAN);
				}
			}
			if (entitatActual.isUsuariActualRead() && request.isUserInRole(ROLE_USER)) {
				rols.add(ROLE_USER);
			}

		}

		return rols;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RolHelper.class);

}
