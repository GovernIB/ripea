/**
 * 
 */
package es.caib.ripea.war.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador per al manteniment d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/contingut")
public class ContingutExpedientController extends BaseUserController {

//	@Autowired
//	private ExpedientService expedientService;
//	@Autowired
//	private MetaExpedientService metaExpedientService;
//
//	@RequestMapping(value = "/{contingutId}/expedient/new", method = RequestMethod.GET)
//	public String get(
//			HttpServletRequest request,
//			@PathVariable Long contingutId,
//			Model model) {
//		model.addAttribute("mantenirPaginacio", true);
//		return get(request, contingutId, null, model);
//	}
//	@RequestMapping(value = "/{contingutId}/expedient/{expedientId}", method = RequestMethod.GET)
//	public String get(
//			HttpServletRequest request,
//			@PathVariable Long contingutId,
//			@PathVariable Long expedientId,
//			Model model) {
//		model.addAttribute("mantenirPaginacio", true);
//		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
//		ExpedientDto expedient = null;
//		if (expedientId != null) {
//			expedient = expedientService.findById(
//					entitatActual.getId(),
//					expedientId);
//		}
//		int any = Calendar.getInstance().get(Calendar.YEAR);
//		ExpedientCommand command = null;
//		if (expedient != null) {
//			command = ExpedientCommand.asCommand(expedient);
//		} else {
//			command = new ExpedientCommand();
//			command.setAny(any);
//		}
//		command.setEntitatId(entitatActual.getId());
//		command.setPareId(contingutId);
//		model.addAttribute(command);
//		model.addAttribute(
//				"metaExpedients",
//				metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId()));
//		return "contingutExpedientForm";
//	}
//	@RequestMapping(value = "/{contingutId}/expedient/new", method = RequestMethod.POST)
//	public String postNew(
//			HttpServletRequest request,
//			@PathVariable Long contingutId,
//			@Validated({Create.class}) ExpedientCommand command,
//			BindingResult bindingResult,
//			Model model) throws IOException {
//		model.addAttribute("mantenirPaginacio", true);
//		return postUpdate(
//				request,
//				contingutId,
//				command,
//				bindingResult,
//				model);
//	}
//	@RequestMapping(value = "/{contingutId}/expedient/update", method = RequestMethod.POST)
//	public String postUpdate(
//			HttpServletRequest request,
//			@PathVariable Long contingutId,
//			@Validated({Update.class}) ExpedientCommand command,
//			BindingResult bindingResult,
//			Model model) {
//		model.addAttribute("mantenirPaginacio", true);
//		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
//		if (bindingResult.hasErrors()) {
//			model.addAttribute(
//					"metaExpedients",
//					metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId()));
//			return "contingutExpedientForm";
//		}
//		if (command.getId() == null) {
//			expedientService.create(
//					entitatActual.getId(),
//					contingutId,
//					command.getMetaNodeId(),
//					command.getAny(),
//					command.getSequencia(),
//					command.getNom(),
//					null,
//					false);
//			return getModalControllerReturnValueSuccess(
//					request,
//					"redirect:../../../contingut/" + contingutId,
//					"expedient.controller.creat.ok");
//		} else {
//			expedientService.update(
//					entitatActual.getId(),
//					command.getId(),
//					command.getNom());
//			return getModalControllerReturnValueSuccess(
//					request,
//					"redirect:../../../contingut/" + contingutId,
//					"expedient.controller.modificat.ok");
//		}
//	}

}
