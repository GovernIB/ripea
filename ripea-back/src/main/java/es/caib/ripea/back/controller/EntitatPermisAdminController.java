package es.caib.ripea.back.controller;

import es.caib.ripea.back.command.PermisCommand;
import es.caib.ripea.back.helper.DatatablesHelper;
import es.caib.ripea.back.helper.MissatgesHelper;
import es.caib.ripea.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.service.EntitatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

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
				getEntitatActualComprovantPermisAdminEntitat(request));
		return "adminPermis";
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				entitatService.findPermisAdmin(entitatActual.getId()),
				"id");
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			Model model) {
		return get(request, null, model);
	}
	@RequestMapping(value = "/{permisId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long permisId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		PermisDto permis = getPermisAdminById(entitatActual, permisId);

		if (permis != null)
			model.addAttribute(PermisCommand.asCommand(permis));
		else
			model.addAttribute(new PermisCommand());

		return "adminPermisForm";
	}

	private PermisDto getPermisAdminById(EntitatDto entitatActual, Long permisId) {
		if (permisId != null) {
			List<PermisDto> permisos = entitatService.findPermisAdmin(
					entitatActual.getId());
			for (PermisDto p: permisos) {
				if (p.getId().equals(permisId)) {
					return p;
				}
			}
		}
		return null;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		if (bindingResult.hasErrors()) {
			request.getSession().setAttribute(MissatgesHelper.SESSION_ATTRIBUTE_BINDING_ERRORS, bindingResult.getGlobalErrors());
			return "adminPermisForm";
		}
		entitatService.updatePermisAdmin(
				entitatActual.getId(),
				PermisCommand.asDto(command));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:permis",
				"entitat.controller.permis.modificat.ok",
				new Object[] { command.getPrincipalTipus()+ " "+command.getPrincipalNom() });
	}

	@RequestMapping(value = "/{permisId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long permisId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		PermisDto permis = getPermisAdminById(entitatActual, permisId);
		entitatService.deletePermisAdmin(
				entitatActual.getId(),
				permisId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../permis",
				"entitat.controller.permis.esborrat.ok",
				new Object[] { permis.getPrincipalTipus()+ " "+permis.getPrincipalNom() });
	}

}
