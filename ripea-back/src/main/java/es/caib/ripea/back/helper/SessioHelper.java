package es.caib.ripea.back.helper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.ContingutVistaEnumDto;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.MoureDestiVistaEnumDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.EntitatService;

public class SessioHelper {

	public static final String SESSION_ATTRIBUTE_AUTH_PROCESSADA = "SessioHelper.autenticacioProcessada";
	public static final String SESSION_ATTRIBUTE_USUARI_ACTUAL = "SessioHelper.usuariActual";
	public static final String SESSION_ATTRIBUTE_CONTINGUT_VISTA = "SessioHelper.contingutVista";
	private static final String SESSION_ATTRIBUTE_PIPELLA_ANOT_REG = "SessioHelper.pipellaAnotacioRegistre";
	private static final String SESSION_ATTRIBUTE_IDIOMA_USUARI = "SessionHelper.idiomaUsuari";
	public static final String SESSION_ATTRIBUTE_ORGAN_ACTUAL_CODI_USUARI = "SessionHelper.organActualCodiUsuari"; // organ derived from current contingut or procediment on which user is working
	public static final String SESSION_ATTRIBUTE_MOURE_VISTA = "SessioHelper.moureVista";
	
	private static boolean propietatsInicialitzades = false;
	private static String capLogo = null;
	private static String capColorFons = null;
	private static String capColorLletra = null;
	private static boolean habilitarTipusDocument = false;
	private static boolean habilitarDocumentsGenerals = false;
	private static boolean habilitarDominis = false;

	public static void resetPropietats() {
        SessioHelper.propietatsInicialitzades = false;
    }

	public static String processarAutenticacio(HttpServletRequest request, HttpServletResponse response, AplicacioService aplicacioService, EntitatService entitatService) {

		String resultat = null;
		
		if (request.getUserPrincipal() != null && !request.getServletPath().startsWith("/error")) {
			
			Boolean autenticacioProcessada = (Boolean)request.getSession().getAttribute(SESSION_ATTRIBUTE_AUTH_PROCESSADA);
			UsuariDto usuariActual = null;
			EntitatDto entitatActual = null;
			if (autenticacioProcessada == null) {
				aplicacioService.processarAutenticacioUsuari();
				usuariActual = aplicacioService.getUsuariActual();
				request.getSession().setAttribute(SESSION_ATTRIBUTE_AUTH_PROCESSADA, new Boolean(true));
				request.getSession().setAttribute(SESSION_ATTRIBUTE_USUARI_ACTUAL, usuariActual);
				// Forçam el refresc de l'entitat actual i dels permisos d'administració d'òrgan
				entitatActual = EntitatHelper.getEntitatActual(request, entitatService);
				//Assignam el darrer rol utilitzat com a rol actual, sino en posam un per defecte.
				if (usuariActual.getRolActual()!=null && RolHelper.getRolsUsuariActual(request).contains(usuariActual.getRolActual())) {
					request.getSession().setAttribute(RolHelper.SESSION_ATTRIBUTE_ROL_ACTUAL, usuariActual.getRolActual());
				} else {
					request.getSession().setAttribute(RolHelper.SESSION_ATTRIBUTE_ROL_ACTUAL, RolHelper.getRolActual(request));
				}
				if (RolHelper.isRolActualDissenyadorOrgan(request)) {
					resultat = "/ripea/metaExpedient";
				}
			}
			if (usuariActual == null) {
				usuariActual = aplicacioService.getUsuariActual();
			}
			if (entitatActual == null) {
				entitatActual = EntitatHelper.getEntitatActual(request, entitatService);
			}
			if (!propietatsInicialitzades) {
                capLogo = aplicacioService.propertyFindByNom(PropertyConfig.ENTITAT_LOGO);
                capColorFons = aplicacioService.propertyFindByNom(PropertyConfig.CAPSALERA_FONS);
                capColorLletra = aplicacioService.propertyFindByNom(PropertyConfig.CAPSALERA_LLETRA);
                habilitarTipusDocument = aplicacioService.propertyBooleanFindByKey(PropertyConfig.TIPUS_DOCUMENT_ACTIUS, false);
                habilitarDocumentsGenerals = aplicacioService.propertyBooleanFindByKey(PropertyConfig.DOCUMENTS_GENERALS_ACTIUS, false);
                habilitarDominis = aplicacioService.propertyBooleanFindByKey(PropertyConfig.DOMINIS_HABILITATS);
				propietatsInicialitzades = true;
			}
			String idioma_usuari = usuariActual.getIdioma();
			LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
			request.getSession().setAttribute("SessionHelper.capsaleraLogo", capLogo);
			request.getSession().setAttribute("SessionHelper.capsaleraColorFons", capColorFons);
			request.getSession().setAttribute("SessionHelper.capsaleraColorLletra", capColorLletra);
			request.getSession().setAttribute("SessionHelper.isTipusDocumentsEnabled", habilitarTipusDocument);
			request.getSession().setAttribute("SessionHelper.isDocumentsGeneralsEnabled", habilitarDocumentsGenerals);
			request.getSession().setAttribute("SessionHelper.isDominisEnabled", habilitarDominis);
			request.getSession().setAttribute(SESSION_ATTRIBUTE_IDIOMA_USUARI, idioma_usuari);
			aplicacioService.actualitzarEntiatThreadLocal(entitatActual);
			localeResolver.setLocale(request, response, StringUtils.parseLocaleString(idioma_usuari));
		}
		
		return resultat;
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

	public static void updateContenidorVista(HttpServletRequest request, ContingutVistaEnumDto vista) {
		request.getSession().setAttribute(SESSION_ATTRIBUTE_CONTINGUT_VISTA, vista);
	}

	public static ContingutVistaEnumDto getContenidorVista(HttpServletRequest request) {
		return (ContingutVistaEnumDto)request.getSession().getAttribute(SESSION_ATTRIBUTE_CONTINGUT_VISTA);
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
	
	public static void updateMoureVista(HttpServletRequest request, MoureDestiVistaEnumDto vista) {
		request.getSession().setAttribute(SESSION_ATTRIBUTE_MOURE_VISTA, vista);
	}
	
	public static MoureDestiVistaEnumDto getMoureVista(HttpServletRequest request) {
		return (MoureDestiVistaEnumDto)request.getSession().getAttribute(SESSION_ATTRIBUTE_MOURE_VISTA);
	}

}
