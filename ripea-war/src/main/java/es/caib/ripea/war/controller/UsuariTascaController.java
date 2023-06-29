/**
 * 
 */
package es.caib.ripea.war.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.api.service.ExpedientTascaService;
import es.caib.ripea.war.command.UsuariTascaRebuigCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.RolHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador per al llistat d'expedients tasques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/usuariTasca")
public class UsuariTascaController extends BaseUserController {



	@Autowired
	private ExpedientTascaService expedientTascaService;


	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		return "usuariTascaList";
	}


	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		return DatatablesHelper.getDatatableResponse(
				request,
				expedientTascaService.findAmbAuthentication(
						entitatActual.getId(), DatatablesHelper.getPaginacioDtoFromRequest(request)));		
	}

	
	@RequestMapping(value = "/{expedientTascaId}/iniciar", method = RequestMethod.GET)
	public String expedientTascaIniciar(
			HttpServletRequest request,
			@PathVariable Long expedientTascaId,
			@RequestParam(value = "redirectATasca", required = false) Boolean redirectATasca,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		expedientTascaService.canviarTascaEstat(
				expedientTascaId,
				TascaEstatEnumDto.INICIADA,
				null,
				RolHelper.getRolActual(request));
		
		return getAjaxControllerReturnValueSuccess(
				request,
				redirectATasca != null && redirectATasca == true ? "redirect:/usuariTasca/" + expedientTascaId + "/tramitar" : "redirect:/usuariTasca",
				"expedient.tasca.controller.iniciada.ok");
		
	}
	
	
	@RequestMapping(value = "/{expedientTascaId}/rebutjar", method = RequestMethod.GET)
	public String getExpedientTascaDetall(
			HttpServletRequest request,
			@PathVariable Long expedientTascaId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		expedientTascaService.findOne(expedientTascaId);
		
		UsuariTascaRebuigCommand command = new UsuariTascaRebuigCommand();
		command.setId(expedientTascaId);
		model.addAttribute(
				"usuariTascaRebuigCommand",
				command);
		
		return "usuariTascaRebuigForm";
	}
	
	@RequestMapping(value = "/rebutjar", method = RequestMethod.POST)
	public String rebutjarPost(
			HttpServletRequest request,
			@Valid UsuariTascaRebuigCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"usuariTascaRebuigCommand",
					command);
			return "usuariTascaRebuigForm";
		}
		
		expedientTascaService.canviarTascaEstat(
				command.getId(),
				TascaEstatEnumDto.REBUTJADA,
				command.getMotiu(), 
				RolHelper.getRolActual(request));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:/usuariTasca",
				"expedient.tasca.controller.rebutjada.ok");
	}
	
	
	

	@RequestMapping(value = "/{expedientTascaId}/finalitzar", method = RequestMethod.GET)
	public String expedientTascaFinalitzar(
			HttpServletRequest request,
			@PathVariable Long expedientTascaId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		expedientTascaService.canviarTascaEstat(
				expedientTascaId,
				TascaEstatEnumDto.FINALITZADA,
				null,
				RolHelper.getRolActual(request));
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:/usuariTasca",
				"expedient.tasca.controller.finalitzada.ok");
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}
	
	


}
