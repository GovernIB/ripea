/**
 * 
 */
package es.caib.ripea.war.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.ripea.core.api.dto.*;
import es.caib.ripea.core.api.service.*;
import es.caib.ripea.war.command.DocumentNotificacionsCommand;
import es.caib.ripea.war.command.InteressatCommand;
import es.caib.ripea.war.command.NotificacioEnviamentCommand;
import es.caib.ripea.war.helper.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.support.RequestContext;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Controlador base que implementa funcionalitats comunes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseController implements MessageSourceAware {

	@Autowired private ExpedientInteressatService expedientInteressatService;
	@Autowired private ContingutService contingutService;
	@Autowired private DadesExternesService dadesExternesService;
	@Autowired private AplicacioService aplicacioService;

	public static final String SESSION_ATTRIBUTE_ROL_ACTUAL = "RolHelper.rol.actual";

	MessageSource messageSource;

	protected String modalUrlTancar() {
		//return "redirect:/nodeco/util/modalTancar";
		return "redirect:" + ModalHelper.ACCIO_MODAL_TANCAR;
	}
	protected String ajaxUrlOk() {
		//return "redirect:/nodeco/util/ajaxOk";
		return "redirect:" + AjaxHelper.ACCIO_AJAX_OK;
	}

	protected String getAjaxControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getAjaxControllerReturnValueSuccess(
				request,
				url,
				messageKey,
				null);
	}
	protected String getAjaxControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs));
		}
		if (AjaxHelper.isAjax(request)) {
			return ajaxUrlOk();
		} else {
			return url;
		}
	}
	protected String getAjaxControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey,
			Throwable ex) {
		return getAjaxControllerReturnValueError(
				request,
				url,
				messageKey,
				null,
				ex);
	}
	protected String getAjaxControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs,
			Throwable ex) {
		if (messageKey != null) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs),
					ex);
		}
		if (AjaxHelper.isAjax(request)) {
			return ajaxUrlOk();
		} else {
			return url;
		}
	}

	protected String getAjaxControllerReturnValueErrorMessage(
			HttpServletRequest request,
			String url,
			String message,
			Throwable ex) {
		if (message != null) {
			MissatgesHelper.error(
					request, 
					message,
					ex);
		}
		if (AjaxHelper.isAjax(request)) {
			return ajaxUrlOk();
		} else {
			return url;
		}
	}

	protected String getModalControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getModalControllerReturnValueSuccess(
				request,
				url,
				messageKey,
				null);
	}
	protected String getModalControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs));
		}
		if (ModalHelper.isModal(request)) {
			//String redirectionPath = redirectFromModal ? url : "";
			return modalUrlTancar();
		} else {
			return url;
		}
	}
	
	protected String getModalControllerReturnValueWarning(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getModalControllerReturnValueWarning(
				request,
				url,
				messageKey,
				null);
	}
	
	protected String getModalControllerReturnValueWarningText(
			HttpServletRequest request,
			String url,
			String text) {

			MissatgesHelper.warning(
					request, 
					text);
		
		if (ModalHelper.isModal(request)) {
			return modalUrlTancar();
		} else {
			return url;
		}
	}
	
	protected String getModalControllerReturnValueWarning(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			MissatgesHelper.warning(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs));
		}
		if (ModalHelper.isModal(request)) {
			//String redirectionPath = redirectFromModal ? url : "";
			return modalUrlTancar();
		} else {
			return url;
		}
	}

	protected String getModalControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey,
			Throwable ex) {
		return getModalControllerReturnValueError(
				request,
				url,
				messageKey,
				null,
				ex);
	}
	protected String getModalControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs,
			Throwable ex) {
		if (messageKey != null) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs),
					ex);
		}
		if (ModalHelper.isModal(request)) {
			return modalUrlTancar();
		} else {
			return url;
		}
	}

	protected String getModalControllerReturnValueErrorMessageText(
			HttpServletRequest request,
			String url,
			String message,
			Throwable ex) {
		if (message != null) {
			MissatgesHelper.error(
					request, 
					message,
					ex);
		}
		if (ModalHelper.isModal(request)) {
			return modalUrlTancar();
		} else {
			return url;
		}
	}
	
