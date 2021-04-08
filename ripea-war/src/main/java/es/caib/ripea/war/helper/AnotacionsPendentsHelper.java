package es.caib.ripea.war.helper;

import javax.servlet.http.HttpServletRequest;

import es.caib.ripea.core.api.service.ExpedientPeticioService;

public class AnotacionsPendentsHelper {

	private static final String REQUEST_PARAMETER_ANOTACIONS_PENDENTS_COUNT = "AnotacionsPendentsHelper.countAnotacionsPendents";
	
	public static Long countAnotacionsPendents(
			HttpServletRequest request,
			ExpedientPeticioService expedientPeticioService) {
		Long count = (Long)request.getAttribute(REQUEST_PARAMETER_ANOTACIONS_PENDENTS_COUNT);
		if (count == null && !RequestHelper.isError(request) && expedientPeticioService != null && RolHelper.isRolActualUsuari(request)) {
			count = new Long(expedientPeticioService.countAnotacionsPendents());
			request.setAttribute(REQUEST_PARAMETER_ANOTACIONS_PENDENTS_COUNT, count);
		}
		return count;
	}

	public static Long countAnotacionsPendents(HttpServletRequest request) {
		return (Long)request.getAttribute(REQUEST_PARAMETER_ANOTACIONS_PENDENTS_COUNT);
	}
	
}
