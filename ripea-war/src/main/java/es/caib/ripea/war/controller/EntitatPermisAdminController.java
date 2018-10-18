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
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.war.command.PermisCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al manteniment de permisos de l'entitat
 * actual per a l'usuari administrador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/permis")
public class EntitatPermisAdminController extends BaseAdminController {

	@Autowired
	private EntitatService entitatService;



	@RequestMapping(method = RequestMethod.GET)
	public String permis(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(
				"entitat",
				getEntitatActualComprovantPermisos(request));
		return "adminPermis";
	}
	@RequestMapping(value = "datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				entitatService.findPermisAdmin(entitatActual.getId()),
				"id");
		return dtr;
	}

	@RequestMapping(value = "new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			Model model) {
		return get(request, null, model);
	}
	@RequestMapping(value = "{permisId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long permisId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		PermisDto permis = null;
		if (permisId != null) {
			List<PermisDto> permisos = entitatService.findPermisAdmin(
					entitatActual.getId());
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
		return "adminPermisForm";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "adminPermisForm";
		}
		entitatService.updatePermisAdmin(
				entitatActual.getId(),
				PermisCommand.asDto(command));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:permis",
				"entitat.controller.permis.modificat.ok");
	}

	@RequestMapping(value = "{permisId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long permisId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		entitatService.deletePermisAdmin(
				entitatActual.getId(),
				permisId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../permis",
				"entitat.controller.permis.esborrat.ok");
	}

}
