/**
 * 
 */
package es.caib.ripea.war.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.MetaExpedientTascaCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador pel llistat de tasques del meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaExpedient")
public class MetaExpedientTascaController extends BaseAdminController {

	@Autowired
	private MetaExpedientService metaExpedientService;

	@RequestMapping(value = "/{metaExpedientId}/tasca", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"metaExpedient",
				metaExpedientService.findById(
						entitatActual.getId(),
						metaExpedientId));
		return "metaExpedientTasca";
	}

	@RequestMapping(value = "/{metaExpedientId}/tasca/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				metaExpedientService.tascaFindPaginatByMetaExpedient(
						entitatActual.getId(),
						metaExpedientId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
		return dtr;
	}

	@RequestMapping(value = "/{metaExpedientId}/tasca/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		return get(
				request,
				metaExpedientId,
				null,
				model);
	}

	@RequestMapping(value = "/{metaExpedientId}/tasca/{id}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long id,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"metaExpedient",
				metaExpedientService.findById(
						entitatActual.getId(),
						metaExpedientId));
		MetaExpedientTascaDto tasca = null;
		if (id != null) {
			tasca = metaExpedientService.tascaFindById(
					entitatActual.getId(),
					metaExpedientId,
					id);
			model.addAttribute(tasca);
		}
		MetaExpedientTascaCommand command = null;
		if (tasca != null)
			command = MetaExpedientTascaCommand.asCommand(tasca);
		else
			command = new MetaExpedientTascaCommand();
		model.addAttribute(command);
		return "metaExpedientTascaForm";
	}

	@RequestMapping(value = "/{metaExpedientId}/tasca/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@Valid MetaExpedientTascaCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"metaExpedient",
					metaExpedientService.findById(
							entitatActual.getId(),
							metaExpedientId));
			return "metaExpedientTascaForm";
		}
		if (command.getId() == null) {
			metaExpedientService.tascaCreate(
					entitatActual.getId(),
					metaExpedientId,
					MetaExpedientTascaCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:expedientEstat/" + metaExpedientId,
					"metaexpedient.controller.tasca.creada.ok");
		} else {
			metaExpedientService.tascaUpdate(
					entitatActual.getId(),
					metaExpedientId,
					MetaExpedientTascaCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:expedientEstat/" + metaExpedientId,
					"metaexpedient.controller.tasca.modificada.ok");
		}
	}

	@RequestMapping(value = "/{metaExpedientId}/tasca/{id}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long id) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaExpedientService.tascaUpdateActiu(
				entitatActual.getId(),
				metaExpedientId,
				id,
				true);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.tasca.activada.ok");
	}
	@RequestMapping(value = "/{metaExpedientId}/tasca/{id}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long id) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaExpedientService.tascaUpdateActiu(
				entitatActual.getId(),
				metaExpedientId,
				id,
				false);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.tasca.desactivada.ok");
	}

	@RequestMapping(value = "/{metaExpedientId}/tasca/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long id) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaExpedientService.tascaDelete(
				entitatActual.getId(),
				metaExpedientId,
				id);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:expedientEstat",
				"metaexpedient.controller.tasca.esborrada.ok");
	}

}