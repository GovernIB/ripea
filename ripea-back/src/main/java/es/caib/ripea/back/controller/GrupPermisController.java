package es.caib.ripea.back.controller;

import es.caib.ripea.back.command.PermisCommand;
import es.caib.ripea.back.helper.DatatablesHelper;
import es.caib.ripea.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.service.GrupService;
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


@Controller
@RequestMapping("/grupPermis")
public class GrupPermisController extends BaseAdminController {

	@Autowired private GrupService grupService;

	@RequestMapping(value = "/{grupId}/permis", method = RequestMethod.GET)
	public String permis(@PathVariable Long grupId, Model model) {
		model.addAttribute("grup", grupService.findById(grupId));
		return "grupPermisList";
	}

	@RequestMapping(value = "/{grupId}/permis/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long grupId) {
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				grupService.findPermisos(grupId));
		return dtr;
	}

	@RequestMapping(value = "/{grupId}/permis/new", method = RequestMethod.GET)
	public String getNew(@PathVariable Long grupId, Model model) {
		return get(grupId, null, model);
	}
	
	@RequestMapping(value = "/{grupId}/permis/{permisId}", method = RequestMethod.GET)
	public String get(
			@PathVariable Long grupId,
			@PathVariable Long permisId,
			Model model) {
		model.addAttribute("grup", grupService.findById(grupId));
		PermisDto permis = getPermisGrupById(grupId, permisId);

		if (permis != null)
			model.addAttribute(PermisCommand.asCommand(permis));
		else
			model.addAttribute(new PermisCommand());

		return "grupPermisForm";
	}

	private PermisDto getPermisGrupById(Long grupId, Long permisId) {
		if (permisId != null) {
			List<PermisDto> permisos = grupService.findPermisos(grupId);
			for (PermisDto p: permisos) {
				if (p.getId().equals(permisId)) {
					return p;
				}
			}
		}
		return null;
	}

	@RequestMapping(value = "/{grupId}/permis", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long grupId,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"grup",
					grupService.findById(grupId));
			return "grupPermisForm";
		}
		
		command.setRead(true);
		grupService.updatePermis(
				grupId,
				PermisCommand.asDto(command));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../grup/" + grupId + "/permis",
				"entitat.controller.permis.modificat.ok",
				new Object[] { command.getPrincipalTipus()+ " "+command.getPrincipalNom() });
	}

	@RequestMapping(value = "/{grupId}/permis/{permisId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long grupId,
			@PathVariable Long permisId,
			Model model) {
		PermisDto permis = getPermisGrupById(grupId, permisId);
		grupService.deletePermis(grupId, permisId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../../grup/" + grupId + "/permis",
				"entitat.controller.permis.esborrat.ok",
				new Object[] { permis.getPrincipalTipus()+ " "+permis.getPrincipalNom() });
	}
}