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
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.war.command.PermisCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al manteniment de permisos de meta-documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaDocument")
public class MetaDocumentPermisController extends BaseAdminController {

	@Autowired
	private MetaDocumentService metaDocumentService;



	@RequestMapping(value = "/{metaDocumentId}/permis", method = RequestMethod.GET)
	public String permis(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"metaDocument",
				metaDocumentService.findById(
						entitatActual.getId(),
						metaDocumentId));
		return "metaDocumentPermis";
	}
	@RequestMapping(value = "/{metaDocumentId}/permis/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				metaDocumentService.findPermis(
						entitatActual.getId(),
						metaDocumentId),
				"id");
	}

	@RequestMapping(value = "/{metaDocumentId}/permis/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			Model model) {
		return get(request, metaDocumentId, null, model);
	}
	@RequestMapping(value = "/{metaDocumentId}/permis/{permisId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			@PathVariable Long permisId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"metaDocument",
				metaDocumentService.findById(
						entitatActual.getId(),
						metaDocumentId));
		PermisDto permis = null;
		if (permisId != null) {
			List<PermisDto> permisos = metaDocumentService.findPermis(
					entitatActual.getId(),
					metaDocumentId);
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
		return "metaDocumentPermisForm";
	}

	@RequestMapping(value = "/{metaDocumentId}/permis", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"entitat",
					metaDocumentService.findById(
							entitatActual.getId(),
							metaDocumentId));
			return "metaDocumentPermisForm";
		}
		metaDocumentService.updatePermis(
				entitatActual.getId(),
				metaDocumentId,
				PermisCommand.asDto(command));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../metaDocument/" + metaDocumentId + "/permis",
				"metadocument.controller.permis.modificat.ok");
	}

	@RequestMapping(value = "/{metaDocumentId}/permis/{permisId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			@PathVariable Long permisId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaDocumentService.deletePermis(
				entitatActual.getId(),
				metaDocumentId,
				permisId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../../metaDocument/" + metaDocumentId + "/permis",
				"metadocument.controller.permis.esborrat.ok");
	}

}
