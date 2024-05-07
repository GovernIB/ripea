package es.caib.ripea.war.helper;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.service.ExpedientPeticioService;

import javax.servlet.http.HttpServletRequest;

public class AnotacionsPendentsHelper {

	private static final String REQUEST_PARAMETER_ANOTACIONS_PENDENTS_COUNT = "AnotacionsPendentsHelper.countAnotacionsPendents";
	public static final String SESSION_ATTRIBUTE_ROL_ACTUAL = "RolHelper.rol.actual";
	
	public static Long countAnotacionsPendents(
			HttpServletRequest request,
			ExpedientPeticioService expedientPeticioService) {
		Long count = (Long)request.getAttribute(REQUEST_PARAMETER_ANOTACIONS_PENDENTS_COUNT);
		if (count == null && !RequestHelper.isError(request) && expedientPeticioService != null && (RolHelper.isRolActualUsuari(request) || RolHelper.isRolActualAdministrador(request) || RolHelper.isRolActualAdministradorOrgan(request))) {
			EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
			String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
			if (entitatActual != null)
				count = new Long(expedientPeticioService.countAnotacionsPendents(entitatActual.getId(), rolActual, EntitatHelper.getOrganGestorActualId(request)));
			request.setAttribute(REQUEST_PARAMETER_ANOTACIONS_PENDENTS_COUNT, count);
		}
		return count;
	}

	public static Long countAnotacionsPendents(HttpServletRequest request) {
		return (Long)request.getAttribute(REQUEST_PARAMETER_ANOTACIONS_PENDENTS_COUNT);
	}
	
}
