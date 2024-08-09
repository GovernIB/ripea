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
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.war.command.PinbalConsultaCommand;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.EnumHelper.HtmlOption;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.RolHelper;

/**
 * Controlador per a la gestió de peticions a PINBAL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/contingut")
public class ContingutPinbalController extends BaseUserOAdminOOrganController {

	@Autowired private MetaDocumentService metaDocumentService;
	@Autowired private DocumentService documentService;
	@Autowired private ExpedientInteressatService expedientInteressatService;

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

	@RequestMapping(value = "/{pareId}/pinbal/guardaFormSessio", method = RequestMethod.POST)
	@ResponseBody
	public String guardaFormSessio(
			HttpServletRequest request,
			@PathVariable Long pareId,
			PinbalConsultaCommand command) {
		//Guardam en la sessió del usuari actual, el formulari tal i com es troba, no fa falta validar
		request.getSession().setAttribute("ContingutPinbalController.command", command);
		return "OK";
	}

	@RequestMapping(value = "/{pareId}/pinbal/new", method = RequestMethod.POST)
	public String postNew(
			HttpServletRequest request,
			@PathVariable Long pareId,
			@Valid PinbalConsultaCommand command,
			BindingResult bindingResult,
			Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		MetaDocumentDto metaDocument = metaDocumentService.findById(command.getMetaDocumentId());
	
		if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.SVDDELSEXWS01) {
			if (StringUtils.isEmpty(command.getDataNaixement())) {
				bindingResult.rejectValue("dataNaixement", "NotEmpty");
			}
			if (command.getPaisNaixament().equals("724") && StringUtils.isEmpty(command.getMunicipiNaixamentSVDDELSEXWS01())) {
				bindingResult.rejectValue("municipiNaixament", "NotEmpty");
			}
			if (!command.getPaisNaixament().equals("724") && StringUtils.isEmpty(command.getPoblacioNaixament())) {
				bindingResult.rejectValue("poblacioNaixament", "NotEmpty");
			}			
			if (command.getCodiNacionalitat().equals("724") && StringUtils.isEmpty(command.getNomPare()) && StringUtils.isEmpty(command.getNomMare())) {
				bindingResult.rejectValue("nomPare", "NotEmpty");
			}
		} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.NIVRENTI) {
			if (command.getExercici() == null) {
				bindingResult.rejectValue("exercici", "NotEmpty");
			}
		} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.SVDDGPRESIDENCIALEGALDOCWS01) {
			
			if (command.getTipusPassaport() == null && Utils.isEmpty(command.getNumeroSoporte())) {
				bindingResult.reject("contingut.pinbal.form.camp.tipus.numero.soporte.passaport.comment");
			}
			if (command.getTipusPassaport() != null && command.getDataCaducidad() == null) {
				bindingResult.rejectValue("dataCaducidad", "NotEmpty");
			}
			if (command.getTipusPassaport() != null && Utils.isEmpty(command.getCodiNacionalitat())) {
				bindingResult.rejectValue("codiNacionalitat", "NotEmpty");
			}
		} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.SVDRRCCNACIMIENTOWS01) {
			
			if (Utils.isEmpty(command.getRegistreCivil())) {
				bindingResult.rejectValue("registreCivil", "NotEmpty");
			}
			if (Utils.isEmpty(command.getPagina())) {
				bindingResult.rejectValue("pagina", "NotEmpty");
			}
			if (Utils.isEmpty(command.getTom())) {
				bindingResult.rejectValue("tom", "NotEmpty");
			}
			if (command.getDataRegistre() == null) {
				bindingResult.rejectValue("dataRegistre", "NotEmpty");
			}

		} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.SVDBECAWS01) {
		
			if (command.getCurs() == null) {
				bindingResult.rejectValue("curs", "NotEmpty");
			}
		}
			
		if (bindingResult.hasErrors()) {
			omplirModelFormulari(request, pareId, model);
			return "contingutPinbalForm";
		}

		Exception e = null;
		try {
			e = documentService.pinbalNovaConsulta(
					entitatActual.getId(),
					pareId,
					command.getMetaDocumentId(),
					PinbalConsultaCommand.asDto(command),
					RolHelper.getRolActual(request));
			
		} catch (Exception ex) {
			e = ex;
		}
		if (e == null) {
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../contingut/" + pareId,
					"pinbal.controller.creat.ok");
		} else {
			logger.error("Error en la de consulta PINBAL", e);
			String info = "";
			Exception pinbalExcepcion = ExceptionHelper.findExceptionInstance(e, PinbalException.class, 3);
			if (pinbalExcepcion != null) {
				String metode = ((PinbalException) pinbalExcepcion).getMetode();
				if (StringUtils.isNotEmpty(metode)) {
					info = " [" + metode + "] ";
				}
			}
			return getModalControllerReturnValueError(
					request,
					"redirect:../contingut/" + pareId,
					"pinbal.controller.creat.error",
					new String[] { info + e.getMessage() },
					e);
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

		List<PinbalServeiDocPermesEnumDto> pinbalServeiDocsPermesos = metaDocumentService.findPinbalServei(
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

	private static final Logger logger = LoggerFactory.getLogger(ContingutPinbalController.class); 
}