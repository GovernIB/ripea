package es.caib.ripea.back.helper;

import es.caib.ripea.service.intf.service.ExpedientTascaService;

import javax.servlet.http.HttpServletRequest;

public class TasquesPendentsHelper {


	private static final String REQUEST_PARAMETER_TASQUES_PENDENTS_COUNT = "TasquesPendentsHelper.countTasquesPendents";
	
	public static Long countTasquesPendents(
			HttpServletRequest request,
			ExpedientTascaService expedientTascaService) {
		Long count = (Long)request.getAttribute(REQUEST_PARAMETER_TASQUES_PENDENTS_COUNT);
		if (count == null && !RequestHelper.isError(request) && expedientTascaService != null && RolHelper.isRolActualUsuari(request)) {
			count = new Long(expedientTascaService.countTasquesPendents());
			request.setAttribute(REQUEST_PARAMETER_TASQUES_PENDENTS_COUNT, count);
		}
		return count;
	}

	public static Long countTasquesPendents(HttpServletRequest request) {
		return (Long)request.getAttribute(REQUEST_PARAMETER_TASQUES_PENDENTS_COUNT);
	}
	


	
}
