package es.caib.ripea.back.helper;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.ExpedientService;
import es.caib.ripea.service.intf.service.MetaExpedientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Utilitat per a gestionar les expedients i metaexpedients de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientHelper {

	private static final String REQUEST_PARAMETER_ACCES_EXPEDIENTS = "ExpedientHelper.teAccesExpedients";
	public static final String SESSION_ATTRIBUTE_ROL_ACTUAL = "RolHelper.rol.actual";
	private static final String REQUEST_PARAMETER_STATISTICS_EXPEDIENTS = "ExpedientHelper.teAccesEstadistiques";
	private static final String SESSION_CONVERSIO_DEFINITIU_ACTIVA = "ExpedientHelper.isConversioDefinitiuActiva";
	private static final String SESSION_URL_VALIDACIO = "ExpedientHelper.isUrlValidacioDefinida";
	private static final String SESSION_URLS_INSTRUCCIO = "ExpedientHelper.isUrlsInstruccioActiu";

	@Autowired private ExpedientService expedientService;
	
	public static void accesUsuariExpedients(
			HttpServletRequest request,
			MetaExpedientService metaExpedientService) {
		request.setAttribute(
				REQUEST_PARAMETER_ACCES_EXPEDIENTS,
				teAccesExpedients(request, metaExpedientService));
	}
	
	public static void accesUsuariEstadistiques(
			HttpServletRequest request,
			MetaExpedientService metaExpedientService) {
		request.setAttribute(
				REQUEST_PARAMETER_STATISTICS_EXPEDIENTS,
				teAccesEstadistiques(request, metaExpedientService));
	}
	public static Boolean teAccesExpedients(
			HttpServletRequest request) {
		return teAccesExpedients(request, null);
	}
	
	public static Boolean teAccesEstadistiques(
			HttpServletRequest request) {
		return teAccesEstadistiques(request, null);
	}
	public static Boolean teAccesExpedients(
			HttpServletRequest request,
			MetaExpedientService metaExpedientService) {
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		
		Boolean teAcces = (Boolean)request.getSession().getAttribute(REQUEST_PARAMETER_ACCES_EXPEDIENTS);
		if (RolHelper.isRolActualUsuari(request) && teAcces == null && metaExpedientService != null) {
			teAcces = new Boolean(false);
			EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
			if (entitatActual != null) {
				teAcces = metaExpedientService.hasPermissionForAnyProcediment(entitatActual.getId(), rolActual, PermissionEnumDto.READ);
			}
			request.getSession().setAttribute(
					REQUEST_PARAMETER_ACCES_EXPEDIENTS,
					teAcces);
		}
		return teAcces;
	}
	
	public static Boolean teAccesEstadistiques(
			HttpServletRequest request,
			MetaExpedientService metaExpedientService) {
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		
		Boolean teAcces = (Boolean)request.getAttribute(REQUEST_PARAMETER_STATISTICS_EXPEDIENTS);
		if (RolHelper.isRolActualUsuari(request) && teAcces == null && metaExpedientService != null) {
			teAcces = new Boolean(false);
			EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
			if (entitatActual != null) {
				List<MetaExpedientDto> expedientsAccessibles =  metaExpedientService.findActiusAmbEntitatPerConsultaEstadistiques(entitatActual.getId(), null, rolActual);
				teAcces = new Boolean(expedientsAccessibles != null && !expedientsAccessibles.isEmpty());
			}
			request.setAttribute(
					REQUEST_PARAMETER_STATISTICS_EXPEDIENTS,
					teAcces);
		}
		return teAcces;
	}

	public static void resetAccesUsuariExpedients(
			HttpServletRequest request) {
		request.getSession().removeAttribute(REQUEST_PARAMETER_ACCES_EXPEDIENTS);
	}
	
	public static Boolean isConversioDefinitiuActiva(HttpServletRequest request) {
		return (Boolean)request.getSession().getAttribute(SESSION_CONVERSIO_DEFINITIU_ACTIVA);
	}
	
	public static void setConversioDefinitiu(
			HttpServletRequest request,
			AplicacioService aplicacioService) {
		boolean isConversioDefinitiuActiva = Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.conversio.definitiu"));
		request.getSession().setAttribute(
				SESSION_CONVERSIO_DEFINITIU_ACTIVA,
				isConversioDefinitiuActiva);
	}
	
	public static Boolean isUrlValidacioDefinida(HttpServletRequest request) {
		return (Boolean)request.getSession().getAttribute(SESSION_URL_VALIDACIO);
	}
	
	public static void setUrlValidacioDefinida(
			HttpServletRequest request,
			AplicacioService aplicacioService) {
		boolean isUrlValidacioDefinida = aplicacioService.propertyFindByNom("es.caib.ripea.documents.validacio.url") != null ? true : false;
		request.getSession().setAttribute(
				SESSION_URL_VALIDACIO,
				isUrlValidacioDefinida);
	}
	
	public static Boolean isUrlsInstruccioActiu(HttpServletRequest request) {
		return (Boolean)request.getSession().getAttribute(SESSION_URLS_INSTRUCCIO);
	}
	
	public static void setUrlsInstruccioActiu(
			HttpServletRequest request,
			AplicacioService aplicacioService) {
		boolean isUrlsInstruccioActiu = Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.expedient.generar.urls.instruccio"));
		request.getSession().setAttribute(
				SESSION_URLS_INSTRUCCIO,
				isUrlsInstruccioActiu);
	}
	
	public ContingutVistaEnumDto getVistaActiva(HttpServletRequest request) {
		
		ContingutVistaEnumDto contingutVista = SessioHelper.getContenidorVista(request);
		if (contingutVista == null) {
			contingutVista = expedientService.getVistaUsuariActual();
			if (contingutVista == null) {
				contingutVista = ContingutVistaEnumDto.TREETABLE_PER_CARPETA;
			}
		}
		return contingutVista;
	}
	
	public MoureDestiVistaEnumDto getVistaMoureActiva(HttpServletRequest request) {
		
		MoureDestiVistaEnumDto contingutVista = SessioHelper.getMoureVista(request);
		if (contingutVista == null) {
			contingutVista = expedientService.getVistaMoureUsuariActual();
			if (contingutVista == null) {
				contingutVista = MoureDestiVistaEnumDto.LLISTA;
			}
		}
		return contingutVista;
	}
	

	
	public void omplirVistaActiva(
			HttpServletRequest request,
			Model model) {
		
		ContingutVistaEnumDto contingutVista = getVistaActiva(request);

		model.addAttribute(
				"vistaIcones",
				contingutVista == ContingutVistaEnumDto.GRID);
		model.addAttribute(
				"vistaLlistat",
				contingutVista == ContingutVistaEnumDto.TREETABLE_PER_CARPETA);
		model.addAttribute(
				"vistaTreetablePerTipusDocuments",
				contingutVista == ContingutVistaEnumDto.TREETABLE_PER_TIPUS_DOCUMENT);
		model.addAttribute(
				"vistaTreetablePerEstats",
				contingutVista == ContingutVistaEnumDto.TREETABLE_PER_ESTAT);

	}
	
	public boolean isVistaTreetablePerTipusDocuments(HttpServletRequest request) {
		ContingutVistaEnumDto contingutVista = getVistaActiva(request);
		return contingutVista == ContingutVistaEnumDto.TREETABLE_PER_TIPUS_DOCUMENT;
	}
	
	public boolean isVistaTreetablePerEstats(HttpServletRequest request) {
		ContingutVistaEnumDto contingutVista = getVistaActiva(request);
		return contingutVista == ContingutVistaEnumDto.TREETABLE_PER_ESTAT;
	}

	public boolean isVistaArbreMoureDocuments(HttpServletRequest request) {
		MoureDestiVistaEnumDto contingutVista = getVistaMoureActiva(request);
		return contingutVista == MoureDestiVistaEnumDto.ARBRE;
	}
}
