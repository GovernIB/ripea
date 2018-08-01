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

import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.war.command.PermisCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al manteniment de permisos d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/entitat")
public class EntitatPermisSuperController extends BaseController {

	@Autowired
	private EntitatService entitatService;



	@RequestMapping(value = "/{entitatId}/permis", method = RequestMethod.GET)
	public String permis(
			@PathVariable Long entitatId,
			Model model) {
		model.addAttribute(
				"entitat",
				entitatService.findById(entitatId));
		return "entitatPermis";
	}
	/*@RequestMapping(value = "/{entitatId}/permis/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesPagina<PermisDto> datatable(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			Model model) {
		return PaginacioHelper.getPaginaPerDatatables(
				request,
				entitatService.findPermisSuper(entitatId));
	}*/
	@RequestMapping(value = "/{entitatId}/permis/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long entitatId) {
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				entitatService.findPermisSuper(entitatId));
		return dtr;
	}

	@RequestMapping(value = "/{entitatId}/permis/new", method = RequestMethod.GET)
	public String getNew(
			@PathVariable Long entitatId,
			Model model) {
		return get(entitatId, null, model);
	}
	@RequestMapping(value = "/{entitatId}/permis/{permisId}", method = RequestMethod.GET)
	public String get(
			@PathVariable Long entitatId,
			@PathVariable Long permisId,
			Model model) {
		model.addAttribute(
				"entitat",
				entitatService.findById(entitatId));
		PermisDto permis = null;
		if (permisId != null) {
			List<PermisDto> permisos = entitatService.findPermisSuper(entitatId);
			for (PermisDto p: permisos) {
				if (p.getId().equals(permisId)) {
					permis = p;
					break;
				}
			}
		}
		if (permis != null)
			model.addAttribute(PermisCommand.asCommand(permis));
		else
			model.addAttribute(new PermisCommand());
		return "entitatPermisForm";
	}

	@RequestMapping(value = "/{entitatId}/permis", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"entitat",
					entitatService.findById(entitatId));
			return "entitatPermisForm";
		}
		entitatService.updatePermisSuper(
				entitatId,
				PermisCommand.asDto(command));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../entitat/" + entitatId + "/permis",
				"entitat.controller.permis.modificat.ok");
	}

	@RequestMapping(value = "/{entitatId}/permis/{permisId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@PathVariable Long permisId,
			Model model) {
		entitatService.deletePermisSuper(entitatId, permisId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../../entitat/" + entitatId + "/permis",
				"entitat.controller.permis.esborrat.ok");
	}

}
