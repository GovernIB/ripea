/**
 * 
 */
package es.caib.ripea.war.helper;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.service.MetaExpedientService;

/**
 * Utilitat per a gestionar les expedients i metaexpedients de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientHelper {

	private static final String REQUEST_PARAMETER_ACCES_EXPEDIENTS = "ExpedientHelper.teAccesExpedients";
	public static final String SESSION_ATTRIBUTE_ROL_ACTUAL = "RolHelper.rol.actual";
	private static final String REQUEST_PARAMETER_STATISTICS_EXPEDIENTS = "ExpedientHelper.teAccesEstadistiques";
	
	public static void accesUsuariExpedients(
			HttpServletRequest request,
			MetaExpedientService metaExpedientService) {
		request.setAttribute(
				REQUEST_PARAMETER_ACCES_EXPEDIENTS,
				teAccesExpedients(request, metaExpedientService));
	}
	
	public static void accesUsuariEstadistiques(
			HttpServletRequest request,
			MetaExpedientService metaExpedientService) {
		request.setAttribute(
				REQUEST_PARAMETER_STATISTICS_EXPEDIENTS,
				teAccesEstadistiques(request, metaExpedientService));
	}
	public static Boolean teAccesExpedients(
			HttpServletRequest request) {
		return teAccesExpedients(request, null);
	}
	
	public static Boolean teAccesEstadistiques(
			HttpServletRequest request) {
		return teAccesEstadistiques(request, null);
	}
	public static Boolean teAccesExpedients(
			HttpServletRequest request,
			MetaExpedientService metaExpedientService) {
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		
		Boolean teAcces = (Boolean)request.getSession().getAttribute(REQUEST_PARAMETER_ACCES_EXPEDIENTS);
		if (RolHelper.isRolActualUsuari(request) && teAcces == null && metaExpedientService != null) {
			teAcces = new Boolean(false);
			EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
			if (entitatActual != null) {
				List<MetaExpedientDto> expedientsAccessibles =  metaExpedientService.findActius(entitatActual.getId(), null, rolActual, false, null);
				teAcces = new Boolean(expedientsAccessibles != null && !expedientsAccessibles.isEmpty());
			}
			request.getSession().setAttribute(
					REQUEST_PARAMETER_ACCES_EXPEDIENTS,
					teAcces);
		}
		return teAcces;
	}
	
	public static Boolean teAccesEstadistiques(
			HttpServletRequest request,
			MetaExpedientService metaExpedientService) {
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		
		Boolean teAcces = (Boolean)request.getAttribute(REQUEST_PARAMETER_STATISTICS_EXPEDIENTS);
		if (RolHelper.isRolActualUsuari(request) && teAcces == null && metaExpedientService != null) {
			teAcces = new Boolean(false);
			EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
			if (entitatActual != null) {
				List<MetaExpedientDto> expedientsAccessibles =  metaExpedientService.findActiusAmbEntitatPerConsultaEstadistiques(entitatActual.getId(), null, rolActual);
				teAcces = new Boolean(expedientsAccessibles != null && !expedientsAccessibles.isEmpty());
			}
			request.setAttribute(
					REQUEST_PARAMETER_STATISTICS_EXPEDIENTS,
					teAcces);
		}
		return teAcces;
	}

	public static void resetAccesUsuariExpedients(
			HttpServletRequest request) {
		request.getSession().removeAttribute(REQUEST_PARAMETER_ACCES_EXPEDIENTS);
	}

}
