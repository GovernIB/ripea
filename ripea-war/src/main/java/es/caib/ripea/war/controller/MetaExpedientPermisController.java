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
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.PermisCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al manteniment de permisos de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaExpedient")
public class MetaExpedientPermisController extends BaseAdminController {

	@Autowired
	private MetaExpedientService metaExpedientService;

	@RequestMapping(value = "/{metaExpedientId}/permis", method = RequestMethod.GET)
	public String permis(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrgan(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		model.addAttribute(
				"metaExpedient",
				metaExpedientService.findById(
						entitatActual.getId(),
						metaExpedientId));
		return "metaExpedientPermis";
	}
	@RequestMapping(value = "/{metaExpedientId}/permis/datatable", method = RequestMethod.GET)
		@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrgan(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		return DatatablesHelper.getDatatableResponse(
				request,
				metaExpedientService.permisFind(
						entitatActual.getId(),
						metaExpedientId),
				"id");
	}

	@RequestMapping(value = "/{metaExpedientId}/permis/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		return get(request, metaExpedientId, null, model);
	}
	@RequestMapping(value = "/{metaExpedientId}/permis/{permisId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long permisId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrgan(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		model.addAttribute(
				"metaExpedient",
				metaExpedientService.findById(
						entitatActual.getId(),
						metaExpedientId));
		PermisDto permis = null;
		if (permisId != null) {
			List<PermisDto> permisos = metaExpedientService.permisFind(
					entitatActual.getId(),
					metaExpedientId);
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
		return "metaExpedientPermisForm";
	}

	@RequestMapping(value = "/{metaExpedientId}/permis", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrgan(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"entitat",
					metaExpedientService.findById(
							entitatActual.getId(),
							metaExpedientId));
			return "metaExpedientPermisForm";
		}
		metaExpedientService.permisUpdate(
				entitatActual.getId(),
				metaExpedientId,
				PermisCommand.asDto(command));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient/" + metaExpedientId + "/permis",
				"metaexpedient.controller.permis.modificat.ok");
	}

	@RequestMapping(value = "/{metaExpedientId}/permis/{permisId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long permisId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrgan(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		metaExpedientService.permisDelete(
				entitatActual.getId(),
				metaExpedientId,
				permisId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../../metaExpedient/" + metaExpedientId + "/permis",
				"metaexpedient.controller.permis.esborrat.ok");
	}

}
