/**
 * 
 */
package es.caib.ripea.back.helper;

import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.OrganGestorDto;
import es.caib.ripea.service.intf.dto.UsuariAnotacioDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.EntitatService;
import es.caib.ripea.service.intf.service.EventService;
import es.caib.ripea.service.intf.service.OrganGestorService;
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
        processarCanviEntitats(request, request.getParameter(REQUEST_PARAMETER_CANVI_ENTITAT), entitatService, aplicacioService, null, null);
    }
	public static void processarCanviEntitats(HttpServletRequest request, String canviEntitat, EntitatService entitatService, AplicacioService aplicacioService) {
        processarCanviEntitats(request, canviEntitat, entitatService, aplicacioService, null, null);
    }
	public static void processarCanviEntitats(HttpServletRequest request, String canviEntitat, EntitatService entitatService, AplicacioService aplicacioService, OrganGestorService organGestorService, EventService eventService) {
		if (canviEntitat != null && canviEntitat.length() > 0) {
			LOGGER.debug("Processant canvi entitat (id=" + canviEntitat + ")");
			boolean entitatCanviada = false;
			try {
				Long canviEntitatId = new Long(canviEntitat);
				List<EntitatDto> entitats = findEntitatsAccessibles(request, entitatService);
				for (EntitatDto entitat : entitats) {
					if (canviEntitatId.equals(entitat.getId())) {
						canviEntitatActual(request, entitat, entitatService);
						entitatCanviada = true;
					}
				}
			} catch (NumberFormatException ignored) {
			}
			String usuariCodi = SecurityContextHolder.getContext().getAuthentication() != null ? SecurityContextHolder.getContext().getAuthentication().getName() : null;
			if (usuariCodi != null) {
				aplicacioService.evictCountAnotacionsPendents(usuariCodi);
				AnotacionsPendentsHelper.resetCounterAnotacionsPendents(request);
				// Igual que en el canvi d'òrgan gestor: el client React només refresca el badge via SSE (el JSP
				// recarrega la pàgina). En canviar d'entitat cal recalcular els òrgans accessibles per resoldre
				// l'òrgan actual de la nova entitat i notificar el nou recompte d'anotacions pendents.
				if (eventService != null && entitatCanviada) {
					try {
						if (organGestorService != null) {
							findOrganismesEntitatAmbPermisCache(request, organGestorService);
						}
						EntitatDto entitatActual = getEntitatActual(request);
						Long entitatActualId = entitatActual != null ? entitatActual.getId() : null;
						String rolActual = (String) request.getSession().getAttribute(RolHelper.SESSION_ATTRIBUTE_ROL_ACTUAL);
						UsuariAnotacioDto uaDto = new UsuariAnotacioDto(usuariCodi, rolActual, getOrganGestorActualId(request), entitatActualId);
						eventService.notifyAnotacionsPendents(List.of(uaDto));
						// El comptador de tasques pendents només es mostra per al rol base (tothom). El seu valor és
						// global per usuari (no depèn d'entitat/òrgan), però el client React no reconnecta el SSE en
						// canviar de dimensió, així que cal reenviar-lo perquè el badge es refresqui. No cal evict de
						// la caché: només la invalida un canvi real de tasca.
						if (RolHelper.isRolActualUsuari(request)) {
							eventService.notifyTasquesPendents(List.of(usuariCodi));
						}
					} catch (Exception ex) {
						LOGGER.error("Error notificant el recompte d'anotacions pendents al canvi d'entitat", ex);
					}
				}
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
		List<OrganGestorDto> organs = (List<OrganGestorDto>) request.getSession().getAttribute(ORGANS_ACCESSIBLES);
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
		List<OrganGestorDto> organs = (List<OrganGestorDto>) request.getSession().getAttribute(ORGANS_ACCESSIBLES);
		if (organs != null && !organs.isEmpty()) {
			return organs;
		} else {
			return new ArrayList<OrganGestorDto>();
		}
	}
	
	public static void findOrganismesEntitatAmbPermisCache(HttpServletRequest request, OrganGestorService organGestorService) {
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		if (organGestorService != null && entitatActual != null) {
			List<OrganGestorDto> organs = findOrganismesEntitatAmbPermisCacheByRol(
					request,
					organGestorService,
					entitatActual);
			request.getSession().setAttribute(ORGANS_ACCESSIBLES, organs);
		}
	}
	
	public static List<OrganGestorDto> findOrganismesEntitatAmbPermisCacheByRol(
			HttpServletRequest request,
			OrganGestorService organGestorService,
			EntitatDto entitatActual) {
		List<OrganGestorDto> organs = new ArrayList<OrganGestorDto>();
		if (RolHelper.isRolActualDissenyadorOrgan(request)) {
			organs = organGestorService.findOrganismesEntitatAmbPermisDissenyCache(entitatActual.getId());
		} else {
			organs = organGestorService.findOrganismesEntitatAmbPermisCache(entitatActual.getId());
		}
		return organs;
	}
	
	public static OrganGestorDto getOrganGestorActual(HttpServletRequest request) {
		OrganGestorDto organGestorActual = (OrganGestorDto)request.getSession().getAttribute(SESSION_ATTRIBUTE_ORGAN_GESTOR_ACTUAL);
		List<OrganGestorDto> organsGestors = findOrganGestorsAccessibles(request);
		if (organGestorActual == null) {			
			if (organsGestors != null && organsGestors.size() > 0) {
				organGestorActual = organsGestors.get(0);
				setOrganGestorActual(request, organGestorActual);
			}
		} else {
			//Encara que el organ gestor no sigui null, comprovar si es accessible actualment
			//S'ha detectat que al canviar de rol es manté en sessió el organ anterior
			if (organsGestors != null && organsGestors.size() > 0) {
				for (OrganGestorDto og: organsGestors) {
					if(og.getId().equals(organGestorActual.getId())) {
						return organGestorActual;
					}
				}
				//Si arribam aqui es que el organ gestor seleccionat per el rol anterior, no esta entre els permesos per el rol actual
				return organsGestors.get(0);
			}
			return null;
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
        processarCanviOrganGestor(request, request.getParameter(REQUEST_PARAMETER_CANVI_GESTOR_ACTUAL), aplicacioService, null);
    }
	public static void processarCanviOrganGestor(HttpServletRequest request, String canviOrganGestor, AplicacioService aplicacioService) {
        processarCanviOrganGestor(request, canviOrganGestor, aplicacioService, null);
    }
	public static void processarCanviOrganGestor(HttpServletRequest request, String canviOrganGestor, AplicacioService aplicacioService, EventService eventService) {
		if (canviOrganGestor != null && canviOrganGestor.length() > 0) {
			LOGGER.debug("Processant canvi òrgan gestor (id=" + canviOrganGestor + ")");
			Long organActualId = null;
			try {
				Long canviOrganGestorId = new Long(canviOrganGestor);
				List<OrganGestorDto> OrgansGestors = findOrganGestorsAccessibles(request);
				for (OrganGestorDto organGestor : OrgansGestors) {
					if (canviOrganGestorId.equals(organGestor.getId())) {
						setOrganGestorActual(request, organGestor);
						organActualId = organGestor.getId();
					}
				}
			} catch (NumberFormatException ignored) {
			}
			String usuariCodi = SecurityContextHolder.getContext().getAuthentication() != null ? SecurityContextHolder.getContext().getAuthentication().getName() : null;
			if (usuariCodi != null) {
				aplicacioService.evictCountAnotacionsPendents(usuariCodi);
				AnotacionsPendentsHelper.resetCounterAnotacionsPendents(request);
				// A diferència del JSP (que recarrega la pàgina i recalcula el comptador en servidor), el
				// client React només actualitza el badge via SSE. Cal notificar el nou recompte per a l'òrgan
				// seleccionat perquè el comptador d'anotacions pendents es refresqui sense recàrrega de pàgina.
				if (eventService != null && organActualId != null) {
					try {
						EntitatDto entitatActual = getEntitatActual(request);
						Long entitatActualId = entitatActual != null ? entitatActual.getId() : null;
						String rolActual = (String) request.getSession().getAttribute(RolHelper.SESSION_ATTRIBUTE_ROL_ACTUAL);
						UsuariAnotacioDto uaDto = new UsuariAnotacioDto(usuariCodi, rolActual, organActualId, entitatActualId);
						eventService.notifyAnotacionsPendents(List.of(uaDto));
						// El comptador de tasques pendents només es mostra per al rol base (tothom). El seu valor és
						// global per usuari (no depèn d'entitat/òrgan), però el client React no reconnecta el SSE en
						// canviar de dimensió, així que cal reenviar-lo perquè el badge es refresqui. No cal evict de
						// la caché: només la invalida un canvi real de tasca.
						if (RolHelper.isRolActualUsuari(request)) {
							eventService.notifyTasquesPendents(List.of(usuariCodi));
						}
					} catch (Exception ex) {
						LOGGER.error("Error notificant el recompte d'anotacions pendents al canvi d'òrgan gestor", ex);
					}
				}
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
