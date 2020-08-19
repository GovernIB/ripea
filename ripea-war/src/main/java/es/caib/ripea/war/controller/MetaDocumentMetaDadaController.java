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
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.war.command.MetaDadaCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al manteniment de les meta-dades dels meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaDocument")
public class MetaDocumentMetaDadaController extends BaseAdminController {

	@Autowired
	private MetaDadaService metaDadaService;
	@Autowired
	private MetaDocumentService metaDocumentService;

	@RequestMapping(value = "/{metaDocumentId}/metaDada", method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable Long metaDocumentId, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		model.addAttribute("metaDocument", metaDocumentService.findById(entitatActual.getId(), null, metaDocumentId));
		return "metaDadaList";
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, @PathVariable Long metaDocumentId, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				metaDadaService.findByMetaNodePaginat(
						entitatActual.getId(),
						metaDocumentId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
		return dtr;
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, @PathVariable Long metaDocumentId, Model model) {
		return get(request, metaDocumentId, null, model);
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/{metaDadaId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			@PathVariable Long metaDadaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		MetaDadaDto metaDada = null;
		if (metaDadaId != null)
			metaDada = metaDadaService.findById(entitatActual.getId(), metaDocumentId, metaDadaId);
		MetaDadaCommand command = null;
		if (metaDada != null) {
			command = MetaDadaCommand.asCommand(metaDada);
		} else {
			command = new MetaDadaCommand();
		}
		command.setEntitatId(entitatActual.getId());
		command.setMetaNodeId(metaDocumentId);
		model.addAttribute(command);
		return "metaDadaForm";
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			@Valid MetaDadaCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		if (bindingResult.hasErrors()) {
			return "metaDadaForm";
		}
		if (command.getId() != null) {
			metaDadaService.update(entitatActual.getId(), metaDocumentId, MetaDadaCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaDada",
					"metadada.controller.modificat.ok");
		} else {
			metaDadaService.create(entitatActual.getId(), metaDocumentId, MetaDadaCommand.asDto(command));
			return getModalControllerReturnValueSuccess(request, "redirect:metaDada", "metadada.controller.creat.ok");
		}
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/{metaDadaId}/enable", method = RequestMethod.GET)
	public String enable(HttpServletRequest request, @PathVariable Long metaDocumentId, @PathVariable Long metaDadaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		metaDadaService.updateActiva(entitatActual.getId(), metaDocumentId, metaDadaId, true);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDada",
				"metadada.controller.activada.ok");
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/{metaDadaId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			@PathVariable Long metaDadaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		metaDadaService.updateActiva(entitatActual.getId(), metaDocumentId, metaDadaId, false);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDada",
				"metadada.controller.desactivada.ok");
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/{metaDadaId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long metaDocumentId, @PathVariable Long metaDadaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		metaDadaService.delete(entitatActual.getId(), metaDocumentId, metaDadaId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDada",
				"metadada.controller.esborrat.ok");
	}

}
