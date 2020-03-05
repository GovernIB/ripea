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
import es.caib.ripea.core.api.dto.TipusDocumentalDto;
import es.caib.ripea.core.api.service.TipusDocumentalService;
import es.caib.ripea.war.command.TipusDocumentalCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al manteniment de les meta-dades dels meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/tipusDocumental")
public class TipusDocumentalController extends BaseAdminController {

	@Autowired
	private TipusDocumentalService tipusDocumentalService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		return "tipusDocumentalList";
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				tipusDocumentalService.findByEntitatPaginat(
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
	
	@RequestMapping(value = "/{tipusDocumentalId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long tipusDocumentalId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		TipusDocumentalDto tipusDocumental = null;
		if (tipusDocumentalId != null)
			tipusDocumental = tipusDocumentalService.findById(
					entitatActual.getId(),
					tipusDocumentalId);
		TipusDocumentalCommand command = null;
		if (tipusDocumental != null) {
			command = TipusDocumentalCommand.asCommand(tipusDocumental);
		} else {
			command = new TipusDocumentalCommand();
		}
		model.addAttribute(command);
		return "tipusDocumentalForm";
	}
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid TipusDocumentalCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "tipusDocumentalForm";
		}
		if (command.getId() != null) {
			tipusDocumentalService.update(
					entitatActual.getId(), 
					TipusDocumentalCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../tipusDocumental",
					"tipusdocumental.controller.modificat.ok");
		} else {
			tipusDocumentalService.create(
					entitatActual.getId(), 
					TipusDocumentalCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../tipusDocumental",
					"tipusdocumental.controller.creat.ok");
		}
	}
	
	@RequestMapping(value = "/{tipusDocumentalId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long tipusDocumentalId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		tipusDocumentalService.delete(
				entitatActual.getId(),
				tipusDocumentalId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../tipusDocumental",
				"tipusdocumental.controller.esborrat.ok");
	}

}
