/**
 * 
 */
package es.caib.ripea.war.helper;

import javax.servlet.http.HttpServletRequest;

import es.caib.ripea.core.api.service.MetaExpedientService;

public class MetaExpedientHelper {

	private static final String SESSION_ATTRIBUTE_REVISIO = "MetaExpedientHelper.revisio.activa";
	
	
	public static void setRevisioActiva(HttpServletRequest request, MetaExpedientService metaExpedientService) {
		Boolean revisioActiva = (Boolean) request.getSession().getAttribute(SESSION_ATTRIBUTE_REVISIO);
		if (revisioActiva == null) {
			revisioActiva = metaExpedientService.isRevisioActiva();
		}
		if (revisioActiva != null) {
			request.getSession().setAttribute(
					SESSION_ATTRIBUTE_REVISIO,
					revisioActiva);
		}
	}
	

	public static boolean getRevisioActiva(HttpServletRequest request) {

		Boolean revisioActiva = (Boolean) request.getSession().getAttribute(SESSION_ATTRIBUTE_REVISIO);
		if (revisioActiva != null) {
			return revisioActiva;
		} else {
			return true;
		}
	}
	
	public static void resetRevisioActiva(HttpServletRequest request) {

		request.getSession().setAttribute(
				SESSION_ATTRIBUTE_REVISIO,
				null);

	}
	

}
