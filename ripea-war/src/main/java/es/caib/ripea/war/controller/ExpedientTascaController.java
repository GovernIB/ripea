/**
 * 
 */
package es.caib.ripea.war.controller;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientTascaComentariDto;
import es.caib.ripea.core.api.dto.ExpedientTascaDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.ExpedientTascaService;
import es.caib.ripea.war.command.ExpedientTascaCommand;
import es.caib.ripea.war.command.TascaReassignarCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.RolHelper;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Controlador per al llistat d'expedients tasques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/expedientTasca")
public class ExpedientTascaController extends BaseUserOAdminOOrganController {

	@Autowired
	private ExpedientTascaService expedientTascaService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private AplicacioService aplicacioService;


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
				expedientId, null);
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
				expedientTascaDto.getExpedient().getId(), null);
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
			model.addAttribute(
					"expedientId",
					expedientId);
			model.addAttribute(
					"expedientTascaCommand",
					expedientTascaCommand);
			ExpedientDto expedientDto = expedientService.findById(
					entitatActual.getId(),
					expedientId, null);
			model.addAttribute(
					"metaexpTasques",
					expedientTascaService.findAmbMetaExpedient(
							entitatActual.getId(),
							expedientDto.getMetaExpedient().getId()));
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

	@RequestMapping(value = "/{expedientTascaId}/reassignar", method = RequestMethod.GET)
	public String reassignar(
			HttpServletRequest request,
			@PathVariable Long expedientTascaId,
			Model model) {
		getEntitatActualComprovantPermisos(request);

		TascaReassignarCommand command = new TascaReassignarCommand();
		model.addAttribute(command);
		
		return "expedientTascaReassignar";
	}
	
	@RequestMapping(value = "/{expedientTascaId}/reassignar", method = RequestMethod.POST)
	public String reassignarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTascaId,
			@Valid TascaReassignarCommand command,
			BindingResult bindingResult,
			Model model) {
		
		getEntitatActualComprovantPermisos(request);
		
		if (bindingResult.hasErrors()) {
			return "expedientTascaReassignar";
		}
	
		expedientTascaService.updateResponsables(expedientTascaId, command.getUsuariCodi());
		
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:/expedientTasca",
				"expedient.tasca.controller.reassignat.ok");
	}

	@RequestMapping(value = "/{expedientTascaId}/comentaris", method = RequestMethod.GET)
	public String comentaris(
			HttpServletRequest request,
			@PathVariable Long expedientTascaId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		ExpedientTascaDto expedientTascaDto = expedientTascaService.findOne(expedientTascaId);
		model.addAttribute("expedientTasca", expedientTascaDto);
		boolean hasWritePermisions = expedientService.hasWritePermission(expedientTascaDto.getExpedient().getId());
		model.addAttribute("hasWritePermisions", hasWritePermisions);
		model.addAttribute("usuariActual", aplicacioService.getUsuariActual());
		return "expedientTascaComentaris";
	}

	@RequestMapping(value = "/{expedientTascaId}/comentaris/publicar", method = RequestMethod.POST)
	@ResponseBody
	public List<ExpedientTascaComentariDto> publicarComentari(
			HttpServletRequest request,
			@PathVariable Long expedientTascaId,
			@RequestParam String text,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		if (text != null && !text.isEmpty()) {
			expedientTascaService.publicarComentariPerExpedientTasca(
					entitatActual.getId(),
					expedientTascaId,
					text,
					RolHelper.getRolActual(request));
		}

		return expedientTascaService.findComentarisPerTasca(entitatActual.getId(), expedientTascaId);
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
