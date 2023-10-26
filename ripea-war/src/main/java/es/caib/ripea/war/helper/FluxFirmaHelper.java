/**
 * 
 */
package es.caib.ripea.war.helper;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.service.AplicacioService;

/**
 * Utilitat per a gestionar la creació de fluxos a nivell d'usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class FluxFirmaHelper {

	private static final String SESSION_CREACIO_FLUX_USUARI = "FluxFirmaHelper.isCreacioFluxUsuariActiu";
	
	public static Boolean isCreacioFluxUsuariActiu(HttpServletRequest request) {
		return (Boolean)request.getSession().getAttribute(SESSION_CREACIO_FLUX_USUARI);
	}
	
	public static void setCreacioFluxUsuariActiu(
			HttpServletRequest request,
			AplicacioService aplicacioService) {
		boolean isCreacioFluxUsuariActiu = Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.plugin.portafirmes.fluxos.usuaris"));
		request.getSession().setAttribute(
				SESSION_CREACIO_FLUX_USUARI,
				isCreacioFluxUsuariActiu);
	}

}
