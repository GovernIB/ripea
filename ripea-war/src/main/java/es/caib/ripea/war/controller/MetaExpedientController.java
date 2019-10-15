/**
 * 
 */
package es.caib.ripea.war.controller;

import java.util.List;

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
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.MetaExpedientCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al manteniment de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaExpedient")
public class MetaExpedientController extends BaseAdminController {

	@Autowired
	private MetaExpedientService metaExpedientService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		Boolean mantenirPaginacio = Boolean.parseBoolean(request.getParameter("mantenirPaginacio"));
		if(mantenirPaginacio) {
			model.addAttribute("mantenirPaginacio", true);
		} else {
			model.addAttribute("mantenirPaginacio", false);
		}
		return "metaExpedientList";
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				metaExpedientService.findByEntitat(
						entitatActual.getId(),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			Model model) {
		return get(request, null, model);
	}
	@RequestMapping(value = "/{metaExpedientId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		MetaExpedientDto metaExpedient = null;
		if (metaExpedientId != null)
			metaExpedient = metaExpedientService.findById(
					entitatActual.getId(),
					metaExpedientId);
		MetaExpedientCommand command = null;
		if (metaExpedient != null)
			command = MetaExpedientCommand.asCommand(metaExpedient);
		else
			command = new MetaExpedientCommand();
		model.addAttribute(command);
		command.setEntitatId(entitatActual.getId());
		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findByEntitat(entitatActual.getId()));
		return "metaExpedientForm";
	}
	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid MetaExpedientCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "metaExpedientForm";
		}
		if (command.getId() != null) {
			metaExpedientService.update(
					entitatActual.getId(),
					MetaExpedientCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaExpedient",
					"metaexpedient.controller.modificat.ok");
		} else {
			metaExpedientService.create(
					entitatActual.getId(),
					MetaExpedientCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaExpedient",
					"metaexpedient.controller.creat.ok");
		}
	}
	@RequestMapping(value = "/{metaExpedientId}/new", method = RequestMethod.GET)
	public String getNewAmbPare(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		MetaExpedientCommand command = new MetaExpedientCommand();
		command.setPareId(metaExpedientId);
		command.setEntitatId(entitatActual.getId());
		model.addAttribute(command);
		return "metaExpedientForm";
	}

	@RequestMapping(value = "/{metaExpedientId}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaExpedientService.updateActiu(
				entitatActual.getId(),
				metaExpedientId,
				true);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.activat.ok");
	}
	@RequestMapping(value = "/{metaExpedientId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaExpedientService.updateActiu(
				entitatActual.getId(),
				metaExpedientId,
				false);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.desactivat.ok");
	}

	@RequestMapping(value = "/{metaExpedientId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		try{
			metaExpedientService.delete(
					entitatActual.getId(),
					metaExpedientId);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../metaExpedient",
					"metaexpedient.controller.esborrat.ok");
		} catch (Exception exc) {
			if (exc.getCause() != null && exc.getCause().getCause() != null) {
				String excMsg = exc.getCause().getCause().getMessage();
				if (excMsg.contains("ORA-02292")) {
					return getAjaxControllerReturnValueError(
							request, 
							"redirect:../../esborrat",
							"meta.expedient.noespotesborrar");
				} else {
					return getAjaxControllerReturnValueErrorMessageText(
							request, 
							"redirect:../../esborrat",
							exc.getCause().getCause().getMessage());
				}
			} else {
				return getAjaxControllerReturnValueErrorMessageText(
						request, 
						"redirect:../../metaExpedient",
						exc.getMessage());
			}
		}
	}

	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaExpedientDto> findAll(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return metaExpedientService.findByEntitat(entitatActual.getId());
	}

	@RequestMapping(value = "/{metaExpedientId}/proximNumeroSequencia/{any}", method = RequestMethod.GET)
	@ResponseBody
	public long proximNumeroSequencia(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable int any) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return metaExpedientService.getProximNumeroSequencia(
				entitatActual.getId(),
				metaExpedientId,
				any);
	}

}