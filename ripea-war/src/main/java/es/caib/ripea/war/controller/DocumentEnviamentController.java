/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;

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

import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentPublicacioTipusEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ServeiTipusEnumDto;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DocumentEnviamentService;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.war.command.DocumentNotificacioCommand;
import es.caib.ripea.war.command.DocumentNotificacioCommand.Electronica;
import es.caib.ripea.war.command.DocumentPublicacioCommand;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.ValidationHelper;

/**
 * Controlador per als enviaments dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/document")
public class DocumentEnviamentController extends BaseUserController {

	@Autowired
	private DocumentEnviamentService documentEnviamentService;
	@Autowired
	private ExpedientInteressatService expedientInteressatService;
	@Autowired
	private ContingutService contingutService;

	@Autowired
	private Validator validator;



	@RequestMapping(value = "/{documentId}/notificar", method = RequestMethod.GET)
	public String notificarGet(
			HttpServletRequest request,
			@PathVariable Long documentId,
			Model model) {
		DocumentNotificacioCommand command = new DocumentNotificacioCommand();
		command.setDocumentId(documentId);
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
		documentEnviamentService.notificacioCreate(
				entitatActual.getId(),
				documentId,
				DocumentNotificacioCommand.asDto(command));
		return this.getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../contingut/" + documentId,
				"document.controller.notificacio.ok");
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
						"notificacio.tipus.enum."));
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
						document.getExpedientPare().getId()));
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

}
