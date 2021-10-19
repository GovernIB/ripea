/**
 * 
 */
package es.caib.ripea.war.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.war.helper.AjaxHelper;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.ModalHelper;
import es.caib.ripea.war.helper.RolHelper;

/**
 * Controlador amb utilitats per a l'aplicació RIPEA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class RipeaController {

	@Autowired
	private AplicacioService aplicacioService;

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request) {
		
		if (RolHelper.isRolActualSuperusuari(request)) {
			return "redirect:entitat";
		} else {
			EntitatDto entitat = EntitatHelper.getEntitatActual(request);
			if (entitat == null)
				throw new SecurityException("No te cap entitat assignada");
			if (RolHelper.isRolActualAdministrador(request) || RolHelper.isRolActualAdministradorOrgan(request) || RolHelper.isRolActualUsuari(request)) {
				return "redirect:expedient";
			} else if (RolHelper.isRolActualRevisor(request)) {
				return "redirect:metaExpedientRevisio";
			} else {
				return "index";
			}
		}
	}

	@RequestMapping(value = ModalHelper.ACCIO_MODAL_TANCAR, method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void modalTancar() {
	}
	@RequestMapping(value = AjaxHelper.ACCIO_AJAX_OK, method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void ajaxOk() {
	}
	@RequestMapping(value = "/missatges", method = RequestMethod.GET)
	public String get() {
		return "util/missatges";
	}

	@RequestMapping(value = "/desenv/usuariActual", method = RequestMethod.GET)
	@ResponseBody
	public UsuariDto desenvUsuariActual() {
		return aplicacioService.getUsuariActual();
	}

	// PER LLEVAR
	@RequestMapping(value = "/util/modalTancar", method = RequestMethod.GET)
	public String utilModalTancar() {
		return "util/modalTancar";
	}
	@RequestMapping(value = "/util/ajaxOk", method = RequestMethod.GET)
	public String utilAjaxOk() {
		return "util/ajaxOk";
	}
	@RequestMapping(value = "/util/alertes", method = RequestMethod.GET)
	public String utilAlertes() {
		return "util/missatges";
	}
	// /PER LLEVAR

	@RequestMapping(value = "/error")
	public String error(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(
				"errorObject",
				new ErrorObject(request));
		return "util/error";
	}

	@RequestMapping(value = "/passarelaModalTancar", method = RequestMethod.GET)
	public String passarelaModalTancar() {
		return "passarelaModalTancar";
	}

	public static class ErrorObject {
		Integer statusCode;
		Throwable throwable;
		String exceptionMessage;
		String requestUri;
		String message;
		public ErrorObject(HttpServletRequest request) {
			statusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
			throwable = (Throwable)request.getAttribute("javax.servlet.error.exception");
			exceptionMessage = getExceptionMessage(throwable, statusCode);
			requestUri = (String)request.getAttribute("javax.servlet.error.request_uri");
			if (requestUri == null) 
				requestUri = "Desconeguda";
			message = 
					"Retornat codi d'error " + statusCode + " "
					+ "per al recurs " + requestUri + " "
					+ "amb el missatge: " + exceptionMessage;
		}
		public Integer getStatusCode() {
			return statusCode;
		}
		public Throwable getThrowable() {
			return throwable;
		}
		public String getThrowableClassName() {
			return throwable.getClass().getName();
		}
		public String getExceptionMessage() {
			return exceptionMessage;
		}
		public String getRequestUri() {
			return requestUri;
		}
		public String getMessage() {
			return message;
		}
		public String getStackTrace() {
			return ExceptionUtils.getStackTrace(throwable);
		}
		public String getFullStackTrace() {
			return ExceptionUtils.getFullStackTrace(throwable);
		}
		public String getRootCauseMessage() {
			return ExceptionUtils.getRootCauseMessage(throwable);
		}
		private String getExceptionMessage(Throwable throwable, Integer statusCode) {
			if (throwable != null) {
				Throwable rootCause = ExceptionUtils.getRootCause(throwable);
				if (rootCause != null)
					return rootCause.getMessage();
				else
					return throwable.getMessage();
			} else {
				HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
				return httpStatus.getReasonPhrase();
			}
		}
	}

}
