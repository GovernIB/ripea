/**
 * 
 */
package es.caib.ripea.war.helper;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.core.api.service.OrganGestorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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
	private static final String SESSION_ATTRIBUTE_ORGAN_GESTOR_ACTUAL = "EntitatHelper.organGestorActual"; // current organ chosen by administrador d'organ
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

	public static void processarCanviEntitats(HttpServletRequest request, EntitatService entitatService, AplicacioService aplicacioService) {
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
			String usuariCodi = SecurityContextHolder.getContext().getAuthentication() != null ? SecurityContextHolder.getContext().getAuthentication().getName() : null;
			if (usuariCodi != null) {
				aplicacioService.evictCountAnotacionsPendents(usuariCodi);
				AnotacionsPendentsHelper.resetCounterAnotacionsPendents(request);
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
				UsuariDto usuariActual = (UsuariDto)request.getSession().getAttribute(SessioHelper.SESSION_ATTRIBUTE_USUARI_ACTUAL);
				if (usuariActual != null && usuariActual.getEntitatPerDefecteId() != null) {
					entitatActual = entitatService.findById(usuariActual.getEntitatPerDefecteId());
					// en cas que s'hagin eliminat els permisos sobre la entitat per defecte, l'esborram
					if (!entitats.contains(entitatActual)) {
						usuariActual.setEntitatPerDefecteId(null);
						entitatService.removeEntitatPerDefecteUsuari(usuariActual.getCodi());
						entitatActual = entitats.get(0);
					}
				} else {
					entitatActual = entitats.get(0);
				}
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

	@SuppressWarnings("unchecked")
	public static List<OrganGestorDto> findOrganGestorsAccessibles(HttpServletRequest request) {
		List<OrganGestorDto> organs = (List<OrganGestorDto>) request.getAttribute(ORGANS_ACCESSIBLES);
		if (organs != null && !organs.isEmpty()) {
			return organs;
		} else {
			return new ArrayList<OrganGestorDto>();
		}
	}
	
	public static void findOrganismesEntitatAmbPermisCache(HttpServletRequest request, OrganGestorService organGestorService) {
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		if (organGestorService != null && entitatActual != null) {
			List<OrganGestorDto> organs = new ArrayList<OrganGestorDto>();
			if (RolHelper.isRolActualDissenyadorOrgan(request)) {
				organs = organGestorService.findOrganismesEntitatAmbPermisDissenyCache(entitatActual.getId());
			} else {
				organs = organGestorService.findOrganismesEntitatAmbPermisCache(entitatActual.getId());
			}
			request.setAttribute(ORGANS_ACCESSIBLES, organs);
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
	
	public static void processarCanviOrganGestor(HttpServletRequest request, AplicacioService aplicacioService) {
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
			String usuariCodi = SecurityContextHolder.getContext().getAuthentication() != null ? SecurityContextHolder.getContext().getAuthentication().getName() : null;
			if (usuariCodi != null) {
				aplicacioService.evictCountAnotacionsPendents(usuariCodi);
				AnotacionsPendentsHelper.resetCounterAnotacionsPendents(request);
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
