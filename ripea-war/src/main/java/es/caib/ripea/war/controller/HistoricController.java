package es.caib.ripea.war.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import es.caib.ripea.core.api.dto.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.HistoricInteressatDto;
import es.caib.ripea.core.api.dto.HistoricMetriquesEnumDto;
import es.caib.ripea.core.api.dto.HistoricUsuariDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginaDto;
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
	public String get(HttpServletRequest request, Model model) {
		getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		model.addAttribute(historicFiltreCommand);
		model.addAttribute("showDadesEntitat", historicFiltreCommand.showingDadesEntitat());
		model.addAttribute("showDadesOrganGestor", historicFiltreCommand.showingDadesOrganGestor());
		model.addAttribute("showDadesUsuari", historicFiltreCommand.showingDadesUsuari());
		model.addAttribute("showDadesInteressat", historicFiltreCommand.showingDadesInteressat());
		model.addAttribute("showingDadesActuals", historicFiltreCommand.getTipusAgrupament() == null);

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

	@RequestMapping(value = "/entitat/actual", method = RequestMethod.GET)
	@ResponseBody
	public List<HistoricExpedientDto> getHistoricExpedientActual(HttpServletRequest request) {
		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		List<HistoricExpedientDto> response = historicService.getDadesActualsEntitat(
				entitat.getId(),
				historicFiltreCommand.asDto());
		return response;
	}

	@RequestMapping(value = "/organgestors", method = RequestMethod.POST)
	@ResponseBody
//	public Map<HistoricMetriquesEnumDto, Map<String, Map<Date, Long>>> getOrgansGestorsMetrics(
	public Map<Date, Map<String, HistoricExpedientDto>> getOrgansGestorsMetrics(
			HttpServletRequest request,
			@RequestParam("metrics[]") HistoricMetriquesEnumDto[] metrics) {
		getEntitatActualComprovantPermisAdminEntitat(request);

		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);

		// registram les mètriques consultades a la sessió
//		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_METRIQUES, metrics);

		Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> dades = historicService.getDadesOrgansGestors(
				historicFiltreCommand.asDto());
		if (dades == null) {
			return new HashMap<>();
		}

		// Substituim els objectes organGestorsDto pel nom de l'organ
		Map<Date, Map<String, HistoricExpedientDto>> response = new HashMap<>();
		for (Date data : dades.keySet()) {
			Map<String, HistoricExpedientDto> mapOrgansGestors = new HashMap<>();
			for (OrganGestorDto organGestor : dades.get(data).keySet()) {
				HistoricExpedientDto historic = dades.get(data).get(organGestor);
				mapOrgansGestors.put(organGestor.getNom(),  historic);
			}
			response.put(data, mapOrgansGestors);
		}

		return response;
	}
	
	@RequestMapping(value = "/organgestors/grouped", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<HistoricExpedientDto>> getHistoricsByOrganGestor(
			HttpServletRequest request,
			@RequestParam("metrics[]") HistoricMetriquesEnumDto[] metrics) {
		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);

		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);

		Map<OrganGestorDto, List<HistoricExpedientDto>> dades = historicService.getHistoricsByOrganGestor(
				historicFiltreCommand.asDto());
		if (dades == null) {
			return new HashMap<>();
		}

		// Substituim els objectes organGestorsDto pel nom de l'organ
		Map<String, List<HistoricExpedientDto>> response = new HashMap<>();
		for (OrganGestorDto organ : dades.keySet()) {
			response.put(organ.getNom(), dades.get(organ));
		}

		return response;
	}


	
	@RequestMapping(value = "/organgestors/actual", method = RequestMethod.POST)
	@ResponseBody
	public Map<HistoricMetriquesEnumDto, Map<String, Long>> getDadesActualsOrgansGestors(
			HttpServletRequest request,
			@RequestParam("metrics[]") HistoricMetriquesEnumDto[] metrics) {
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);

		// registram les mètriques consultades a la sessió
//		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_METRIQUES, metrics);

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
			results.put(codiUsuari, historicService.getDadesUsuari(codiUsuari, historicFiltreCommand.asDto()));
		}

		return results;
	}

	@RequestMapping(value = "/usuaris/actual", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<HistoricUsuariDto>> usuarisDataActual(
			HttpServletRequest request,
			@RequestParam("usuaris[]") String[] usuarisCodi) {

		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_USUARIS, usuarisCodi);

		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		Map<String, List<HistoricUsuariDto>> response = new HashMap<String, List<HistoricUsuariDto>>();
		for (String codiUsuari : usuarisCodi) {
			response.put(codiUsuari, historicService.getDadesActualsUsuari(codiUsuari, historicFiltreCommand.asDto()));
		}
		return response;
	}

	@RequestMapping(value = "/interessats/dades/", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<HistoricInteressatDto>> interessatsData(
			HttpServletRequest request,
			@RequestParam("interessats[]") String[] interessatsDocNum) {
		// registram els usuaris consultats a la sessió
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_INTERESSATS, interessatsDocNum);

		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		Map<String, List<HistoricInteressatDto>> results = new HashMap<String, List<HistoricInteressatDto>>();
		for (String docNum : interessatsDocNum) {
			List<HistoricInteressatDto> historics = historicService.getDadesInteressat(
					docNum,
					historicFiltreCommand.asDto());
			results.put(docNum, historics);
		}

		return results;
	}

	@RequestMapping(value = "/interessats/actual", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<HistoricInteressatDto>> interessatsDataActual(
			HttpServletRequest request,
			@RequestParam("interessats[]") String[] interessatsDocNum) {

		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_INTERESSATS, interessatsDocNum);

		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		Map<String, List<HistoricInteressatDto>> response = new HashMap<String, List<HistoricInteressatDto>>();
		for (String docNum : interessatsDocNum) {
			response.put(docNum, historicService.getDadesActualsInteressat(docNum, historicFiltreCommand.asDto()));
		}
		return response;
	}

	@RequestMapping(value = "/exportar", method = RequestMethod.POST)
	public String export(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam String format) throws Exception {
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);

		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		FitxerDto fitxer = null;
		if (historicFiltreCommand.showingDadesEntitat()) {
			fitxer = exportacioActionHistoric.exportarHistoricEntitat(entitatActual, historicFiltreCommand.asDto(), format);
			
		} else if (historicFiltreCommand.showingDadesOrganGestor()) {
			fitxer = exportacioActionHistoric.exportarHistoricOrgansGestors(historicFiltreCommand.asDto(), format);
			
		} else if (historicFiltreCommand.showingDadesUsuari()) {
			String[] usuaris = (String[])RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_USUARIS);
			usuaris = usuaris == null ? new String[0] : usuaris;
			fitxer = exportacioActionHistoric.exportarHistoricUsuaris(usuaris, historicFiltreCommand.asDto(), format);
			
		} else if (historicFiltreCommand.showingDadesInteressat()) {
			String[] interessats = (String[])RequestSessionHelper.obtenirObjecteSessio(
					request,
					SESSION_ATTRIBUTE_INTERESSATS);
			interessats = interessats == null ? new String[0] : interessats;
			fitxer = exportacioActionHistoric.exportarHistoricInteressats(interessats, historicFiltreCommand.asDto(), format);
			
		} else {
			throw new Exception("No s'han seleccionat el tipus de dades a generar");
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
