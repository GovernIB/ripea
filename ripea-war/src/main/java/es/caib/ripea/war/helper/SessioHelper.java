/**
 * 
 */
package es.caib.ripea.war.helper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.service.AplicacioService;

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


	public static void processarAutenticacio(
			HttpServletRequest request,
			HttpServletResponse response,
			AplicacioService aplicacioService) {
		if (request.getUserPrincipal() != null) {
			Boolean autenticacioProcessada = (Boolean)request.getSession().getAttribute(
					SESSION_ATTRIBUTE_AUTH_PROCESSADA);
			if (autenticacioProcessada == null) {
				aplicacioService.processarAutenticacioUsuari();
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_AUTH_PROCESSADA,
						new Boolean(true));
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_USUARI_ACTUAL,
						aplicacioService.getUsuariActual());
			}
			String idioma_usuari = aplicacioService.getUsuariActual().getIdioma();
			LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
			
			request.getSession().setAttribute(
					SESSION_ATTRIBUTE_IDIOMA_USUARI, 
					idioma_usuari);
			
	        localeResolver.setLocale(
	        		request, 
	        		response, 
	        		StringUtils.parseLocaleString(
	        				(String)request.getSession().getAttribute(SESSION_ATTRIBUTE_IDIOMA_USUARI))
	        		);
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
	public static void updateContenidorVista(
			HttpServletRequest request,
			String vista) {
		request.getSession().setAttribute(
				SESSION_ATTRIBUTE_CONTENIDOR_VISTA,
				vista);
	}
	public static String getContenidorVista(HttpServletRequest request) {
		return (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_CONTENIDOR_VISTA);
	}
	
	public static void marcatLlegit(
			HttpServletRequest request) {
		request.getSession().setAttribute(
				SESSION_ATTRIBUTE_PIPELLA_ANOT_REG,
				new Boolean(true));
	}
	public static boolean desmarcarLlegit(
			HttpServletRequest request) {
		Boolean llegit = (Boolean) request.getSession().getAttribute(SESSION_ATTRIBUTE_PIPELLA_ANOT_REG);
		request.getSession().removeAttribute(SESSION_ATTRIBUTE_PIPELLA_ANOT_REG);
		return llegit != null && llegit;
	}

}
