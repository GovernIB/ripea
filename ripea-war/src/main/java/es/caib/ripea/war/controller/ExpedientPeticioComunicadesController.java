/**
 * 
 */
package es.caib.ripea.war.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.core.api.service.ExpedientPeticioService;
import es.caib.ripea.war.command.ExpedientPeticioFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.RequestSessionHelper;


@Controller
@RequestMapping("/expedientPeticioComunicades")
public class ExpedientPeticioComunicadesController extends BaseUserOAdminOOrganController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "ExpedientPeticioComunicadesController.session.filtre";

	@Autowired
	private ExpedientPeticioService expedientPeticioService;


	
	@RequestMapping(method = RequestMethod.GET)
	public String getComunicadas(HttpServletRequest request, Model model) {

		model.addAttribute(getFiltreCommand(request));
		return "expedientPeticioComunicadaList";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String postComunicadas(
			HttpServletRequest request,
			ExpedientPeticioFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {

		getEntitatActualComprovantPermisos(request);
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
			}
		}
		return "redirect:expedientPeticioComunicades";
	}

	
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse comunicadasDatatable(HttpServletRequest request) {

		ExpedientPeticioFiltreCommand expedientPeticioFiltreCommand = getFiltreCommand(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				expedientPeticioService.findComunicadesAmbFiltre(
						ExpedientPeticioFiltreCommand.asDto(expedientPeticioFiltreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
	}
	
	
	@RequestMapping(value = "/reprocessar/{expedientPeticioId}", method = RequestMethod.GET)
	public String comunicadaReprocessar(
			HttpServletRequest request,
			@PathVariable Long expedientPeticioId,
			Model model) {
		try {
			expedientPeticioService.comunicadaReprocessar(expedientPeticioId);
			
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/expedientPeticioComunicades",
					"expedient.peticio.comunicades.descarregar.ok");
		} catch (Exception e) {
			
			return getModalControllerReturnValueErrorMessageText(
					request,
					"redirect:/expedientPeticioComunicades",
					getMessage(request, "expedient.peticio.comunicades.descarregar.error") + ": " + e.getMessage(),
					e);
		}

	}
	

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}

	/**
	 * Gets filtreCommand from session, if it doesnt exist it creates new one in session
	 * @param request
	 * @return 
	 */
	private ExpedientPeticioFiltreCommand getFiltreCommand(HttpServletRequest request) {

		ExpedientPeticioFiltreCommand filtreCommand = (ExpedientPeticioFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ExpedientPeticioFiltreCommand();
			filtreCommand.setEstatAll(ExpedientPeticioEstatEnumDto.CREAT);
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return filtreCommand;
	}
	

	private static final Logger logger = LoggerFactory.getLogger(ExpedientPeticioComunicadesController.class);

}
