/**
 * 
 */
package es.caib.ripea.back.helper;

import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.ExpedientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Utilitat per a gestionar la creació de fluxos a nivell d'usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class FluxFirmaHelper {

	private static final String SESSION_CREACIO_FLUX_USUARI = "FluxFirmaHelper.isCreacioFluxUsuariActiu";
	
	@Autowired private AplicacioService aplicacioService;
	
	public Boolean isWsUsuariEntitatActiu(HttpServletRequest request) {
		return Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.PORTAFIB_PLUGIN_USUARISPF_WS));
	}
	
	public static Boolean isCreacioFluxUsuariActiu(HttpServletRequest request) {
		return (Boolean)request.getSession().getAttribute(SESSION_CREACIO_FLUX_USUARI);
	}
	
	public static void setCreacioFluxUsuariActiu(
			HttpServletRequest request,
			AplicacioService aplicacioService) {
		boolean isCreacioFluxUsuariActiu = Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.PERMETRE_USUARIS_CREAR_FLUX_PORTAFIB));
		request.getSession().setAttribute(
				SESSION_CREACIO_FLUX_USUARI,
				isCreacioFluxUsuariActiu);
	}

}
