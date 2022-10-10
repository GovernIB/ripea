/**
 * 
 */
package es.caib.ripea.war.helper;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.service.MetaExpedientService;

import javax.servlet.http.HttpServletRequest;

public class MetaExpedientHelper {

	private static final String SESSION_ATTRIBUTE_REVISIO = "MetaExpedientHelper.revisio.activa";
	private static final String SESSION_ATTRIBUTE_ORGANS_NO_SYNC = "MetaExpedientHelper.organsNoSincronitzats";
	
	
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

//	public static void setOrgansNoSincronitzats(HttpServletRequest request, MetaExpedientService metaExpedientService) {
//		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
//		if (entitatActual != null && metaExpedientService != null)
//			request.getSession().setAttribute(
//					MetaExpedientHelper.SESSION_ATTRIBUTE_ORGANS_NO_SYNC,
//					metaExpedientService.getMetaExpedientsAmbOrganNoSincronitzat(entitatActual.getId()));
//	}

	public static Integer getOrgansNoSincronitzats(HttpServletRequest request) {
		Integer organsNoSincronitzats = (Integer) request.getSession().getAttribute(SESSION_ATTRIBUTE_ORGANS_NO_SYNC);
		return organsNoSincronitzats != null ? organsNoSincronitzats : 0;
	}

}
