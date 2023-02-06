/**
 * 
 */
package es.caib.ripea.war.helper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.caib.ripea.core.api.dto.EntitatDto;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.EntitatService;

/**
 * Utilitat per a gestionar accions de context de sessió.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SessioHelper {

	public static final String SESSION_ATTRIBUTE_AUTH_PROCESSADA = "SessioHelper.autenticacioProcessada";
	public static final String SESSION_ATTRIBUTE_USUARI_ACTUAL = "SessioHelper.usuariActual";
	public static final String SESSION_ATTRIBUTE_CONTENIDOR_VISTA = "SessioHelper.contenidorVista";
	private static final String SESSION_ATTRIBUTE_PIPELLA_ANOT_REG = "SessioHelper.pipellaAnotacioRegistre";
	private static final String SESSION_ATTRIBUTE_IDIOMA_USUARI = "SessionHelper.idiomaUsuari";
	
	public static final String SESSION_ATTRIBUTE_ORGAN_ACTUAL_CODI_USUARI = "SessionHelper.organActualCodiUsuari"; // organ derived from current contingut or procediment on which user is working

	public static void processarAutenticacio(HttpServletRequest request, HttpServletResponse response, AplicacioService aplicacioService, EntitatService entitatService) {

		if (request.getUserPrincipal() != null && !request.getServletPath().startsWith("/error")) {
			Boolean autenticacioProcessada = (Boolean)request.getSession().getAttribute(SESSION_ATTRIBUTE_AUTH_PROCESSADA);
			if (autenticacioProcessada == null) {
				aplicacioService.processarAutenticacioUsuari();
				request.getSession().setAttribute(SESSION_ATTRIBUTE_AUTH_PROCESSADA, new Boolean(true));
				request.getSession().setAttribute(SESSION_ATTRIBUTE_USUARI_ACTUAL, aplicacioService.getUsuariActual());
				// Forçam el refresc de l'entitat actual i dels permisos d'administració d'òrgan
				EntitatHelper.getEntitatActual(request, entitatService);
			}
			String idioma_usuari = aplicacioService.getUsuariActual().getIdioma();
			LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
			request.getSession().setAttribute("SessionHelper.capsaleraLogo", aplicacioService.propertyFindByNom("es.caib.ripea.capsalera.logo"));
			request.getSession().setAttribute("SessionHelper.capsaleraColorFons", aplicacioService.propertyFindByNom("es.caib.ripea.capsalera.color.fons"));
			request.getSession().setAttribute("SessionHelper.capsaleraColorLletra", aplicacioService.propertyFindByNom("es.caib.ripea.capsalera.color.lletra"));
			request.getSession().setAttribute("SessionHelper.isTipusDocumentsEnabled", aplicacioService.propertyBooleanFindByKey("es.caib.ripea.habilitar.tipusdocument", false));
			request.getSession().setAttribute("SessionHelper.isDocumentsGeneralsEnabled", aplicacioService.propertyBooleanFindByKey("es.caib.ripea.habilitar.documentsgenerals", false));
			request.getSession().setAttribute(SESSION_ATTRIBUTE_IDIOMA_USUARI, idioma_usuari);
			EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
			aplicacioService.actualitzarEntiatThreadLocal(entitat);
			localeResolver.setLocale(request, response, StringUtils.parseLocaleString((String)request.getSession().getAttribute(SESSION_ATTRIBUTE_IDIOMA_USUARI)));
		}
	}

	public static boolean isAutenticacioProcessada(HttpServletRequest request) {
		return request.getSession().getAttribute(SESSION_ATTRIBUTE_AUTH_PROCESSADA) != null;
	}

	public static UsuariDto getUsuariActual(HttpServletRequest request) {
		return (UsuariDto)request.getSession().getAttribute(SESSION_ATTRIBUTE_USUARI_ACTUAL);
	}

	public static void setUsuariActual(HttpServletRequest request, UsuariDto usuari) {
		request.getSession().setAttribute(SESSION_ATTRIBUTE_USUARI_ACTUAL, usuari);
	}

	public static void updateContenidorVista(HttpServletRequest request, String vista) {
		request.getSession().setAttribute(SESSION_ATTRIBUTE_CONTENIDOR_VISTA, vista);
	}

	public static String getContenidorVista(HttpServletRequest request) {
		return (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_CONTENIDOR_VISTA);
	}

	public static void marcatLlegit(HttpServletRequest request) {
		request.getSession().setAttribute(SESSION_ATTRIBUTE_PIPELLA_ANOT_REG, new Boolean(true));
	}

	public static boolean desmarcarLlegit(HttpServletRequest request) {
		Boolean llegit = (Boolean)request.getSession().getAttribute(SESSION_ATTRIBUTE_PIPELLA_ANOT_REG);
		request.getSession().removeAttribute(SESSION_ATTRIBUTE_PIPELLA_ANOT_REG);
		return llegit != null && llegit;
	}
	
	public static String getOrganActual(HttpServletRequest request) {
		return (String) request.getSession().getAttribute(SessioHelper.SESSION_ATTRIBUTE_ORGAN_ACTUAL_CODI_USUARI);
	}
	
	public static void setOrganActual(HttpServletRequest request, String organActual) {
		request.getSession().setAttribute(SESSION_ATTRIBUTE_ORGAN_ACTUAL_CODI_USUARI, organActual);
	}
	
	public static void removeOrganActual(HttpServletRequest request) {
		request.getSession().removeAttribute(SESSION_ATTRIBUTE_ORGAN_ACTUAL_CODI_USUARI);
	}

}
