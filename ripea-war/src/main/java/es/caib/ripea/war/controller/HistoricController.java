package es.caib.ripea.war.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.service.HistoricService;
import es.caib.ripea.core.api.service.MetaExpedientService;
//import es.caib.ripea.war.command.ExpedientFiltreCommand;
import es.caib.ripea.war.command.HistoricFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.RequestSessionHelper;

@Controller
@RequestMapping("/historic")
public class HistoricController extends BaseAdminController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "HistoricController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "HistoricController.session.seleccio";

	@Autowired
	private HistoricService historicService;
	@Autowired
	private MetaExpedientService metaExpedientService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		model.addAttribute(historicFiltreCommand);
//		List<MetaExpedientDto> metaExpedients = metaExpedientService.findByEntitat(entitat.getId());
//		model.addAttribute("historicEntitat", 
//				historicService.getDadesEntitat(
//						entitat.getId(), 
//						(new LocalDate()).minusYears(10).toDate(), 
//						(new LocalDate()).plusYears(10).toDate(), 
//						metaExpedients));
		return "historic";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid HistoricFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE);
//			RequestSessionHelper.esborrarObjecteSessio(
//					request,
//					SESSION_ATTRIBUTE_SELECCIO);
			
			return "redirect:historic";
		} 
		if (!bindingResult.hasErrors()) {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
//			if (metaExpedientId == null || !metaExpedientId.equals(filtreCommand.getMetaExpedientId())) {
//				RequestSessionHelper.esborrarObjecteSessio(
//						request,
//						SESSION_ATTRIBUTE_SELECCIO);
//				RequestSessionHelper.actualitzarObjecteSessio(
//						request,
//						SESSION_ATTRIBUTE_METAEXP_ID,
//						filtreCommand.getMetaExpedientId());
//			}
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
