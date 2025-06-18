/**
 * 
 */
package es.caib.ripea.back.helper;

import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.UsuariAnotacioDto;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.EventService;
import es.caib.ripea.service.intf.service.OrganGestorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilitat per a gestionar el canvi de rol de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RolHelper {

	private static final String ROLE_SUPER 			= "IPA_SUPER";
	private static final String ROLE_ADMIN 			= "IPA_ADMIN";
	private static final String ROLE_DISSENY 		= "IPA_DISSENY";
	private static final String ROLE_ADMIN_ORGAN 	= "IPA_ORGAN_ADMIN";
	private static final String ROLE_REVISOR 		= "IPA_REVISIO";
	private static final String ROLE_USER 			= "tothom";

	private static final String REQUEST_PARAMETER_CANVI_ROL  = "canviRol";
	public  static final String SESSION_ATTRIBUTE_ROL_ACTUAL = "RolHelper.rol.actual";

	public static void processarCanviRols(
			HttpServletRequest request,
			AplicacioService aplicacioService,
			OrganGestorService organGestorService,
			EventService eventService) {
        processarCanviRols(request, request.getParameter(REQUEST_PARAMETER_CANVI_ROL), aplicacioService, organGestorService, eventService);
    }
	public static void processarCanviRols(
			HttpServletRequest request,
			String canviRol,
			AplicacioService aplicacioService,
			OrganGestorService organGestorService,
			EventService eventService) {
		
		if (canviRol != null && canviRol.length() > 0) {
			
			LOGGER.debug("Processant canvi rol (rol=" + canviRol + ")");

			try {
			
				if (ROLE_ADMIN_ORGAN.equals(canviRol) && isUsuariActualTeOrgans(request)) {
					request.getSession().setAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL, canviRol);
					aplicacioService.setRolUsuariActual(canviRol);
				} else if (request.isUserInRole(canviRol)) {
					request.getSession().setAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL, canviRol);
					aplicacioService.setRolUsuariActual(canviRol);
				}
				
				if (eventService!=null) {
		            Long entitatActualId = aplicacioService.getEntitatActualId();
		            Long organActualId = aplicacioService.getOrganActualId();
		            String rolActualCodi = aplicacioService.getRolActualCodi();
		            UsuariAnotacioDto uaDto = new UsuariAnotacioDto(SecurityContextHolder.getContext().getAuthentication().getName(), rolActualCodi, organActualId, entitatActualId);
					eventService.notifyAnotacionsPendents(List.of(uaDto));
				}
			
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println(ex.getMessage());
			}
			
			AnotacionsPendentsHelper.resetCounterAnotacionsPendents(request);
			
			//Un cop canviat el rol, actualitzam organs gestors si procedeix
			EntitatHelper.findOrganismesEntitatAmbPermisCache(request, organGestorService);
		}
	}
	
	public static void setRolActualFromDb(HttpServletRequest request, AplicacioService aplicacioService) {
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		if (rolActual == null) {
			rolActual = aplicacioService.getUsuariActual().getRolActual();
		}
		if (rolActual != null && !rolActual.isEmpty()) {
			request.getSession().setAttribute(
					SESSION_ATTRIBUTE_ROL_ACTUAL,
					rolActual);
		}
	}
	
	public static String getRolActual(HttpServletRequest request) {
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		List<String> rolsDisponibles = getRolsUsuariActual(request);
		if (rolActual == null || !rolsDisponibles.contains(rolActual)) {
			if (request.isUserInRole(ROLE_USER) && rolsDisponibles.contains(ROLE_USER)) {
				rolActual = ROLE_USER;
			} else if (request.isUserInRole(ROLE_ADMIN) && rolsDisponibles.contains(ROLE_ADMIN)) {
				rolActual = ROLE_ADMIN;
			} else if (isUsuariActualTeOrgans(request) && rolsDisponibles.contains(ROLE_ADMIN_ORGAN)) {
				rolActual = ROLE_ADMIN_ORGAN;
			} else if (request.isUserInRole(ROLE_SUPER) && rolsDisponibles.contains(ROLE_SUPER)) {
				rolActual = ROLE_SUPER;
			} else if (request.isUserInRole(ROLE_REVISOR) && rolsDisponibles.contains(ROLE_REVISOR)) {
				rolActual = ROLE_REVISOR;
			} else if (request.isUserInRole(ROLE_DISSENY) && rolsDisponibles.contains(ROLE_DISSENY)) {
				rolActual = ROLE_DISSENY;				
			}
			/**
			 * Això de actualitzar a la sessió el rol del usuari cada cop que es consulta el rol no está bé.
			 * Aquesta funció es molt utilitzada, i si consultes per un rol que no tens, entra per la segona condició del IF
			 * lo cual provoca que se t'actualitzi el rol en sessió a ROLE_USER (el primer que troba)
			 * Exemple: al canvi de rol s'actualitzen els organs gestors, això depen del rol (si ets admin o dissenyador de organ)
			 * 			Això provoca una cridada a aquets mètode, y una persona que no tenia el rol de disseny, se li assignava user.
			 */
			/*if (rolActual != null) {
				request.getSession().setAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL, rolActual);
			}*/
		}
		
		String resultat = null;
		if (rolActual!=null) {
			resultat = rolActual;
		} else {
			if (rolsDisponibles!=null && rolsDisponibles.size()>0) {
				resultat = rolsDisponibles.get(0);
			} else {
				resultat = ROLE_USER;
			}
		}
		
		LOGGER.debug("Obtenint rol actual (rol=" + resultat + ")");

		return resultat;
	}

	public static boolean isRolActualSuperusuari(HttpServletRequest request) {
		return ROLE_SUPER.equals(getRolActual(request));
	}
	public static boolean isRolActualDissenyadorOrgan(HttpServletRequest request) {
		return ROLE_DISSENY.equals(getRolActual(request));
	}
	public static boolean isRolActualAdministrador(HttpServletRequest request) {
		return ROLE_ADMIN.equals(getRolActual(request));
	}
	public static boolean isRolActualAdministradorOrgan(HttpServletRequest request) {
		return ROLE_ADMIN_ORGAN.equals(getRolActual(request));
	}
	public static boolean isRolActualRevisor(HttpServletRequest request) {
		return ROLE_REVISOR.equals(getRolActual(request));
	}
	public static boolean isRolActualUsuari(HttpServletRequest request) {
		return ROLE_USER.equals(getRolActual(request));
	}
	public static boolean isRolAmbFiltreOrgan(HttpServletRequest request) {
		return ROLE_ADMIN_ORGAN.equals(getRolActual(request)) || ROLE_DISSENY.equals(getRolActual(request));
	}
	
	public static boolean hasRolSuperusuari(HttpServletRequest request) {
		return getRolsUsuariActual(request).contains(ROLE_ADMIN);
	}
	public static boolean hasRolDissenyadorOrgan(HttpServletRequest request) {
		return getRolsUsuariActual(request).contains(ROLE_DISSENY);
	}
	public static boolean hasRolAdministrador(HttpServletRequest request) {
		return getRolsUsuariActual(request).contains(ROLE_ADMIN);
	}
	public static boolean hasRolAdministradorOrgan(HttpServletRequest request) {
		return getRolsUsuariActual(request).contains(ROLE_ADMIN_ORGAN);
	}
	public static boolean hasRolRevisor(HttpServletRequest request) {
		return getRolsUsuariActual(request).contains(ROLE_REVISOR);
	}
	public static boolean hasRolUsuari(HttpServletRequest request) {
		return getRolsUsuariActual(request).contains(ROLE_USER);
	}
	
	public static List<String> getRolsUsuariActual(HttpServletRequest request) {
		LOGGER.debug("Obtenint rols disponibles per a l'usuari actual");
		List<String> rols = new ArrayList<String>();
		if (request.isUserInRole(ROLE_SUPER)) {
			rols.add(ROLE_SUPER);
		}
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		if (entitatActual != null) {
			if (entitatActual.isUsuariActualAdministration() && request.isUserInRole(ROLE_ADMIN)) {
				rols.add(ROLE_ADMIN);
			}
			if (request.isUserInRole(ROLE_ADMIN_ORGAN)) {
				rols.add(ROLE_ADMIN_ORGAN);
			}
			if (entitatActual.isUsuariActualRead() && request.isUserInRole(ROLE_DISSENY)) {
				rols.add(ROLE_DISSENY);
			}			
			if (entitatActual.isUsuariActualRead() && request.isUserInRole(ROLE_USER)) {
				rols.add(ROLE_USER);
			}
			if (entitatActual.isUsuariActualRead() && request.isUserInRole(ROLE_REVISOR) && MetaExpedientHelper.getRevisioActiva(request)) {
				rols.add(ROLE_REVISOR);
			}
		}

		return rols;
	}

	public static void esborrarRolActual(HttpServletRequest request) {
		request.getSession().removeAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
	}

	public static String getRequestParameterCanviRol() {
		return REQUEST_PARAMETER_CANVI_ROL;
	}

	private static boolean isUsuariActualTeOrgans(HttpServletRequest request) {
		return EntitatHelper.isUsuariActualTeOrgans(request);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RolHelper.class);

}
