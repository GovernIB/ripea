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

import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoDocumentalEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesDocumentTipusDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.MetaDocumentCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al manteniment de meta-documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaExpedient")
public class MetaDocumentController extends BaseAdminController {

	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private AplicacioService aplicacioService;
	
	@RequestMapping(value = "/{metaExpedientId}/metaDocument", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"metaExpedient",
				metaExpedientService.findById(
						entitatActual.getId(),
						metaExpedientId));
		return "metaDocumentList";
	}
	@RequestMapping(value = "/{metaExpedientId}/metaDocument/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				metaDocumentService.findByMetaExpedient(
						entitatActual.getId(),
						metaExpedientId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
		return dtr;
	}

	@RequestMapping(value = "/{metaExpedientId}/metaDocument/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		return get(request, metaExpedientId, null, model);
	}
	@RequestMapping(value = "/{metaExpedientId}/metaDocument/{metaDocumentId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDocumentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		MetaDocumentDto metaDocument = null;
		if (metaDocumentId != null) {
			metaDocument = metaDocumentService.findById(
					entitatActual.getId(),
					metaExpedientId,
					metaDocumentId);
		}
		MetaDocumentCommand command = null;
		if (metaDocument != null) {
			command = MetaDocumentCommand.asCommand(metaDocument);
		} else {
			command = new MetaDocumentCommand();
		}
		command.setEntitatId(entitatActual.getId());
		command.setMetaExpedientId(metaExpedientId);
		model.addAttribute(command);
		emplenarModelForm(model);
		return "metaDocumentForm";
	}
	@RequestMapping(value = "/{metaExpedientId}/metaDocument", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@Valid MetaDocumentCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			emplenarModelForm(model);
			return "metaDocumentForm";
		}
		if (command.getId() != null) {
			metaDocumentService.update(
					entitatActual.getId(),
					metaExpedientId,
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
					metaExpedientId,
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

	@RequestMapping(value = "/{metaExpedientId}/metaDocument/{metaDocumentId}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDocumentId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaDocumentService.updateActiu(
				entitatActual.getId(),
				metaExpedientId,
				metaDocumentId,
				true);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDocument",
				"metadocument.controller.activat.ok");
	}
	@RequestMapping(value = "/{metaExpedientId}/metaDocument/{metaDocumentId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDocumentId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaDocumentService.updateActiu(
				entitatActual.getId(),
				metaExpedientId,
				metaDocumentId,
				false);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDocument",
				"metadocument.controller.desactivat.ok");
	}

	@RequestMapping(value = "/{metaExpedientId}/metaDocument/{metaDocumentId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDocumentId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		try{
			metaDocumentService.delete(
					entitatActual.getId(),
					metaExpedientId,
					metaDocumentId);	
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../metaDocument",
					"metadocument.controller.esborrat.ok");
		} catch (Exception exc) {
			if (exc.getCause() != null && exc.getCause().getCause() != null) {
				String excMsg = exc.getCause().getCause().getMessage();
				if (excMsg.contains("ORA-02292")) {
					return getAjaxControllerReturnValueError(
							request, 
							"redirect:../../esborrat",
							"meta.document.noespotesborrar");
				} else {
					return getAjaxControllerReturnValueErrorMessageText(
							request, 
							"redirect:../../esborrat",
							exc.getCause().getCause().getMessage());
				}
			} else {
				return getAjaxControllerReturnValueErrorMessageText(
						request, 
						"redirect:../../metaExpedient",
						exc.getMessage());
			}
		}		
	}

	@RequestMapping(value = "/metaDocument/findAll", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaDocumentDto> findAll(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return metaDocumentService.findByEntitat(entitatActual.getId());
	}

	public void emplenarModelForm(
			Model model) {
		List<PortafirmesDocumentTipusDto> tipus = metaDocumentService.portafirmesFindDocumentTipus();
		model.addAttribute(
				"isPortafirmesDocumentTipusSuportat",
				new Boolean(tipus != null));
		model.addAttribute(
				"portafirmesDocumentTipus",
				tipus);
		//Dades nti
		model.addAttribute(
				"ntiOrigenOptions",
				EnumHelper.getOptionsForEnum(
						NtiOrigenEnumDto.class,
						"document.nti.origen.enum."));
		model.addAttribute(
				"ntiTipusDocumentalOptions",
				EnumHelper.getOptionsForEnum(
						DocumentNtiTipoDocumentalEnumDto.class,
						"document.nti.tipdoc.enum."));
		model.addAttribute(
				"ntiEstatElaboracioOptions",
				EnumHelper.getOptionsForEnum(
						DocumentNtiEstadoElaboracionEnumDto.class,
						"document.nti.estela.enum."));
		model.addAttribute("isFirmaBiometrica", 
				Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.documents.firma.biometrica.activa")));

	}

}
