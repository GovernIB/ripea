package es.caib.ripea.war.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
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
import es.caib.ripea.core.api.dto.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.HistoricMetriquesEnumDto;
import es.caib.ripea.core.api.dto.HistoricUsuariDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.service.HistoricService;
import es.caib.ripea.war.command.HistoricFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.RequestSessionHelper;

@Controller
@RequestMapping("/historic")
public class HistoricController extends BaseAdminController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "HistoricController.session.filtre";
	private static final String SESSION_ATTRIBUTE_METRIQUES = "HistoricController.session.metriques";
	private static final String SESSION_ATTRIBUTE_USUARIS = "HistoricController.session.usuaris";

	@Autowired
	private HistoricService historicService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		model.addAttribute(historicFiltreCommand);
		model.addAttribute("showDadesEntitat", historicFiltreCommand.showingDadesEntitat());
		model.addAttribute("showDadesOrganGestor", historicFiltreCommand.showingDadesOrganGestor());
		model.addAttribute("showDadesUsuari", historicFiltreCommand.showingDadesUsuari());
		model.addAttribute("showDadesInteressat", historicFiltreCommand.showingDadesInteressat());

		model.addAttribute("showingDadesActuals", historicFiltreCommand.getTipusAgrupament() == null);

		HistoricMetriquesEnumDto[] metriques = (HistoricMetriquesEnumDto[])RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_METRIQUES);
		if (metriques == null) {
			metriques = new HistoricMetriquesEnumDto[0];
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_METRIQUES, metriques);
		}
		model.addAttribute("metriquesSeleccionades", metriques);
		String[] usuaris = (String[])RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_USUARIS);
		if (usuaris == null) {
			usuaris = new String[0];
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_USUARIS, usuaris);
		}
		model.addAttribute("usuarisSeleccionats", usuaris);
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
	public DatatablesResponse expedientsDatatable(HttpServletRequest request) {
		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		PaginaDto<HistoricExpedientDto> pagina = historicService.getPageDadesEntitat(
				entitat.getId(),
				historicFiltreCommand.asDto(),
				DatatablesHelper.getPaginacioDtoFromRequest(request));
		return DatatablesHelper.getDatatableResponse(request, pagina);
	}

	@RequestMapping(value = "/chart/entitat", method = RequestMethod.GET)
	@ResponseBody
	public List<HistoricExpedientDto> expedientsEntitatChartData(HttpServletRequest request) {
		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		List<HistoricExpedientDto> response = historicService.getDadesEntitat(
				entitat.getId(),
				historicFiltreCommand.asDto());
		return response;
	}

	@RequestMapping(value = "/organgestors", method = RequestMethod.POST)
	@ResponseBody
	public Map<HistoricMetriquesEnumDto, Map<String, Map<Date, Long>>> getOrgansGestorsMetrics(
			HttpServletRequest request,
			@RequestParam("metrics[]") HistoricMetriquesEnumDto[] metrics) {
		getEntitatActualComprovantPermisAdminEntitat(request);

		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);

		// registram les mètriques consultades a la sessió
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_METRIQUES, metrics);

		Map<OrganGestorDto, List<HistoricExpedientDto>> dades = historicService.getDadesOrgansGestors(
				historicFiltreCommand.asDto());

		Map<HistoricMetriquesEnumDto, Map<String, Map<Date, Long>>> response = new HashMap<>();
		if (dades == null) {
			return response;
		}

		for (HistoricMetriquesEnumDto metric : metrics) {
			Map<String, Map<Date, Long>> mapOrgansGestors = new HashMap<>();
			for (Entry<OrganGestorDto, List<HistoricExpedientDto>> entry : dades.entrySet()) {
				Map<Date, Long> mapMetric = new HashMap<>();
				for (HistoricExpedientDto historic : entry.getValue())
					mapMetric.put(historic.getData(), metric.getValue(historic));
				mapOrgansGestors.put(entry.getKey().getNom(), mapMetric);
			}
			response.put(metric, mapOrgansGestors);
		}

		return response;
	}

	@RequestMapping(value = "/actual/organgestors", method = RequestMethod.POST)
	@ResponseBody
	public Map<HistoricMetriquesEnumDto, Map<String, Long>> getDadesActualsOrgansGestors(
			HttpServletRequest request,
			@RequestParam("metrics[]") HistoricMetriquesEnumDto[] metrics) {
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);

		// registram les mètriques consultades a la sessió
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_METRIQUES, metrics);

		Map<OrganGestorDto, HistoricExpedientDto> dades = historicService.getDadesActualsOrgansGestors(
				historicFiltreCommand.asDto());

		Map<HistoricMetriquesEnumDto, Map<String, Long>> response = new HashMap<>();
		if (dades == null) {
			return response;
		}
		for (HistoricMetriquesEnumDto metric : metrics) {
			Map<String, Long> mapOrgansGestors = new HashMap<>();
			for (Entry<OrganGestorDto, HistoricExpedientDto> entry : dades.entrySet()) {
				mapOrgansGestors.put(entry.getKey().getNom(), metric.getValue(entry.getValue()));
			}
			response.put(metric, mapOrgansGestors);
		}

		return response;
	}

	@RequestMapping(value = "/usuaris/dades/", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<HistoricUsuariDto>> usuarisData(
			HttpServletRequest request,
			@RequestParam("usuaris[]") String[] usuarisCodi) {
		// registram els usuaris consultats a la sessió
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_USUARIS, usuarisCodi);

		getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		Map<String, List<HistoricUsuariDto>> results = new HashMap<String, List<HistoricUsuariDto>>();
		for (String codiUsuari : usuarisCodi) {
			results.put(codiUsuari, 
					historicService.getDadesUsuari(codiUsuari, historicFiltreCommand.asDto()));	
		}
		
		return results;
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
