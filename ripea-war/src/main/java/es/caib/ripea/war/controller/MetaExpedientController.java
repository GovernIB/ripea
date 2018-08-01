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
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientMetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaNodeMetaDadaDto;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.MetaExpedientCommand;
import es.caib.ripea.war.command.MetaExpedientMetaDocumentCommand;
import es.caib.ripea.war.command.MetaNodeMetaDadaCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.MissatgesHelper;

/**
 * Controlador per al manteniment de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaExpedient")
public class MetaExpedientController extends BaseAdminController {

	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private MetaDadaService metaDadaService;
	@Autowired
	private MetaDocumentService metaDocumentService;



	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request) {
		getEntitatActualComprovantPermisos(request);
		return "metaExpedientList";
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				metaExpedientService.findByEntitatPaginat(
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
	@RequestMapping(value = "/{metaExpedientId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		MetaExpedientDto metaExpedient = null;
		if (metaExpedientId != null)
			metaExpedient = metaExpedientService.findById(
					entitatActual.getId(),
					metaExpedientId);
		MetaExpedientCommand command = null;
		if (metaExpedient != null)
			command = MetaExpedientCommand.asCommand(metaExpedient);
		else
			command = new MetaExpedientCommand();
		model.addAttribute(command);
		command.setEntitatId(entitatActual.getId());
		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findByEntitat(entitatActual.getId()));
		return "metaExpedientForm";
	}
	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid MetaExpedientCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "metaExpedientForm";
		}
		if (command.getId() != null) {
			metaExpedientService.update(
					entitatActual.getId(),
					MetaExpedientCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaExpedient",
					"metaexpedient.controller.modificat.ok");
		} else {
			metaExpedientService.create(
					entitatActual.getId(),
					MetaExpedientCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaExpedient",
					"metaexpedient.controller.creat.ok");
		}
	}
	@RequestMapping(value = "/{metaExpedientId}/new", method = RequestMethod.GET)
	public String getNewAmbPare(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		MetaExpedientCommand command = new MetaExpedientCommand();
		command.setPareId(metaExpedientId);
		command.setEntitatId(entitatActual.getId());
		model.addAttribute(command);
		return "metaExpedientForm";
	}

	@RequestMapping(value = "/{metaExpedientId}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaExpedientService.updateActiu(
				entitatActual.getId(),
				metaExpedientId,
				true);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.activat.ok");
	}
	@RequestMapping(value = "/{metaExpedientId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaExpedientService.updateActiu(
				entitatActual.getId(),
				metaExpedientId,
				false);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.desactivat.ok");
	}

	@RequestMapping(value = "/{metaExpedientId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaExpedientService.delete(
				entitatActual.getId(),
				metaExpedientId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.esborrat.ok");
	}

	@RequestMapping(value = "/{metaExpedientId}/metaDada", method = RequestMethod.GET)
	public String metaDadaList(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"metaExpedient",
				metaExpedientService.findById(
						entitatActual.getId(),
						metaExpedientId));
		model.addAttribute(
				"metaDadesGlobals",
				metaExpedientService.metaDadaFindGlobals(
						entitatActual.getId()));
		return "metaExpedientMetaDada";
	}
	@RequestMapping(value = "/{metaExpedientId}/metaDada/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse metaDadaDatatable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		MetaExpedientDto metaExpedient = metaExpedientService.findById(
				entitatActual.getId(),
				metaExpedientId);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				metaExpedient.getMetaDades(),
				"id");
		return dtr;
	}

	@RequestMapping(value = "/{metaExpedientId}/metaDada/new", method = RequestMethod.GET)
	public String metaDadaNew(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		return metaDadaGet(
				request,
				metaExpedientId,
				null,
				model);
	}
	@RequestMapping(value = "/{metaExpedientId}/metaDada/{metaNodeMetaDadaId}", method = RequestMethod.GET)
	public String metaDadaGet(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaNodeMetaDadaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		omplirModelFormulariMetaDada(
				request,
				entitatActual,
				metaExpedientId,
				model);
		MetaNodeMetaDadaCommand command = null;
		if (metaNodeMetaDadaId != null) {
			MetaNodeMetaDadaDto metaNodeMetaDada = metaExpedientService.metaDadaFind(
					entitatActual.getId(),
					metaExpedientId,
					metaNodeMetaDadaId);
			model.addAttribute("metaNodeMetaDada", metaNodeMetaDada);
			command = MetaNodeMetaDadaCommand.asCommand(metaNodeMetaDada);
		} else {
			command = new MetaNodeMetaDadaCommand();
		}
		model.addAttribute(command);
		return "metaExpedientMetaDadaForm";
	}
	@RequestMapping(value = "/{metaExpedientId}/metaDada/save", method = RequestMethod.POST)
	public String metaDadaSave(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@Valid MetaNodeMetaDadaCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			omplirModelFormulariMetaDada(
					request,
					entitatActual,
					metaExpedientId,
					model);
			return "metaExpedientMetaDadaForm";
		}
		if (command.getId() == null) {
			metaExpedientService.metaDadaCreate(
					entitatActual.getId(),
					metaExpedientId,
					command.getMetaDadaId(),
					command.getMultiplicitat(),
					command.isReadOnly());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../metaExpedient/" + metaExpedientId + "/metaDada",
					"metaexpedient.controller.metadada.creada.ok");
		} else {
			metaExpedientService.metaDadaUpdate(
					entitatActual.getId(),
					metaExpedientId,
					command.getId(),
					command.getMultiplicitat(),
					command.isReadOnly());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../metaExpedient/" + metaExpedientId + "/metaDada",
					"metaexpedient.controller.metadada.modificada.ok");
		}
	}

	@RequestMapping(value = "/{metaExpedientId}/metaDada/{metaDadaId}/up", method = RequestMethod.GET)
	public String metaDadaUp(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDadaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaExpedientService.metaDadaMoveUp(
				entitatActual.getId(),
				metaExpedientId,
				metaDadaId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../metaDada",
				null);
	}

	@RequestMapping(value = "/{metaExpedientId}/metaDada/{metaDadaId}/down", method = RequestMethod.GET)
	public String metaDadaDown(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDadaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaExpedientService.metaDadaMoveDown(
				entitatActual.getId(),
				metaExpedientId,
				metaDadaId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../metaDada",
				null);
	}

	@RequestMapping(value = "/{metaExpedientId}/metaDada/{metaDadaId}/move/{posicio}", method = RequestMethod.GET)
	public String metaDadaMove(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDadaId,
			@PathVariable int posicio,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaExpedientService.metaDadaMoveTo(
				entitatActual.getId(),
				metaExpedientId,
				metaDadaId,
				posicio);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../metaDada",
				null);
	}

	@RequestMapping(value = "/{metaExpedientId}/metaDada/{metaDadaId}/delete", method = RequestMethod.GET)
	public String metaDadaDelete(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDadaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaExpedientService.metaDadaDelete(
				entitatActual.getId(),
				metaExpedientId,
				metaDadaId);
		MissatgesHelper.success(
				request, 
				getMessage(
						request, 
						"metaexpedient.controller.metadada.esborrada.ok"));
		return "redirect:../../metaDada";
	}

	@RequestMapping(value = "/{metaExpedientId}/metaDocument", method = RequestMethod.GET)
	public String metaDocuments(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"metaExpedient",
				metaExpedientService.findById(
						entitatActual.getId(),
						metaExpedientId));
		return "metaExpedientMetaDocument";
	}
	@RequestMapping(value = "/{metaExpedientId}/metaDocument/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse metaDocumentDatatable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		MetaExpedientDto metaExpedient = metaExpedientService.findById(
				entitatActual.getId(),
				metaExpedientId);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				metaExpedient.getMetaDocuments(),
				"id");
		return dtr;
	}

	@RequestMapping(value = "/{metaExpedientId}/metaDocument/new", method = RequestMethod.GET)
	public String metaDocumentGetNew(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		return metaDocumentGet(
				request,
				metaExpedientId,
				null,
				model);
	}
	@RequestMapping(value = "/{metaExpedientId}/metaDocument/{metaExpedientMetaDocumentId}", method = RequestMethod.GET)
	public String metaDocumentGet(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaExpedientMetaDocumentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		omplirModelFormulariMetaDocument(
				request,
				entitatActual,
				metaExpedientId,
				model);
		MetaExpedientMetaDocumentCommand command = null;
		if (metaExpedientMetaDocumentId != null) {
			MetaExpedientMetaDocumentDto metaExpedientMetaDocument = metaExpedientService.findMetaDocument(
					entitatActual.getId(),
					metaExpedientId,
					metaExpedientMetaDocumentId);
			model.addAttribute("metaExpedientMetaDocument", metaExpedientMetaDocument);
			command = MetaExpedientMetaDocumentCommand.asCommand(metaExpedientMetaDocument);
		} else {
			command = new MetaExpedientMetaDocumentCommand();
		}
		model.addAttribute(command);
		return "metaExpedientMetaDocumentForm";
	}
	@RequestMapping(value = "/{metaExpedientId}/metaDocument/save", method = RequestMethod.POST)
	public String metaDocumentSave(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@Valid MetaExpedientMetaDocumentCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			omplirModelFormulariMetaDocument(
					request,
					entitatActual,
					metaExpedientId,
					model);
			return "metaExpedientMetaDocumentForm";
		}
		if (command.getId() == null) {
			metaExpedientService.metaDocumentCreate(
					entitatActual.getId(),
					metaExpedientId,
					command.getMetaDocumentId(),
					command.getMultiplicitat(),
					command.isReadOnly());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../metaExpedient/" + metaExpedientId + "/metaDocument",
					"metaexpedient.controller.metadocument.creat.ok");
		} else {
			metaExpedientService.metaDocumentUpdate(
					entitatActual.getId(),
					metaExpedientId,
					command.getId(),
					command.getMultiplicitat(),
					command.isReadOnly());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../metaExpedient/" + metaExpedientId + "/metaDocument",
					"metaexpedient.controller.metadocument.modificat.ok");
		}
	}

	@RequestMapping(value = "/{metaExpedientId}/metaDocument/{metaDocumentId}/delete", method = RequestMethod.GET)
	public String metaDocumentDelete(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDocumentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaExpedientService.metaDocumentDelete(
				entitatActual.getId(),
				metaExpedientId,
				metaDocumentId);
		MissatgesHelper.success(
				request, 
				getMessage(
						request, 
						"metaexpedient.controller.metadocument.esborrat.ok"));
		return "redirect:../../metaDocument";
	}

	@RequestMapping(value = "/{metaExpedientId}/metaDocument/move/{metaDocumentId}/{posicio}", method = RequestMethod.GET)
	public String metaDocumentMove(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDocumentId,
			@PathVariable int posicio,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaExpedientService.metaDocumentMove(
				entitatActual.getId(),
				metaExpedientId,
				metaDocumentId,
				posicio);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../metaDocument",
				null);
	}

	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaExpedientDto> findAll(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return metaExpedientService.findByEntitat(entitatActual.getId());
	}



	private void omplirModelFormulariMetaDada(
			HttpServletRequest request,
			EntitatDto entitat,
			Long metaExpedientId,
			Model model) {
		MetaExpedientDto metaExpedient = metaExpedientService.findById(
				entitat.getId(),
				metaExpedientId);
		model.addAttribute("metaExpedient", metaExpedient);
		List<MetaDadaDto> metaDades = metaDadaService.findActiveByEntitat(
				entitat.getId(),
				true,
				false);
		for (MetaNodeMetaDadaDto metaNodeMetaDada: metaExpedient.getMetaDades()) {
			metaDades.remove(metaNodeMetaDada.getMetaDada());
		}
		model.addAttribute("metaDades", metaDades);
	}
	private void omplirModelFormulariMetaDocument(
			HttpServletRequest request,
			EntitatDto entitat,
			Long metaExpedientId,
			Model model) {
		MetaExpedientDto metaExpedient = metaExpedientService.findById(
				entitat.getId(),
				metaExpedientId);
		model.addAttribute("metaExpedient", metaExpedient);
		List<MetaDocumentDto> metaDocuments = metaDocumentService.findByEntitatAndActiveTrue(
				entitat.getId(),
				false);
		for (MetaExpedientMetaDocumentDto metaExpedientMetaDocument: metaExpedient.getMetaDocuments()) {
			metaDocuments.remove(metaExpedientMetaDocument.getMetaDocument());
		}
		model.addAttribute("metaDocuments", metaDocuments);
	}

}
