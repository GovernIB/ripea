/**
 * 
 */
package es.caib.ripea.war.controller;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.PinbalConsentimentEnumDto;
import es.caib.ripea.war.helper.*;

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
