package es.caib.ripea.war.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.historic.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.historic.HistoricInteressatDto;
import es.caib.ripea.core.api.dto.historic.HistoricMetriquesEnumDto;
import es.caib.ripea.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.ripea.core.api.dto.historic.HistoricUsuariDto;
import es.caib.ripea.core.api.exception.PermissionDeniedStatisticsException;
import es.caib.ripea.core.api.service.HistoricService;
import es.caib.ripea.war.command.HistoricFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.historic.ExportacioActionHistoric;

@Controller
@RequestMapping("/historic")
public class HistoricController extends BaseAdminController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "HistoricController.session.filtre";
//	private static final String SESSION_ATTRIBUTE_METRIQUES = "HistoricController.session.metriques";
	private static final String SESSION_ATTRIBUTE_USUARIS = "HistoricController.session.usuaris";
	private static final String SESSION_ATTRIBUTE_INTERESSATS = "HistoricController.session.interessats";

	@Autowired
	private HistoricService historicService;

	@Autowired
	private ExportacioActionHistoric exportacioActionHistoric;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
		getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		model.addAttribute(historicFiltreCommand);
		model.addAttribute("showDadesEntitat", historicFiltreCommand.showingDadesEntitat());
		model.addAttribute("showDadesOrganGestor", historicFiltreCommand.showingDadesOrganGestor());
		model.addAttribute("showDadesUsuari", historicFiltreCommand.showingDadesUsuari());
		model.addAttribute("showDadesInteressat", historicFiltreCommand.showingDadesInteressat());
		model.addAttribute("showingDadesActuals", historicFiltreCommand.getTipusAgrupament() == null);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
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
			historicService.comprovarAccesEstadistiques(entitat.getId(), rolActual);
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
		getEntitatActualComprovantPermisAdminEntitat(request);
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
		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		PaginaDto<HistoricExpedientDto> pagina = null;
		try {
			pagina = historicService.getPageDadesEntitat(
				entitat.getId(),
				historicFiltreCommand.asDto(),
				rolActual,
				DatatablesHelper.getPaginacioDtoFromRequest(request));
		} catch (PermissionDeniedStatisticsException e) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is denied");
		}
		return DatatablesHelper.getDatatableResponse(request, pagina);
	}

	@RequestMapping(value = "/chart/entitat", method = RequestMethod.GET)
	@ResponseBody
	public List<HistoricExpedientDto> expedientsEntitatChartData(HttpServletRequest request, HttpServletResponse response) throws IOException {
		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		List<HistoricExpedientDto> result = null;
		try {
			result = historicService.getDadesEntitat(
				entitat.getId(),
				rolActual,
				historicFiltreCommand.asDto());
		} catch (PermissionDeniedStatisticsException e) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is denied");
		}
		return result;
	}

	@RequestMapping(value = "/entitat/actual", method = RequestMethod.GET)
	@ResponseBody
	public List<HistoricExpedientDto> getHistoricExpedientActual(HttpServletRequest request, HttpServletResponse response) throws IOException {
		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		List<HistoricExpedientDto> result = null;
		
		try {
			result = historicService.getDadesActualsEntitat(
				entitat.getId(),
				rolActual,
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
		getEntitatActualComprovantPermisAdminEntitat(request);
		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		Map<OrganGestorDto, List<HistoricExpedientDto>> dades = null;
		
		try {
			dades = historicService.getHistoricsByOrganGestor(
				entitat.getId(),
				rolActual,
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
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		Map<OrganGestorDto, HistoricExpedientDto> dades = null;
		
		try {
			dades = historicService.getDadesActualsOrgansGestors(
				entitat.getId(),
				rolActual,
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
		getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		Map<String, List<HistoricUsuariDto>> results = new HashMap<String, List<HistoricUsuariDto>>();
		for (String codiUsuari : usuarisCodi) {
			try {
				results.put(codiUsuari, historicService.getDadesUsuari(codiUsuari, entitat.getId(), rolActual, historicFiltreCommand.asDto()));
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
		getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		Map<String, List<HistoricUsuariDto>> results = new HashMap<String, List<HistoricUsuariDto>>();
		for (String codiUsuari : usuarisCodi) {
			try {
				results.put(codiUsuari, historicService.getDadesActualsUsuari(codiUsuari, entitat.getId(), rolActual, historicFiltreCommand.asDto()));
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
		getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		Map<String, List<HistoricInteressatDto>> results = new HashMap<String, List<HistoricInteressatDto>>();
		for (String docNum : interessatsDocNum) {
			try {
				List<HistoricInteressatDto> historics = historicService.getDadesInteressat(
						docNum,
						entitat.getId(),
						rolActual,
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
		getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		Map<String, List<HistoricInteressatDto>> results = new HashMap<String, List<HistoricInteressatDto>>();
		for (String docNum : interessatsDocNum) {
			try {
				results.put(docNum, historicService.getDadesActualsInteressat(docNum, entitat.getId(), rolActual, historicFiltreCommand.asDto()));
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
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		FitxerDto fitxer = null;
		try {
			if (historicFiltreCommand.showingDadesEntitat()) {
				fitxer = exportacioActionHistoric.exportarHistoricEntitat(entitatActual, rolActual, historicFiltreCommand.asDto(), format);
				
			} else if (historicFiltreCommand.showingDadesOrganGestor()) {
				fitxer = exportacioActionHistoric.exportarHistoricOrgansGestors(entitatActual.getId(), rolActual, historicFiltreCommand.asDto(), format);
				
			} else if (historicFiltreCommand.showingDadesUsuari()) {
				String[] usuaris = (String[])RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_USUARIS);
				usuaris = usuaris == null ? new String[0] : usuaris;
				fitxer = exportacioActionHistoric.exportarHistoricUsuaris(usuaris, entitatActual.getId(), rolActual, historicFiltreCommand.asDto(), format);
				
			} else if (historicFiltreCommand.showingDadesInteressat()) {
				String[] interessats = (String[])RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_INTERESSATS);
				interessats = interessats == null ? new String[0] : interessats;
				fitxer = exportacioActionHistoric.exportarHistoricInteressats(interessats, entitatActual.getId(), rolActual, historicFiltreCommand.asDto(), format);
				
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
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
//		Cookie cookie = WebUtils.getCookie(request, COOKIE_MEUS_EXPEDIENTS);
//		filtreCommand.setMeusExpedients(cookie != null && "true".equals(cookie.getValue()));
		return filtreCommand;
	}

}
