package es.caib.ripea.war.helper;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;

import es.caib.ripea.core.api.dto.ExpedientTascaDto;
import es.caib.ripea.core.api.service.ExpedientTascaService;

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
