/**
 * 
 */
package es.caib.ripea.war.controller;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJBException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.PinbalConsentimentEnumDto;
import es.caib.ripea.core.api.exception.PinbalException;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.war.command.PinbalConsultaCommand;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.EnumHelper.HtmlOption;

/**
 * Controlador per a la gestió de peticions a PINBAL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/contingut")
public class ContingutPinbalController extends BaseUserOAdminOOrganController {

	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private ExpedientInteressatService expedientInteressatService;
	
	@RequestMapping(value = "/{pareId}/pinbal/new", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long pareId,
			Model model) {
		return get(request, pareId, null, model);
	}
	@RequestMapping(value = "/{pareId}/pinbal/{documentId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long pareId,
			@PathVariable Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		PinbalConsultaCommand command = new PinbalConsultaCommand();
		command.setEntitatId(entitatActual.getId());
		command.setPareId(pareId);
		omplirModelFormulari(
				request,
				pareId,
				model);
		model.addAttribute(command);
		return "contingutPinbalForm";
	}
	@RequestMapping(value = "/{pareId}/pinbal/new", method = RequestMethod.POST)
	public String postNew(
			HttpServletRequest request,
			@PathVariable Long pareId,
			@Valid PinbalConsultaCommand command,
			BindingResult bindingResult,
			Model model) {

		if (bindingResult.hasErrors()) {
			omplirModelFormulari(request, pareId, model);
			return "contingutPinbalForm";
		}
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		try {
			documentService.pinbalNovaConsulta(entitatActual.getId(), pareId, command.getMetaDocumentId(), PinbalConsultaCommand.asDto(command));
			return getModalControllerReturnValueSuccess(request, "redirect:../contingut/" + pareId, "pinbal.controller.creat.ok");
		} catch (Exception ex) {
			logger.error("Error en la consulta PINBAL", ex);
			return getModalControllerReturnValueError(request, "redirect:../contingut/" + pareId, "pinbal.controller.creat.error", new String[] {ex.getMessage()});
		}
	}

	private void omplirModelFormulari(
			HttpServletRequest request,
			Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List <MetaDocumentDto> metaDocuments = metaDocumentService.findActiusPerCreacio(
				entitatActual.getId(),
				contingutId);
		Iterator<MetaDocumentDto> itmd = metaDocuments.iterator();
		while (itmd.hasNext()) {
			MetaDocumentDto metaDocument = itmd.next();
			if (!metaDocument.isPinbalActiu()) {
				itmd.remove();
			}
		};
		model.addAttribute("metaDocuments", metaDocuments);
		List<InteressatDto> interessats = expedientInteressatService.findByExpedient(
				entitatActual.getId(),
				contingutId,
				false);
		Iterator<InteressatDto> itin = interessats.iterator();
		while (itin.hasNext()) {
			InteressatDto interessat = itin.next();
			if (interessat.getTipus() != InteressatTipusEnumDto.PERSONA_FISICA) {
				itin.remove();
			}
		};
		model.addAttribute("interessats", interessats);
		model.addAttribute(
				"consentimentOptions",
				EnumHelper.getOptionsForEnum(
						PinbalConsentimentEnumDto.class,
						"pinbal.consentiment.enum."));
		model.addAttribute(
				"comunitats",
				Arrays.asList(new HtmlOption("07", "Illes Balears")));
		model.addAttribute(
				"provincies",
				Arrays.asList(new HtmlOption("07", "Illes Balears")));
	}

	
	private static final Logger logger = LoggerFactory.getLogger(ContingutPinbalController.class); 

}
