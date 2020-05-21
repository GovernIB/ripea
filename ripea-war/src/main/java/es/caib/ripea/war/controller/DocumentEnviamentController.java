/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validator;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentPublicacioTipusEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.NotificacioInfoRegistreDto;
import es.caib.ripea.core.api.dto.ServeiTipusEnumDto;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DocumentEnviamentService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.plugin.NotibRepostaException;
import es.caib.ripea.war.command.DocumentNotificacioCommand;
import es.caib.ripea.war.command.DocumentNotificacioCommand.Electronica;
import es.caib.ripea.war.command.DocumentPublicacioCommand;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.ValidationHelper;

/**
 * Controlador per als enviaments dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/document")
public class DocumentEnviamentController extends BaseUserController {

	private static final String SESSION_ATTRIBUTE_ENTREGA_POSTAL = "ContingutDocumentController.session.entregaPostal";
	
	@Autowired
	private DocumentEnviamentService documentEnviamentService;
	@Autowired
	private ExpedientInteressatService expedientInteressatService;
	@Autowired
	private ContingutService contingutService;
	
	@Autowired
	private DocumentService documentService;

	@Autowired
	private Validator validator;

	@RequestMapping(value = "/{documentId}/notificar", method = RequestMethod.GET)
	public String notificarGet(
			HttpServletRequest request,
			@PathVariable Long documentId,
			Model model) {
		emplenarModelNotificacio(
				request,
				documentId,
				model);
		DocumentNotificacioCommand command = new DocumentNotificacioCommand();
		command.setDocumentId(documentId);
		Object entrega_postal_sessio = RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ENTREGA_POSTAL);
		
		if (entrega_postal_sessio != null) {
			boolean entregaPostalHablitada = (boolean)entrega_postal_sessio;
			model.addAttribute("entregaPostal", entregaPostalHablitada);
		} else {
			model.addAttribute("entregaPostal", true);
		}
		model.addAttribute(
				"serveiTipusEstats",
				EnumHelper.getOptionsForEnum(
						ServeiTipusEnumDto.class,
						"notificacio.servei.tipus.enum."));
		model.addAttribute(command);
		return "notificacioForm";
	}

	@RequestMapping(value = "/{documentId}/notificar", method = RequestMethod.POST)
	public String notificarPost(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@Validated({DocumentNotificacioCommand.Create.class}) DocumentNotificacioCommand command,
			BindingResult bindingResult,
			Model model) {
		if (!DocumentNotificacioTipusEnumDto.MANUAL.equals(command.getTipus())) {
			new ValidationHelper(validator).isValid(
					command,
					bindingResult,
					Electronica.class);
		}
		if (bindingResult.hasErrors()) {
			emplenarModelNotificacio(
					request,
					documentId,
					model);
			return "notificacioForm";
		}
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		try {
			documentEnviamentService.notificacioCreate(
					entitatActual.getId(),
					documentId,
					DocumentNotificacioCommand.asDto(command));
			MissatgesHelper.success(
					request, 
					getMessage(
							request,
							"document.controller.notificacio.ok"));
			return "redirect:/passarelaModalTancar";
//			return this.getModalControllerReturnValueSuccess(
//					request,
//					"redirect:../../../contingut/" + documentId,
//					"document.controller.notificacio.ok");

		} catch (Exception ex) {
			logger.error(ExceptionUtils.getRootCauseMessage(ex), ex);
			String msg = "";
			Throwable rootCause = ExceptionUtils.getRootCause(ex);
			if (rootCause instanceof NotibRepostaException) {
				msg = getMessage(request, "contingut.enviament.errorReposta.notib") + " " + rootCause.getMessage();
			} else {
				msg = rootCause.getMessage();
			}
			MissatgesHelper.error(
					request,  
					msg);
			return "redirect:/passarelaModalTancar";
//			return getModalControllerReturnValueErrorMessageText(
//					request,
//					"redirect:../../../contingut/" + documentId,
//					msg);
		}
	}

	@RequestMapping(value = "/{documentId}/notificacio/{notificacioId}/info")
	public String notificacioInfo(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@PathVariable Long notificacioId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"notificacio",
				documentEnviamentService.notificacioFindAmbId(
						entitatActual.getId(),
						documentId,
						notificacioId));
		return "notificacioInfo";
	}

	@RequestMapping(value = "/{documentId}/notificacio/{notificacioId}/{enviamentId}/descarregarJustificant", method = RequestMethod.GET)
	public String notificacioConsultarIDescarregarJustificant(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long documentId,
			@PathVariable Long notificacioId,
			@PathVariable Long enviamentId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		NotificacioInfoRegistreDto registreInfo = documentService.notificacioConsultarIDescarregarJustificant(
				entitatActual.getId(),
				documentId,
				enviamentId);
		
		if (registreInfo.getJustificant() != null) {
			writeFileToResponse(
					registreInfo.getNumRegistreFormatat() != null ? "justificant" + registreInfo.getNumRegistreFormatat() + ".pdf" : "justificant.pdf",
					registreInfo.getJustificant(),
					response);
		} else {
			return this.getModalControllerReturnValueError(
					request,
					"redirect:../" + documentId +"/notificacio/" + notificacioId + "/info",
					"expedient.controller.notificacio.justificant.ko");
		}
		return null;
	}


	@RequestMapping(value = "/{documentId}/notificacio/{notificacioId}", method = RequestMethod.GET)
	public String notificacioUpdateGet(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@PathVariable Long notificacioId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		emplenarModelNotificacio(
				request,
				documentId,
				model);
		DocumentNotificacioCommand command = DocumentNotificacioCommand.asCommand(
				documentEnviamentService.notificacioFindAmbId(
						entitatActual.getId(),
						documentId,
						notificacioId));
		model.addAttribute(command);
		return "notificacioForm";
	}
	@RequestMapping(value = "/{documentId}/notificacio/{notificacioId}", method = RequestMethod.POST)
	public String notificacioUpdatePost(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@PathVariable Long notificacioId,
			@Validated({DocumentNotificacioCommand.Update.class}) DocumentNotificacioCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			emplenarModelNotificacio(
					request,
					documentId,
					model);
			return "notificacioForm";
		}
		documentEnviamentService.notificacioUpdate(
				entitatActual.getId(),
				documentId,
				DocumentNotificacioCommand.asDto(command));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../contingut/" + documentId,
				"expedient.controller.notificacio.modificada.ok");
	}

	@RequestMapping(value = "/{documentId}/notificacio/{notificacioId}/delete", method = RequestMethod.GET)
	public String notificacioDelete(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@PathVariable Long notificacioId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		documentEnviamentService.notificacioDelete(
				entitatActual.getId(),
				documentId,
				notificacioId);
		return this.getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../../contingut/" + documentId,
				"expedient.controller.notificacio.esborrada.ok");
	}
	
	
	
	
	@RequestMapping(value = "/{pareId}/document/{documentId}/descarregar", method = RequestMethod.GET)
	public String descarregar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long pareId,
			@PathVariable Long documentId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				documentId,
				true,
				false);
		if (contingut instanceof DocumentDto) {
			FitxerDto fitxer = documentService.descarregar(
					entitatActual.getId(),
					documentId,
					null);
			writeFileToResponse(
					fitxer.getNom(),
					fitxer.getContingut(),
					response);
			return null;
		}
		MissatgesHelper.error(
				request, 
				getMessage(
						request, 
						"document.controller.descarregar.error"));
		if (contingut.getPare() != null)
			return "redirect:../../contingut/" + pareId;
		else
			return "redirect:../../expedient";
	}
	
	
	@RequestMapping(value = "/{enviamentId}/descarregarCertificacio", method = RequestMethod.GET)
	public String notificacioConsultarIDescarregarCertificacio(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long enviamentId) throws IOException {

		writeFileToResponse(
				"justificant.pdf",
				documentService.notificacioConsultarIDescarregarCertificacio(enviamentId),
				response);
		return null;
	}
	
	
	

	/*@RequestMapping(value = "/{documentId}/notificacio/{notificacioId}/refrescar")
	public String notificacioRefrescar(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@PathVariable Long notificacioId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		boolean fetaAmbExit = documentEnviamentService.notificacioRetry(
				entitatActual.getId(),
				documentId,
				notificacioId);
		if (fetaAmbExit) {
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.controller.notificacio.reintent.ok"));
		} else {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.controller.notificacio.reintent.error"));
		}
		return "redirect:info";
	}*/

	@RequestMapping(value = "/{documentId}/publicar", method = RequestMethod.GET)
	public String publicarGet(
			HttpServletRequest request,
			@PathVariable Long documentId,
			Model model) {
		emplenarModelPublicacio(
				request,
				documentId,
				model);
		DocumentPublicacioCommand command = new DocumentPublicacioCommand();
		command.setDocumentId(documentId);
		model.addAttribute(command);
		return "publicacioForm";
	}

	@RequestMapping(value = "/{documentId}/publicar", method = RequestMethod.POST)
	public String publicarPost(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@Validated({DocumentPublicacioCommand.Create.class}) DocumentPublicacioCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			emplenarModelPublicacio(
					request,
					documentId,
					model);
			return "publicacioForm";
		}
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		documentEnviamentService.publicacioCreate(
				entitatActual.getId(),
				documentId,
				DocumentPublicacioCommand.asDto(command));
		return this.getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../contingut/" + documentId,
				"document.controller.publicacio.ok");
	}

	@RequestMapping(value = "/{documentId}/publicacio/{publicacioId}/info")
	public String publicacioInfo(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@PathVariable Long publicacioId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"publicacio",
				documentEnviamentService.publicacioFindAmbId(
						entitatActual.getId(),
						documentId,
						publicacioId));
		return "publicacioInfo";
	}

	@RequestMapping(value = "/{documentId}/publicacio/{publicacioId}", method = RequestMethod.GET)
	public String publicacioUpdateGet(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@PathVariable Long publicacioId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		emplenarModelPublicacio(
				request,
				documentId,
				model);
		DocumentPublicacioCommand command = DocumentPublicacioCommand.asCommand(
				documentEnviamentService.publicacioFindAmbId(
						entitatActual.getId(),
						documentId,
						publicacioId));
		model.addAttribute(command);
		return "publicacioForm";
	}
	@RequestMapping(value = "/{documentId}/publicacio/{publicacioId}", method = RequestMethod.POST)
	public String publicacioUpdatePost(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@PathVariable Long publicacioId,
			@Validated({DocumentPublicacioCommand.Update.class}) DocumentPublicacioCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			emplenarModelPublicacio(
					request,
					documentId,
					model);
			return "publicacioForm";
		}
		documentEnviamentService.publicacioUpdate(
				entitatActual.getId(),
				documentId,
				DocumentPublicacioCommand.asDto(command));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../contingut/" + documentId,
				"expedient.controller.publicacio.modificada.ok");
	}

	@RequestMapping(value = "/{documentId}/publicacio/{publicacioId}/delete", method = RequestMethod.GET)
	public String publicacioDelete(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@PathVariable Long publicacioId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		documentEnviamentService.publicacioDelete(
				entitatActual.getId(),
				documentId,
				publicacioId);
		return this.getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../../contingut/" + documentId,
				"expedient.controller.publicacio.esborrada.ok");
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}

	@RequestMapping(value = "/{documentId}/estat", method = RequestMethod.GET)
	@ResponseBody
	public String getEstatDarreraNotificació(
			HttpServletRequest request,
			@PathVariable Long documentId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentEnviamentDto lastEnviament = null;
		List<DocumentEnviamentDto> enviaments = documentEnviamentService.findAmbDocument(
				entitatActual.getId(), 
				documentId);
		if (enviaments != null && !enviaments.isEmpty()) {
			//Order by date
			Collections.sort(enviaments);
			lastEnviament = enviaments.get(enviaments.size() - 1);
			return lastEnviament.getEstat().name();
		}
		return null;
	}

	private ExpedientDto emplenarModelNotificacio(
			HttpServletRequest request,
			Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentDto document = (DocumentDto)contingutService.findAmbIdUser(
				entitatActual.getId(),
				documentId,
				false,
				false);
		model.addAttribute(
				"document",
				document);
		model.addAttribute(
				"notificacioTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						DocumentNotificacioTipusEnumDto.class,
						"notificacio.tipus.enum.",
						new Enum<?>[] {DocumentNotificacioTipusEnumDto.MANUAL}));
		model.addAttribute(
				"notificacioEstatEnumOptions",
				EnumHelper.getOptionsForEnum(
						DocumentEnviamentEstatEnumDto.class,
						"notificacio.estat.enum.",
						new Enum<?>[] {DocumentEnviamentEstatEnumDto.PROCESSAT}));
		model.addAttribute(
				"interessats",
				expedientInteressatService.findByExpedient(
						entitatActual.getId(),
						document.getExpedientPare().getId(),
						true));
		model.addAttribute(
				"expedientId",
				document.getExpedientPare().getId());
		return document.getExpedientPare();
	}

	private void emplenarModelPublicacio(
			HttpServletRequest request,
			Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"publicacioTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						DocumentPublicacioTipusEnumDto.class,
						"publicacio.tipus.enum."));
		model.addAttribute(
				"publicacioEstatEnumOptions",
				EnumHelper.getOptionsForEnum(
						DocumentEnviamentEstatEnumDto.class,
						"publicacio.estat.enum.",
						new Enum<?>[] {
							DocumentEnviamentEstatEnumDto.ENVIAT,
							DocumentEnviamentEstatEnumDto.PROCESSAT,
							DocumentEnviamentEstatEnumDto.CANCELAT}));
		
		model.addAttribute(
				"document",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						documentId,
						false,
						false));
		model.addAttribute(
				"publicacioTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						DocumentPublicacioTipusEnumDto.class,
						"publicacio.tipus.enum."));
		model.addAttribute(
				"publicacioEstatEnumOptions",
				EnumHelper.getOptionsForEnum(
						DocumentEnviamentEstatEnumDto.class,
						"publicacio.estat.enum.",
						new Enum<?>[] {
							DocumentEnviamentEstatEnumDto.ENVIAT,
							DocumentEnviamentEstatEnumDto.PROCESSAT,
							DocumentEnviamentEstatEnumDto.CANCELAT}));
		
	}
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentEnviamentController.class);

}
