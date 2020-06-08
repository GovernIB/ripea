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
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientTascaDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.ExpedientTascaService;
import es.caib.ripea.war.command.ExpedientTascaCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al llistat d'expedients tasques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/expedientTasca")
public class ExpedientTascaController extends BaseUserController {

//	private static final String SESSION_ATTRIBUTE_FILTRE = "ExpedientTascaController.session.filtre";

	@Autowired
	private ExpedientTascaService expedientTascaService;
	@Autowired
	private ExpedientService expedientService;


//	@RequestMapping(method = RequestMethod.GET)
//	public String get(
//			HttpServletRequest request,
//			Model model) {
//		model.addAttribute(
//				getFiltreCommand(request));
//		return "expedientTascaList";
//	}

//	@RequestMapping(method = RequestMethod.POST)
//	public String post(
//			HttpServletRequest request,
//			ExpedientPeticioFiltreCommand filtreCommand,
//			BindingResult bindingResult,
//			Model model,
//			@RequestParam(value = "accio", required = false) String accio) {
//		getEntitatActualComprovantPermisos(request);
//		if ("netejar".equals(accio)) {
//			RequestSessionHelper.esborrarObjecteSessio(
//					request,
//					SESSION_ATTRIBUTE_FILTRE);
//		} else {
//			if (!bindingResult.hasErrors()) {
//				RequestSessionHelper.actualitzarObjecteSessio(
//						request,
//						SESSION_ATTRIBUTE_FILTRE,
//						filtreCommand);
//			}
//		}
//		return "redirect:expedientTasca";
//	}

	@RequestMapping(value = "/{expedientId}/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		return DatatablesHelper.getDatatableResponse(
				request,
				expedientTascaService.findAmbExpedient(
						entitatActual.getId(),
						expedientId));		
	}
	
	
	
	@RequestMapping(value = "/{expedientId}/new", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientTascaCommand expedientTascaCommand = new ExpedientTascaCommand();

		model.addAttribute(
				"expedientId",
				expedientId);
		model.addAttribute(
				"expedientTascaCommand",
				expedientTascaCommand);
		ExpedientDto expedientDto = expedientService.findById(
				entitatActual.getId(),
				expedientId);
		model.addAttribute(
				"metaexpTasques",
				expedientTascaService.findAmbMetaExpedient(
						entitatActual.getId(),
						expedientDto.getMetaExpedient().getId()));

		return "expedientTascaForm";
	}
	

	
	@RequestMapping(value = "/{expedientTascaId}/detall", method = RequestMethod.GET)
	public String getExpedientTascaDetall(
			HttpServletRequest request,
			@PathVariable Long expedientTascaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientTascaDto expedientTascaDto = expedientTascaService.findOne(expedientTascaId);
		expedientService.findById(
				entitatActual.getId(),
				expedientTascaDto.getExpedient().getId());
		model.addAttribute(
				"expedientTascaDto",
				expedientTascaDto);
		
		return "expedientTascaDetall";
	}
	
	@RequestMapping(value = "/{expedientTascaId}/cancellar", method = RequestMethod.GET)
	public String expedientTascaCancellar(
			HttpServletRequest request,
			@PathVariable Long expedientTascaId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		expedientTascaService.canviarEstat(expedientTascaId, TascaEstatEnumDto.CANCELLADA, null);
		
		ExpedientTascaDto expedientTascaDto = expedientTascaService.findOne(expedientTascaId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:/contingut/" + expedientTascaDto.getExpedient().getId(),
				"expedient.tasca.controller.cancellada.ok");
		
	}	
	
	
	@RequestMapping(value="/{expedientId}/tasca", method = RequestMethod.POST)
	public String postTasca(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@Valid ExpedientTascaCommand expedientTascaCommand,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		if (bindingResult.hasErrors()) {
			return "expedientTascaForm";
		}

		ExpedientTascaDto expedientTascaDto = expedientTascaService.createTasca(
				entitatActual.getId(),
				expedientId,
				ExpedientTascaCommand.asDto(expedientTascaCommand));
		
		expedientTascaService.enviarEmailCrearTasca(
				expedientTascaDto.getId());
		
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:/expedientTasca",
				"expedient.tasca.controller.creat.ok");
	}
	
	
	@RequestMapping(value = "{metaExpedientTascaId}/getMetaExpedientTasca", method = RequestMethod.GET)
	@ResponseBody
	public MetaExpedientTascaDto findMetaExpedientTascaById(
			HttpServletRequest request,
			@PathVariable Long metaExpedientTascaId,
			Model model) {
		return expedientTascaService.findMetaExpedientTascaById(metaExpedientTascaId);
	}
	
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}
	
	

	
	
//	/**
//	 * Gets filtreCommand from session, if it doesnt exist it creates new one in session
//	 * @param request
//	 * @return 
//	 */
//	private ExpedientPeticioFiltreCommand getFiltreCommand(
//			HttpServletRequest request) {
//		ExpedientPeticioFiltreCommand filtreCommand = (ExpedientPeticioFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
//				request,
//				SESSION_ATTRIBUTE_FILTRE);
//		if (filtreCommand == null) {
//			filtreCommand = new ExpedientPeticioFiltreCommand();
//			RequestSessionHelper.actualitzarObjecteSessio(
//					request,
//					SESSION_ATTRIBUTE_FILTRE,
//					filtreCommand);
//		}
//		return filtreCommand;
//	}


}
