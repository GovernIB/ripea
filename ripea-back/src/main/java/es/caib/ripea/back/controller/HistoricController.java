package es.caib.ripea.back.controller;

import es.caib.ripea.back.command.HistoricFiltreCommand;
import es.caib.ripea.back.helper.DatatablesHelper;
import es.caib.ripea.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.back.helper.RequestSessionHelper;
import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.OrganGestorDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.historic.*;
import es.caib.ripea.service.intf.exception.PermissionDeniedStatisticsException;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.HistoricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/historic")
public class HistoricController extends BaseAdminController {

	public static final String SESSION_ATTRIBUTE_FILTRE = "HistoricController.session.filtre";
//	private static final String SESSION_ATTRIBUTE_METRIQUES = "HistoricController.session.metriques";
	private static final String SESSION_ATTRIBUTE_USUARIS = "HistoricController.session.usuaris";
	private static final String SESSION_ATTRIBUTE_INTERESSATS = "HistoricController.session.interessats";

	@Autowired
	private HistoricService historicService;
	@Autowired
	private AplicacioService aplicacioService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		model.addAttribute(historicFiltreCommand);
		model.addAttribute("showDadesEntitat", historicFiltreCommand.showingDadesEntitat());
		model.addAttribute("showDadesOrganGestor", historicFiltreCommand.showingDadesOrganGestor());
		model.addAttribute("showDadesUsuari", historicFiltreCommand.showingDadesUsuari());
		model.addAttribute("showDadesInteressat", historicFiltreCommand.showingDadesInteressat());
		model.addAttribute("showingDadesActuals", historicFiltreCommand.getTipusAgrupament() == null);
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		String[] usuaris = (String[])RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_USUARIS);
		if (usuaris == null) {
			usuaris = new String[0];
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_USUARIS, usuaris);
		}
		model.addAttribute("usuarisSeleccionats", usuaris);

		String[] interessats = (String[])RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_INTERESSATS);
		if (interessats == null) {
			interessats = new String[0];
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_INTERESSATS, interessats);
		}
		model.addAttribute("interessatsSeleccionats", interessats);
		try {
			historicService.comprovarAccesEstadistiques(entitat.getId(), RolHelper.getRolActual(request));
		} catch (PermissionDeniedStatisticsException e) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is denied");
		}
		return "historic";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid HistoricFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		getEntitatActualComprovantPermisos(request);
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
			return "redirect:historic";
		}
		if (!bindingResult.hasErrors()) {
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return "redirect:historic";
	}

	@RequestMapping(value = "/expedient/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse expedientsDatatable(HttpServletRequest request, HttpServletResponse response) throws IOException {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		PaginaDto<HistoricExpedientDto> pagina = null;
		try {
			pagina = historicService.getPageDadesEntitat(
				entitat.getId(),
				historicFiltreCommand.asDto(),
				RolHelper.getRolActual(request),
				DatatablesHelper.getPaginacioDtoFromRequest(request));
		} catch (PermissionDeniedStatisticsException e) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is denied");
		}
		return DatatablesHelper.getDatatableResponse(request, pagina);
	}

	@RequestMapping(value = "/chart/entitat", method = RequestMethod.GET)
	@ResponseBody
	public List<HistoricExpedientDto> expedientsEntitatChartData(HttpServletRequest request, HttpServletResponse response) throws IOException {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		List<HistoricExpedientDto> result = null;
		try {
			result = historicService.getDadesEntitat(
				entitat.getId(),
				RolHelper.getRolActual(request),
				historicFiltreCommand.asDto());
		} catch (PermissionDeniedStatisticsException e) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is denied");
		}
		return result;
	}

	@RequestMapping(value = "/entitat/actual", method = RequestMethod.GET)
	@ResponseBody
	public List<HistoricExpedientDto> getHistoricExpedientActual(HttpServletRequest request, HttpServletResponse response) throws IOException {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		List<HistoricExpedientDto> result = null;
		
		try {
			result = historicService.getDadesActualsEntitat(
				entitat.getId(),
				RolHelper.getRolActual(request),
				historicFiltreCommand.asDto());
		} catch (PermissionDeniedStatisticsException e) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is denied");
		}
		return result;
	}
	
	@RequestMapping(value = "/organgestors", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<HistoricExpedientDto>> getHistoricsByOrganGestor(
			HttpServletRequest request, 
			HttpServletResponse response,
			@RequestParam("metrics[]") HistoricMetriquesEnumDto[] metrics) throws IOException {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		Map<OrganGestorDto, List<HistoricExpedientDto>> dades = null;
		
		try {
			dades = historicService.getHistoricsByOrganGestor(
				entitat.getId(),
				RolHelper.getRolActual(request),
				historicFiltreCommand.asDto());
		} catch (PermissionDeniedStatisticsException e) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is denied");
		}
		if (dades == null) {
			return new HashMap<>();
		}

		// Substituim els objectes organGestorsDto pel nom de l'organ
		Map<String, List<HistoricExpedientDto>> results = new HashMap<>();
		for (OrganGestorDto organ : dades.keySet()) {
			results.put(organ.getNom(), dades.get(organ));
		}

		return results;
	}
	
	@RequestMapping(value = "/organgestors/actual", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, HistoricExpedientDto> getDadesActualsOrgansGestors(
			HttpServletRequest request, 
			HttpServletResponse response) throws IOException {
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		Map<OrganGestorDto, HistoricExpedientDto> dades = null;
		
		try {
			dades = historicService.getDadesActualsOrgansGestors(
				entitat.getId(),
				RolHelper.getRolActual(request),
				historicFiltreCommand.asDto());
		} catch (PermissionDeniedStatisticsException e) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is denied");
		}
		Map<String, HistoricExpedientDto> results = new HashMap<>();
		if (dades == null) {
			return results;
		}
		for (OrganGestorDto organ : dades.keySet()) {
			results.put(organ.getNom(), dades.get(organ));
		}
		return results;
	}

	@RequestMapping(value = "/usuaris/dades/", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<HistoricUsuariDto>> usuarisData(
			HttpServletRequest request, 
			HttpServletResponse response,
			@RequestParam("usuaris[]") String[] usuarisCodi) throws IOException {
		// registram els usuaris consultats a la sessió
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_USUARIS, usuarisCodi);
		getEntitatActualComprovantPermisos(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		Map<String, List<HistoricUsuariDto>> results = new HashMap<String, List<HistoricUsuariDto>>();
		for (String codiUsuari : usuarisCodi) {
			try {
				results.put(codiUsuari, historicService.getDadesUsuari(
						codiUsuari, 
						entitat.getId(), 
						RolHelper.getRolActual(request), 
						historicFiltreCommand.asDto()));
			} catch (PermissionDeniedStatisticsException e) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is denied");
			}
		}

		return results;
	}

	@RequestMapping(value = "/usuaris/actual", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<HistoricUsuariDto>> usuarisDataActual(
			HttpServletRequest request, 
			HttpServletResponse response,
			@RequestParam("usuaris[]") String[] usuarisCodi) throws IOException {
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_USUARIS, usuarisCodi);
		getEntitatActualComprovantPermisos(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		Map<String, List<HistoricUsuariDto>> results = new HashMap<String, List<HistoricUsuariDto>>();
		for (String codiUsuari : usuarisCodi) {
			try {
				results.put(codiUsuari, historicService.getDadesActualsUsuari(
						codiUsuari, 
						entitat.getId(), 
						RolHelper.getRolActual(request), 
						historicFiltreCommand.asDto()));
			} catch (PermissionDeniedStatisticsException e) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is denied");
			}
		}
		return results;
	}

	@RequestMapping(value = "/interessats/dades/", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<HistoricInteressatDto>> interessatsData(
			HttpServletRequest request, 
			HttpServletResponse response,
			@RequestParam("interessats[]") String[] interessatsDocNum) throws IOException {
		// registram els usuaris consultats a la sessió
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_INTERESSATS, interessatsDocNum);
		getEntitatActualComprovantPermisos(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		Map<String, List<HistoricInteressatDto>> results = new HashMap<String, List<HistoricInteressatDto>>();
		for (String docNum : interessatsDocNum) {
			try {
				List<HistoricInteressatDto> historics = historicService.getDadesInteressat(
						docNum,
						entitat.getId(),
						RolHelper.getRolActual(request),
						historicFiltreCommand.asDto());
				results.put(docNum, historics);
			} catch (PermissionDeniedStatisticsException e) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is denied");
			}
		}

		return results;
	}

	@RequestMapping(value = "/interessats/actual", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<HistoricInteressatDto>> interessatsDataActual(
			HttpServletRequest request, 
			HttpServletResponse response,
			@RequestParam("interessats[]") String[] interessatsDocNum) throws IOException {
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_INTERESSATS, interessatsDocNum);
		getEntitatActualComprovantPermisos(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		Map<String, List<HistoricInteressatDto>> results = new HashMap<String, List<HistoricInteressatDto>>();
		for (String docNum : interessatsDocNum) {
			try {
				results.put(docNum, historicService.getDadesActualsInteressat(
						docNum, 
						entitat.getId(), 
						RolHelper.getRolActual(request), 
						historicFiltreCommand.asDto()));
			} catch (PermissionDeniedStatisticsException e) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is denied");
			}
		}
		return results;
	}

	@RequestMapping(value = "/exportar", method = RequestMethod.POST)
	public String export(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam String format) throws Exception {
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		if (historicFiltreCommand.getTipusAgrupament() == null) {
			historicFiltreCommand.setTipusAgrupament(HistoricTipusEnumDto.DIARI);
		}
		String rolActual = RolHelper.getRolActual(request);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		FitxerDto fitxer = null;
		try {
			if (historicFiltreCommand.showingDadesEntitat()) {
				fitxer = historicService.exportarHistoricEntitat(entitatActual, rolActual, historicFiltreCommand.asDto(), format);
				
			} else if (historicFiltreCommand.showingDadesOrganGestor()) {
				fitxer = historicService.exportarHistoricOrgansGestors(entitatActual.getId(), rolActual, historicFiltreCommand.asDto(), format);
				
			} else if (historicFiltreCommand.showingDadesUsuari()) {
				String[] usuaris = (String[])RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_USUARIS);
				usuaris = usuaris == null ? new String[0] : usuaris;
				fitxer = historicService.exportarHistoricUsuaris(usuaris, entitatActual.getId(), rolActual, historicFiltreCommand.asDto(), format);
				
			} else if (historicFiltreCommand.showingDadesInteressat()) {
				String[] interessats = (String[])RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_INTERESSATS);
				interessats = interessats == null ? new String[0] : interessats;
				fitxer = historicService.exportarHistoricInteressats(interessats, entitatActual.getId(), rolActual, historicFiltreCommand.asDto(), format);
				
			} else {
				throw new Exception("No s'han seleccionat el tipus de dades a generar");
			}
		} catch (PermissionDeniedStatisticsException e) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is denied");
		}
		writeFileToResponse(fitxer.getNom(), fitxer.getContingut(), response);
		return null;
	}

	////
	// PRIVATE CONTENT
	////

	private HistoricFiltreCommand getFiltreCommand(HttpServletRequest request) {
		HistoricFiltreCommand filtreCommand = (HistoricFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new HistoricFiltreCommand();
			Long id = aplicacioService.getProcedimentPerDefecte(EntitatHelper.getEntitatActual(request).getId(), RolHelper.getRolActual(request));
			filtreCommand.setMetaExpedientsIds(id != null ? Arrays.asList(id) : null);
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
//		Cookie cookie = WebUtils.getCookie(request, COOKIE_MEUS_EXPEDIENTS);
//		filtreCommand.setMeusExpedients(cookie != null && "true".equals(cookie.getValue()));
		return filtreCommand;
	}

}