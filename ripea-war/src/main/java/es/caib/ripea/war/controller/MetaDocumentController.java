/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.IOException;
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
import es.caib.ripea.core.api.dto.MetaNodeMetaDadaDto;
import es.caib.ripea.core.api.dto.PortafirmesDocumentTipusDto;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.war.command.MetaDocumentCommand;
import es.caib.ripea.war.command.MetaNodeMetaDadaCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al manteniment de meta-documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaDocument")
public class MetaDocumentController extends BaseAdminController {

	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private MetaDadaService metaDadaService;



	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request) {
		getEntitatActualComprovantPermisos(request);
		return "metaDocumentList";
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				metaDocumentService.findByEntitatPaginat(
						entitatActual.getId(),
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			Model model) {
		return get(request, null, model);
	}
	@RequestMapping(value = "/{metaDocumentId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		MetaDocumentDto metaDocument = null;
		if (metaDocumentId != null)
			metaDocument = metaDocumentService.findById(
					entitatActual.getId(),
					metaDocumentId);
		MetaDocumentCommand command = null;
		if (metaDocument != null)
			command = MetaDocumentCommand.asCommand(metaDocument);
		else
			command = new MetaDocumentCommand();
		model.addAttribute(command);
		command.setEntitatId(entitatActual.getId());
		List<PortafirmesDocumentTipusDto> tipus = metaDocumentService.findPortafirmesDocumentTipus();
		model.addAttribute(
				"isPortafirmesDocumentTipusSuportat",
				new Boolean(tipus != null));
		model.addAttribute(
				"portafirmesDocumentTipus",
				tipus);
		return "metaDocumentForm";
	}
	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid MetaDocumentCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "metaDocumentForm";
		}
		if (command.getId() != null) {
			metaDocumentService.update(
					entitatActual.getId(),
					MetaDocumentCommand.asDto(command),
					command.getPlantilla().getOriginalFilename(),
					command.getPlantilla().getContentType(),
					command.getPlantilla().getBytes());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaDocument",
					"metadocument.controller.modificat.ok");
		} else {
			metaDocumentService.create(
					entitatActual.getId(),
					MetaDocumentCommand.asDto(command),
					command.getPlantilla().getOriginalFilename(),
					command.getPlantilla().getContentType(),
					command.getPlantilla().getBytes());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaDocument",
					"metadocument.controller.creat.ok");
		}
	}

	@RequestMapping(value = "/{metaDocumentId}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaDocumentService.updateActiu(
				entitatActual.getId(),
				metaDocumentId,
				true);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDocument",
				"metadocument.controller.activat.ok");
	}
	@RequestMapping(value = "/{metaDocumentId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaDocumentService.updateActiu(
				entitatActual.getId(),
				metaDocumentId,
				false);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDocument",
				"metadocument.controller.desactivat.ok");
	}

	@RequestMapping(value = "/{metaDocumentId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaDocumentService.delete(
				entitatActual.getId(),
				metaDocumentId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDocument",
				"metadocument.controller.esborrat.ok");
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada", method = RequestMethod.GET)
	public String metaDadaList(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"metaDocument",
				metaDocumentService.findById(
						entitatActual.getId(),
						metaDocumentId));
		model.addAttribute(
				"metaDadesGlobals",
				metaDocumentService.metaDadaFindGlobals(
						entitatActual.getId()));
		return "metaDocumentMetaDada";
	}
	@RequestMapping(value = "/{metaDocumentId}/metaDada/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse metaDadaDatatable(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		MetaDocumentDto metaDocument = metaDocumentService.findById(
				entitatActual.getId(),
				metaDocumentId);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				metaDocument.getMetaDades(),
				"id");
		return dtr;
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/new", method = RequestMethod.GET)
	public String metaDadaNewGet(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			Model model) {
		return metaDadaGet(
				request,
				metaDocumentId,
				null,
				model);
	}
	@RequestMapping(value = "/{metaDocumentId}/metaDada/{metaNodeMetaDadaId}", method = RequestMethod.GET)
	public String metaDadaGet(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			@PathVariable Long metaNodeMetaDadaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		omplirModelFormulariMetaDada(
				request,
				entitatActual,
				metaDocumentId,
				model);
		MetaNodeMetaDadaCommand command = null;
		if (metaNodeMetaDadaId != null) {
			MetaNodeMetaDadaDto metaNodeMetaDada = metaDocumentService.metaDadaFind(
					entitatActual.getId(),
					metaDocumentId,
					metaNodeMetaDadaId);
			model.addAttribute("metaNodeMetaDada", metaNodeMetaDada);
			command = MetaNodeMetaDadaCommand.asCommand(metaNodeMetaDada);
		} else {
			command = new MetaNodeMetaDadaCommand();
		}
		model.addAttribute(command);
		return "metaDocumentMetaDadaForm";
	}
	@RequestMapping(value = "/{metaDocumentId}/metaDada/save", method = RequestMethod.POST)
	public String metaDadaSave(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			@Valid MetaNodeMetaDadaCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			omplirModelFormulariMetaDada(
					request,
					entitatActual,
					metaDocumentId,
					model);
			return "metaDocumentMetaDadaForm";
		}
		if (command.getId() == null) {
			metaDocumentService.metaDadaCreate(
					entitatActual.getId(),
					metaDocumentId,
					command.getMetaDadaId(),
					command.getMultiplicitat(),
					command.isReadOnly());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../metaDocument/" + metaDocumentId + "/metaDada",
					"metadocument.controller.metadada.creada.ok");
		} else {
			metaDocumentService.metaDadaUpdate(
					entitatActual.getId(),
					metaDocumentId,
					command.getId(),
					command.getMultiplicitat(),
					command.isReadOnly());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../metaDocument/" + metaDocumentId + "/metaDada",
					"metadocument.controller.metadada.modificada.ok");
		}
	}
	@RequestMapping(value = "/{metaDocumentId}/metaDada/{metaNodeMetaDadaId}/delete", method = RequestMethod.GET)
	public String metaDadaRemove(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			@PathVariable Long metaNodeMetaDadaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaDocumentService.metaDadaDelete(
				entitatActual.getId(),
				metaDocumentId,
				metaNodeMetaDadaId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../metaDocument/" + metaDocumentId + "/metaDada",
				"metadocument.controller.metadada.esborrada.ok");
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/{metaDadaId}/up", method = RequestMethod.GET)
	public String metaDadaUp(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			@PathVariable Long metaDadaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaDocumentService.metaDadaMoveUp(
				entitatActual.getId(),
				metaDocumentId,
				metaDadaId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../metaDada",
				null);
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/{metaDadaId}/down", method = RequestMethod.GET)
	public String metaDadaDown(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			@PathVariable Long metaDadaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaDocumentService.metaDadaMoveDown(
				entitatActual.getId(),
				metaDocumentId,
				metaDadaId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../metaDada",
				null);
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/{metaDadaId}/move/{posicio}", method = RequestMethod.GET)
	public String metaDadaMove(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			@PathVariable Long metaDadaId,
			@PathVariable int posicio,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaDocumentService.metaDadaMoveTo(
				entitatActual.getId(),
				metaDocumentId,
				metaDadaId,
				posicio);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../metaDada",
				null);
	}

	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaDocumentDto> findAll(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return metaDocumentService.findByEntitat(entitatActual.getId());
	}



	private void omplirModelFormulariMetaDada(
			HttpServletRequest request,
			EntitatDto entitat,
			Long metaDocumentId,
			Model model) {
		MetaDocumentDto metaDocument = metaDocumentService.findById(
				entitat.getId(),
				metaDocumentId);
		model.addAttribute("metaDocument", metaDocument);
		List<MetaDadaDto> metaDades = metaDadaService.findActiveByEntitat(
				entitat.getId(),
				true,
				false);
		for (MetaNodeMetaDadaDto metaNodeMetaDada: metaDocument.getMetaDades()) {
			metaDades.remove(metaNodeMetaDada.getMetaDada());
		}
		model.addAttribute("metaDades", metaDades);
	}

}
