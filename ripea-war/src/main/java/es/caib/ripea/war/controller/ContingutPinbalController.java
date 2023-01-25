/**
 * 
 */
package es.caib.ripea.war.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaDocumentPinbalServeiEnumDto;
import es.caib.ripea.core.api.dto.PinbalConsentimentEnumDto;
import es.caib.ripea.core.api.dto.PinbalServeiDocPermesEnumDto;
import es.caib.ripea.core.api.exception.PinbalException;
import es.caib.ripea.core.api.service.DadesExternesService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.war.command.PinbalConsultaCommand;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.EnumHelper.HtmlOption;
import es.caib.ripea.war.helper.ExceptionHelper;

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
	@Autowired
	private DadesExternesService dadesExternesService;
	
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

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		MetaDocumentDto metaDocument = metaDocumentService.findById(entitatActual.getId(), command.getMetaDocumentId());
	
		if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.SVDDELSEXWS01) {
			bindingResult.rejectValue("dataNaixementObligatori", "NotEmpty");
		}
			
		if (bindingResult.hasErrors()) {
			omplirModelFormulari(request, pareId, model);
			return "contingutPinbalForm";
		}
		try {
			documentService.pinbalNovaConsulta(entitatActual.getId(), pareId, command.getMetaDocumentId(), PinbalConsultaCommand.asDto(command));
			return getModalControllerReturnValueSuccess(request, "redirect:../contingut/" + pareId, "pinbal.controller.creat.ok");
		} catch (Exception ex) {
			logger.error("Error en la consulta PINBAL", ex);
			String info = "";
			Exception pinbalExcepcion = ExceptionHelper.findExceptionInstance(ex, PinbalException.class, 3);
			if (pinbalExcepcion != null) {
				String metode = ((PinbalException) pinbalExcepcion).getMetode();
				if (StringUtils.isNotEmpty(metode)) {
					info = " [" + metode + "] ";
				}
			}
			return getModalControllerReturnValueError(request, "redirect:../contingut/" + pareId, "pinbal.controller.creat.error", new String[] {info + ex.getMessage()}, ex);
		}
	}
	
	
	@RequestMapping(value = "/{pareId}/pinbal/titulars/{metaDocumentId}", method = RequestMethod.GET)
	@ResponseBody
	public List<InteressatDto> findTitularsPerTipusDocument(
			HttpServletRequest request,
			@PathVariable Long pareId,
			@PathVariable Long metaDocumentId,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		List<PinbalServeiDocPermesEnumDto> pinbalServeiDocsPermesos = metaDocumentService.findById(
				entitatActual.getId(),
				metaDocumentId).
				getPinbalServeiDocsPermesos();

		List<InteressatDto> interessats = expedientInteressatService.findByExpedient(entitatActual.getId(),
				pareId,
				false);

		if (pinbalServeiDocsPermesos != null && !pinbalServeiDocsPermesos.isEmpty()) {
			Iterator<InteressatDto> itin = interessats.iterator();
			while (itin.hasNext()) {
				InteressatDto interessat = itin.next();
				if (!isInteressatDocumentOk(interessat, pinbalServeiDocsPermesos)) {
					itin.remove();
				} 
			};
		}
		return interessats;
	}
	
	
	private boolean isInteressatDocumentOk(InteressatDto interessat, List<PinbalServeiDocPermesEnumDto> pinbalServeiDocsPermesos) {
		
		if (interessat.getTipus() == InteressatTipusEnumDto.PERSONA_FISICA) {
			
			if (interessat.getDocumentTipus() == InteressatDocumentTipusEnumDto.NIF) {
				if (pinbalServeiDocsPermesos.contains(PinbalServeiDocPermesEnumDto.NIF) || pinbalServeiDocsPermesos.contains(PinbalServeiDocPermesEnumDto.DNI)) {
					return true;
				} else {
					return false;
				}
			} else if (interessat.getDocumentTipus() == InteressatDocumentTipusEnumDto.DOCUMENT_IDENTIFICATIU_ESTRANGERS) {
				if (pinbalServeiDocsPermesos.contains(PinbalServeiDocPermesEnumDto.NIE)) {
					return true;
				} else {
					return false;
				}
			} else if (interessat.getDocumentTipus() == InteressatDocumentTipusEnumDto.PASSAPORT) {
				if (pinbalServeiDocsPermesos.contains(PinbalServeiDocPermesEnumDto.PASSAPORT)) {
					return true;
				} else {
					return false;
				}
			} else
				return false;

		} else if (interessat.getTipus()==InteressatTipusEnumDto.PERSONA_JURIDICA) {
			if (pinbalServeiDocsPermesos.contains(PinbalServeiDocPermesEnumDto.NIF) || pinbalServeiDocsPermesos.contains(PinbalServeiDocPermesEnumDto.CIF)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	

	private void omplirModelFormulari(
			HttpServletRequest request,
			Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List <MetaDocumentDto> metaDocuments = metaDocumentService.findActiusPerCreacio(
				entitatActual.getId(),
				contingutId, 
				null, 
				false);
		Iterator<MetaDocumentDto> itmd = metaDocuments.iterator();
		while (itmd.hasNext()) {
			MetaDocumentDto metaDocument = itmd.next();
			if (!metaDocument.isPinbalActiu()) {
				itmd.remove();
			}
		};
		model.addAttribute("metaDocuments", metaDocuments);
//		List<InteressatDto> interessats = expedientInteressatService.findByExpedient(
//				entitatActual.getId(),
//				contingutId,
//				false);
//		Iterator<InteressatDto> itin = interessats.iterator();
//		while (itin.hasNext()) {
//			InteressatDto interessat = itin.next();
//			if (interessat.getTipus() == InteressatTipusEnumDto.ADMINISTRACIO) {
//				itin.remove();
//			}
//			
//		};
		model.addAttribute("interessats", new ArrayList<>());
		model.addAttribute(
				"consentimentOptions",
				EnumHelper.getOptionsForEnum(
						PinbalConsentimentEnumDto.class,
						"pinbal.consentiment.enum."));
		model.addAttribute(
				"comunitats",
				Arrays.asList(new HtmlOption("04", "Illes Balears")));
		model.addAttribute(
				"provincies",
				Arrays.asList(new HtmlOption("07", "Illes Balears")));
		model.addAttribute(
				"municipis",
				dadesExternesService.findMunicipisPerProvinciaPinbal("07"));
		
		
	}

	
	private static final Logger logger = LoggerFactory.getLogger(ContingutPinbalController.class); 

}