//	protected String getModalControllerReturnValueErrorMessageText(
//			HttpServletRequest request,
//			String url,
//			String message) {
//		if (message != null) {
//			MissatgesHelper.error(
//					request,
//					message);
//		}
//		if (ModalHelper.isModal(request)) {
//			return modalUrlTancar();
//		} else {
//			return url;
//		}
//	}
	

	protected void writeFileToResponse(
			String fileName,
			byte[] fileContent,
			HttpServletResponse response) throws IOException {
		response.setHeader("Pragma", "");
		response.setHeader("Expires", "");
		response.setHeader("Cache-Control", "");
		response.setHeader("Content-Disposition","attachment; filename=\"" + fileName + "\"");
		if (fileName != null && !fileName.isEmpty())
			response.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
		response.getOutputStream().write(fileContent);
	}

	protected String getMessage(
			HttpServletRequest request,
			String key,
			Object[] args) {
		String message = messageSource.getMessage(
				key,
				args,
				"???" + key + "???",
				new RequestContext(request).getLocale());
		return message;
	}

	protected String getMessage(
			HttpServletRequest request,
			String key) {
		return getMessage(request, key, null);
	}

	protected String getRolActual(
			HttpServletRequest request) {
		return RolHelper.getRolActual(request);
	}

	public ExpedientDto emplenarModelNotificacio(
			HttpServletRequest request,
			EntitatDto entitatActual,
			Long documentId,
			DocumentNotificacionsCommand command,
			Model model,
			Boolean notificacioConcatenatEntregaPostal) throws JsonProcessingException {
		DocumentDto document = (DocumentDto)contingutService.findAmbIdUser(
				entitatActual.getId(),
				documentId,
				false,
				false,
				true,
				null,
				null);
		boolean procedimentSenseCodiSia = false;
		if (document.getExpedientPare()!=null) {
			if (document.getExpedientPare().getMetaExpedient().getTipusClassificacio() == TipusClassificacioEnumDto.ID) {
				procedimentSenseCodiSia = true;
			}
			command.setExpedientPareId(document.getExpedientPare().getId());
		}
		model.addAttribute(
				"procedimentSenseCodiSia",
				procedimentSenseCodiSia);
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
				"interessatTipus",
				EnumHelper.getOptionsForEnum(
						InteressatTipusEnumDto.class,
						"interessat.tipus.enum."));

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

		boolean enviamentPostalProperty = aplicacioService.propertyBooleanFindByKey("es.caib.ripea.notificacio.enviament.postal.actiu", true);

		if (enviamentPostalProperty) {
			if (notificacioConcatenatEntregaPostal != null) {
				model.addAttribute("entregaPostal", (boolean) notificacioConcatenatEntregaPostal);
			} else {
				model.addAttribute("entregaPostal", true);
			}
		} else {
			model.addAttribute("entregaPostal", false);
		}

		model.addAttribute(
				"serveiTipusEstats",
				EnumHelper.getOptionsForEnum(
						ServeiTipusEnumDto.class,
						"notificacio.servei.tipus.enum."));
		if (command != null) {
			List<InteressatDto> interessats = expedientInteressatService.findByExpedient(
					entitatActual.getId(),
					document.getExpedientPare().getId(),
					true);
			command.getEnviaments().clear();

			for (InteressatDto interessatDto : interessats) {
				NotificacioEnviamentCommand notificacioParte = new NotificacioEnviamentCommand();

				notificacioParte.setTitular(InteressatCommand.asCommand(interessatDto));
				if (interessatDto.getRepresentant() != null) {
					notificacioParte.setDestinatari(InteressatCommand.asCommand(interessatDto.getRepresentant()));
				}
				command.getEnviaments().add(notificacioParte);
			}
			if (command.getEnviaments() != null && !command.getEnviaments().isEmpty()) {
				ObjectMapper mapper = new ObjectMapper();
				String notificacions = mapper.writeValueAsString(command.getEnviaments());
				model.addAttribute("notificacions", notificacions);
				ompleDadesAdresa(request, model);
			}
		}
		return document.getExpedientPare();
	}

	private void ompleDadesAdresa(HttpServletRequest request, Model model) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String paisos = mapper.writeValueAsString(dadesExternesService.findPaisos());
			model.addAttribute("paisos", paisos);
		} catch (Exception e) {
			MissatgesHelper.warning(request, getMessage(request, "interessat.controller.paisos.error"));
		}
		try {
			String provincies = mapper.writeValueAsString(dadesExternesService.findProvincies());
			model.addAttribute("provincies", provincies);
		} catch (Exception e) {
			MissatgesHelper.warning(request, getMessage(request, "interessat.controller.provincies.error"));
		}
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}
}
