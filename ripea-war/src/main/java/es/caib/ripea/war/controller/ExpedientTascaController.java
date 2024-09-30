/**
 * 
 */
package es.caib.ripea.war.controller;

import es.caib.ripea.core.api.dto.*;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.ExpedientTascaService;
import es.caib.ripea.war.command.ExpedientTascaCommand;
import es.caib.ripea.war.command.TascaDataLimitCommand;
import es.caib.ripea.war.command.TascaReassignarCommand;
import es.caib.ripea.war.command.TascaReobrirCommand;
import es.caib.ripea.war.helper.*;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
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
import java.util.ArrayList;
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
						expedientId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
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

	@RequestMapping(value = "/{expedientTascaId}/canviarPrioritat", method = RequestMethod.GET)
	public String canviarPrioritatGet(
			HttpServletRequest request,
			@PathVariable Long expedientTascaId,
			Model model) {
		ExpedientTascaDto expedientTascaDto = expedientTascaService.findOne(expedientTascaId);
		model.addAttribute(expedientTascaDto);
		return "tascaChoosePrioritatForm";
	}

	@RequestMapping(value = "/canviarPrioritat", method = RequestMethod.POST)
	public String canviarPrioritatPost(
			HttpServletRequest request,
			ExpedientTascaDto command,
			BindingResult bindingResult,
			Model model) {
		expedientTascaService.changeTascaPrioritat(command);
		MissatgesHelper.success(request, getMessage(request, "tasca.controller.prioritatModificat.ok", new Object[]{command.getTitol()}));
		return modalUrlTancar();
//		return getModalControllerReturnValueSuccess(
//				request,
//				"redirect:../expedient",
//				"expedient.controller.prioritatModificat.ok");
	}

	@RequestMapping(value = "/{expedientTascaId}/cancellar", method = RequestMethod.GET)
	public String expedientTascaCancellar(
			HttpServletRequest request,
			@PathVariable Long expedientTascaId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		
		expedientTascaService.canviarTascaEstat(
				expedientTascaId,
				TascaEstatEnumDto.CANCELLADA,
				null,
				RolHelper.getRolActual(request));
		
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
			model.addAttribute("errorsValidacio", true);
			return "expedientTascaForm";
		}
		if (expedientTascaCommand!=null && expedientTascaCommand.getDuracio()!=null) {
			String duracio = expedientTascaCommand.getDuracio().toLowerCase().trim();
			if (!duracio.endsWith("h") && !duracio.endsWith("d")) {
				duracio += "d";
			}
			expedientTascaCommand.setDuracio(duracio);
		}
		expedientTascaService.createTasca(
				entitatActual.getId(),
				expedientId,
				ExpedientTascaCommand.asDto(expedientTascaCommand));

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
	
		expedientTascaService.updateResponsables(expedientTascaId, command.getResponsablesCodi());
		
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:/expedientTasca",
				"expedient.tasca.controller.reassignat.ok");
	}
	
	@RequestMapping(value = "/{expedientTascaId}/reobrir", method = RequestMethod.GET)
	public String reobrir(
			HttpServletRequest request,
			@PathVariable Long expedientTascaId,
			Model model) {
		getEntitatActualComprovantPermisos(request);

		TascaReobrirCommand command = new TascaReobrirCommand();
		ExpedientTascaDto expedientTascaDto = expedientTascaService.findOne(expedientTascaId);

		command.setResponsablesCodi(new ArrayList<String>());
		
		ExpedientTascaCommand expedientTasca = ExpedientTascaCommand.asCommand(expedientTascaDto);
		
		command.setResponsablesCodi(expedientTasca.getResponsablesCodi());
		
		model.addAttribute(command);
		
		return "expedientTascaReobrir";
	}
	
	@RequestMapping(value = "/{expedientTascaId}/reobrir", method = RequestMethod.POST)
	public String reobrirPost(
			HttpServletRequest request,
			@PathVariable Long expedientTascaId,
			@Valid TascaReobrirCommand command,
			BindingResult bindingResult,
			Model model) {
		
		getEntitatActualComprovantPermisos(request);
		
		if (bindingResult.hasErrors()) {
			return "expedientTascaReobrir";
		}
	
		expedientTascaService.reobrirTasca(
				expedientTascaId, 
				command.getResponsablesCodi(), 
				command.getMotiu(),
				RolHelper.getRolActual(request));
		
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:/expedientTasca",
				"expedient.tasca.controller.reobrir.ok");
	}
	
	@RequestMapping(value = "/{expedientTascaId}/datalimit", method = RequestMethod.GET)
	public String datalimit(
			HttpServletRequest request,
			@PathVariable Long expedientTascaId,
			Model model) {
		getEntitatActualComprovantPermisos(request);

		ExpedientTascaDto expedientTascaDto = expedientTascaService.findOne(expedientTascaId);
		
		TascaDataLimitCommand command = new TascaDataLimitCommand();
		command.setDataLimit(expedientTascaDto.getDataLimit());
		
		model.addAttribute(command);
		
		return "expedientTascaDataLimit";
	}
	
	@RequestMapping(value = "/{expedientTascaId}/datalimit", method = RequestMethod.POST)
	public String datalimitPost(
			HttpServletRequest request,
			@PathVariable Long expedientTascaId,
			@Valid TascaDataLimitCommand command,
			BindingResult bindingResult,
			Model model) {
		
		getEntitatActualComprovantPermisos(request);
		
		if (bindingResult.hasErrors()) {
			return "expedientTascaDataLimit";
		}
	
		expedientTascaService.updateDataLimit(expedientTascaId, command.getDataLimit());
		
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:/expedientTasca",
				"expedient.tasca.controller.dataLimit.ok");
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
