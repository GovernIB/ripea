/**
 * 
 */
package es.caib.ripea.war.helper;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.service.EntitatService;

/**
 * Utilitat per a gestionar les entitats de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatHelper {

	private static final String REQUEST_PARAMETER_CANVI_ENTITAT = "canviEntitat";
	private static final String REQUEST_ATTRIBUTE_ENTITATS = "EntitatHelper.entitats";
	private static final String SESSION_ATTRIBUTE_ENTITAT_ACTUAL = "EntitatHelper.entitatActual";



	public static List<EntitatDto> findEntitatsAccessibles(
			HttpServletRequest request) {
		return findEntitatsAccessibles(request, null);
	}
	@SuppressWarnings("unchecked")
	public static List<EntitatDto> findEntitatsAccessibles(
			HttpServletRequest request,
			EntitatService entitatService) {
		List<EntitatDto> entitats = (List<EntitatDto>)request.getAttribute(
				REQUEST_ATTRIBUTE_ENTITATS);
		if (entitats == null && entitatService != null) {
			System.out.println(">>> " + request.getRequestURI() + ", " + request.getUserPrincipal());
			entitats = entitatService.findAccessiblesUsuariActual();
			request.setAttribute(REQUEST_ATTRIBUTE_ENTITATS, entitats);
		}
		return entitats;
	}
	public static void processarCanviEntitats(
			HttpServletRequest request,
			EntitatService entitatService) {
		String canviEntitat = request.getParameter(REQUEST_PARAMETER_CANVI_ENTITAT);
		if (canviEntitat != null && canviEntitat.length() > 0) {
			LOGGER.debug("Processant canvi entitat (id=" + canviEntitat + ")");
			try {
				Long canviEntitatId = new Long(canviEntitat);
				List<EntitatDto> entitats = findEntitatsAccessibles(request, entitatService);
				for (EntitatDto entitat: entitats) {
					if (canviEntitatId.equals(entitat.getId())) {
						canviEntitatActual(request, entitat);
					}
				}
			} catch (NumberFormatException ignored) {
			}
		}
	}

	public static EntitatDto getEntitatActual(
			HttpServletRequest request) {
		return getEntitatActual(request, null);
	}
	public static EntitatDto getEntitatActual(
			HttpServletRequest request,
			EntitatService entitatService) {
		EntitatDto entitatActual = (EntitatDto)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ENTITAT_ACTUAL);
		if (entitatActual == null) {
			List<EntitatDto> entitats = findEntitatsAccessibles(request, entitatService);
			if (entitats != null && entitats.size() > 0) {
				entitatActual = entitats.get(0);
				canviEntitatActual(request, entitatActual);
			}
		}
		return entitatActual;
	}

	public static String getRequestParameterCanviEntitat() {
		return REQUEST_PARAMETER_CANVI_ENTITAT;
	}



	private static void canviEntitatActual(
			HttpServletRequest request,
			EntitatDto entitatActual) {
		request.getSession().setAttribute(
				SESSION_ATTRIBUTE_ENTITAT_ACTUAL,
				entitatActual);
		ExpedientHelper.resetAccesUsuariExpedients(request);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(EntitatHelper.class);

}
