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

import es.caib.ripea.core.api.dto.DominiDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.service.DominiService;
import es.caib.ripea.war.command.DominiCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al manteniment de les meta-dades dels meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/domini")
public class DominiController extends BaseAdminController {

	@Autowired
	private DominiService dominiService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		return "dominiList";
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				dominiService.findByEntitatPaginat(
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
	
	@RequestMapping(value = "/{dominiId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long dominiId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DominiDto domini = null;
		if (dominiId != null)
			domini = dominiService.findById(
					entitatActual.getId(),
					dominiId);
		DominiCommand command = null;
		if (domini != null) {
			command = DominiCommand.asCommand(domini);
			command.setEntitatId(entitatActual.getId());
		} else {
			command = new DominiCommand();
			command.setEntitatId(entitatActual.getId());
		}
		model.addAttribute(command);
		return "dominiForm";
	}
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid DominiCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "dominiForm";
		}
		if (command.getId() != null) {
			dominiService.update(
					entitatActual.getId(), 
					DominiCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../domini",
					"domini.controller.modificat.ok");
		} else {
			dominiService.create(
					entitatActual.getId(), 
					DominiCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../domini",
					"domini.controller.creat.ok");
		}
	}
	
	@RequestMapping(value = "/{dominiId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long dominiId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		dominiService.delete(
				entitatActual.getId(),
				dominiId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../domini",
				"domini.controller.esborrat.ok");
	}
	
	@RequestMapping(value = "/cache/refrescar", method = RequestMethod.GET)
	public String cacheRefrescar(
			HttpServletRequest request) {
		dominiService.evictDominiCache();
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../domini",
				"domini.controller.evict");
	}

}
