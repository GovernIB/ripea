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
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.war.command.PermisCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al manteniment de permisos de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/expedientEstat")
public class ExpedientEstatPermisController extends BaseAdminController {

	@Autowired
	private ExpedientService expedientService;

	@RequestMapping(value = "/{expedientEstatId}/permis", method = RequestMethod.GET)
	public String permis(
			HttpServletRequest request,
			@PathVariable Long expedientEstatId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"expedientEstat",
				expedientService.findExpedientEstatById(
						entitatActual.getId(),
						expedientEstatId));
		return "expedientEstatPermisList";
	}
	@RequestMapping(value = "/{expedientEstatId}/permis/datatable", method = RequestMethod.GET)
		@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientEstatId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				expedientService.estatPermisFind(
						entitatActual.getId(),
						expedientEstatId),
				"id");
	}

	@RequestMapping(value = "/{expedientEstatId}/permis/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			@PathVariable Long expedientEstatId,
			Model model) {
		return get(request, expedientEstatId, null, model);
	}
	@RequestMapping(value = "/{expedientEstatId}/permis/{permisId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long expedientEstatId,
			@PathVariable Long permisId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"expedientEstat",
				expedientService.findExpedientEstatById(
						entitatActual.getId(),
						expedientEstatId));
		PermisDto permis = null;
		if (permisId != null) {
			List<PermisDto> permisos = expedientService.estatPermisFind(
					entitatActual.getId(),
					expedientEstatId);
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
		return "expedientEstatPermisForm";
	}

	@RequestMapping(value = "/{expedientEstatId}/permis", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long expedientEstatId,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"entitat",
					expedientService.findById(
							entitatActual.getId(),
							expedientEstatId));
			return "metaExpedientPermisForm";
		}
		expedientService.estatPermisUpdate(
				entitatActual.getId(),
				expedientEstatId,
				PermisCommand.asDto(command));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../expedientEstat/" + expedientEstatId + "/permis",
				"metaexpedient.controller.permis.modificat.ok");
	}

	@RequestMapping(value = "/{expedientEstatId}/permis/{permisId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long expedientEstatId,
			@PathVariable Long permisId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		expedientService.estatPermisDelete(
				entitatActual.getId(),
				expedientEstatId,
				permisId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../../expedientEstat/" + expedientEstatId + "/permis",
				"metaexpedient.controller.permis.esborrat.ok");
	}

}
