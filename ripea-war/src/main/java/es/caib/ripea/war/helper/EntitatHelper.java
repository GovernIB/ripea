/**
 * 
 */
package es.caib.ripea.war.helper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
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
	
	private static final String REQUEST_PARAMETER_CANVI_GESTOR_ACTUAL = "canviOrganGestor";
	private static final String SESSION_ATTRIBUTE_ORGAN_GESTOR_ACTUAL = "EntitatHelper.organGestorActual";
	private static final String ORGANS_ACCESSIBLES= "organs.accessiblesUsuari";

	public static List<EntitatDto> findEntitatsAccessibles(HttpServletRequest request) {
		return findEntitatsAccessibles(request, null);
	}

	@SuppressWarnings("unchecked")
	public static List<EntitatDto> findEntitatsAccessibles(HttpServletRequest request, EntitatService entitatService) {
		List<EntitatDto> entitats = (List<EntitatDto>)request.getAttribute(REQUEST_ATTRIBUTE_ENTITATS);
		if (entitats == null && entitatService != null) {
			entitats = entitatService.findAccessiblesUsuariActual();
			request.setAttribute(REQUEST_ATTRIBUTE_ENTITATS, entitats);
		}
		return entitats;
	}

	public static void processarCanviEntitats(HttpServletRequest request, EntitatService entitatService) {
		String canviEntitat = request.getParameter(REQUEST_PARAMETER_CANVI_ENTITAT);
		if (canviEntitat != null && canviEntitat.length() > 0) {
			LOGGER.debug("Processant canvi entitat (id=" + canviEntitat + ")");
			try {
				Long canviEntitatId = new Long(canviEntitat);
				List<EntitatDto> entitats = findEntitatsAccessibles(request, entitatService);
				for (EntitatDto entitat : entitats) {
					if (canviEntitatId.equals(entitat.getId())) {
						canviEntitatActual(request, entitat, entitatService);
					}
				}
			} catch (NumberFormatException ignored) {
			}
		}
	}

	public static EntitatDto getEntitatActual(HttpServletRequest request) {
		return getEntitatActual(request, null);
	}

	public static EntitatDto getEntitatActual(HttpServletRequest request, EntitatService entitatService) {
		EntitatDto entitatActual = (EntitatDto)request.getSession().getAttribute(SESSION_ATTRIBUTE_ENTITAT_ACTUAL);
		if (entitatActual == null) {
			List<EntitatDto> entitats = findEntitatsAccessibles(request, entitatService);
			if (entitats != null && entitats.size() > 0) {
				entitatActual = entitats.get(0);
				canviEntitatActual(request, entitatActual, entitatService);
			}
		}
		return entitatActual;
	}

	public static String getRequestParameterCanviEntitat() {
		return REQUEST_PARAMETER_CANVI_ENTITAT;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isUsuariActualTeOrgans(HttpServletRequest request) {

		List<OrganGestorDto> organs = (List<OrganGestorDto>) request.getAttribute(ORGANS_ACCESSIBLES);
		if (organs != null && !organs.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	private static void canviEntitatActual(
			HttpServletRequest request,
			EntitatDto entitatActual,
			EntitatService entitatService) {
		request.getSession().setAttribute(SESSION_ATTRIBUTE_ENTITAT_ACTUAL, entitatActual);
		ExpedientHelper.resetAccesUsuariExpedients(request);
	}

	////
	// ORGANS GESTORS
	////
	@SuppressWarnings("unchecked")
	public static List<OrganGestorDto> findOrganGestorsAccessibles(HttpServletRequest request) {
		List<OrganGestorDto> organs = (List<OrganGestorDto>) request.getAttribute(ORGANS_ACCESSIBLES);
		if (organs != null && !organs.isEmpty()) {
			return organs;
		} else {
			return new ArrayList<OrganGestorDto>();
		}
	}
	
	public static OrganGestorDto getOrganGestorActual(HttpServletRequest request) {
		OrganGestorDto organGestorActual = (OrganGestorDto)request.getSession().getAttribute(SESSION_ATTRIBUTE_ORGAN_GESTOR_ACTUAL);
		if (organGestorActual == null) {
			List<OrganGestorDto> organsGestors = findOrganGestorsAccessibles(request);
			if (organsGestors != null && organsGestors.size() > 0) {
				organGestorActual = organsGestors.get(0);
				setOrganGestorActual(request, organGestorActual);
			}
		}
		return organGestorActual;
	}
	
	public static Long getOrganGestorActualId(HttpServletRequest request) {
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		if (organGestorActual != null) {
			return organGestorActual.getId();
		} else {
			return null;
		}
	}
	
	public static void processarCanviOrganGestor(HttpServletRequest request) {
		String canviOrganGestor = request.getParameter(REQUEST_PARAMETER_CANVI_GESTOR_ACTUAL);
		if (canviOrganGestor != null && canviOrganGestor.length() > 0) {
			LOGGER.debug("Processant canvi òrgan gestor (id=" + canviOrganGestor + ")");
			try {
				Long canviOrganGestorId = new Long(canviOrganGestor);
				List<OrganGestorDto> OrgansGestors = findOrganGestorsAccessibles(request);
				for (OrganGestorDto organGestor : OrgansGestors) {
					if (canviOrganGestorId.equals(organGestor.getId())) {
						setOrganGestorActual(request, organGestor);
					}
				}
			} catch (NumberFormatException ignored) {
			}
		}
	}
	
	public static String getRequestParameterCanviOrganGestor() {
		return REQUEST_PARAMETER_CANVI_GESTOR_ACTUAL;
	}
	
	private static void setOrganGestorActual(
			HttpServletRequest request,
			OrganGestorDto organGestorActual) {
		request.getSession().setAttribute(SESSION_ATTRIBUTE_ORGAN_GESTOR_ACTUAL, organGestorActual);
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EntitatHelper.class);

}
