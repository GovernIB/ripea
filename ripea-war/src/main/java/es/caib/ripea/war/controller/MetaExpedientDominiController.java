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
import es.caib.ripea.core.api.dto.MetaExpedientDominiDto;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.MetaExpedientDominiCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador pel llistat de tasques del meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaExpedient")
public class MetaExpedientDominiController extends BaseAdminController {

	@Autowired
	private MetaExpedientService metaExpedientService;

	@RequestMapping(value = "/{metaExpedientId}/domini", method = RequestMethod.GET)
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
		return "metaExpedientDomini";
	}

	@RequestMapping(value = "/{metaExpedientId}/domini/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				metaExpedientService.dominiFindPaginatByMetaExpedient(
						entitatActual.getId(),
						metaExpedientId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
		return dtr;
	}

	@RequestMapping(value = "/{metaExpedientId}/domini/new", method = RequestMethod.GET)
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

	@RequestMapping(value = "/{metaExpedientId}/domini/{id}", method = RequestMethod.GET)
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
		MetaExpedientDominiDto domini = null;
		if (id != null) {
			domini = metaExpedientService.dominiFindById(
					entitatActual.getId(),
					metaExpedientId,
					id);
			model.addAttribute(domini);
		}
		MetaExpedientDominiCommand command = null;
		if (domini != null)
			command = MetaExpedientDominiCommand.asCommand(domini);
		else
			command = new MetaExpedientDominiCommand();
		model.addAttribute(command);
		return "metaExpedientDominiForm";
	}

	@RequestMapping(value = "/{metaExpedientId}/domini/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@Valid MetaExpedientDominiCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"metaExpedient",
					metaExpedientService.findById(
							entitatActual.getId(),
							metaExpedientId));
			return "metaExpedientDominiForm";
		}
		if (command.getId() == null) {
			metaExpedientService.dominiCreate(
					entitatActual.getId(),
					metaExpedientId,
					MetaExpedientDominiCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:expedientEstat/" + metaExpedientId,
					"metaexpedient.controller.domini.creada.ok");
		} else {
			metaExpedientService.dominiUpdate(
					entitatActual.getId(),
					metaExpedientId,
					MetaExpedientDominiCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:expedientEstat/" + metaExpedientId,
					"metaexpedient.controller.domini.modificada.ok");
		}
	}

	@RequestMapping(value = "/{metaExpedientId}/domini/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long id) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaExpedientService.dominiDelete(
				entitatActual.getId(),
				metaExpedientId,
				id);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:expedientEstat",
				"metaexpedient.controller.domini.esborrada.ok");
	}

}
