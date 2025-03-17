/**
 * 
 */
package es.caib.ripea.back.error;

import es.caib.ripea.back.controller.ExpedientController;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.SistemaExternException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.ejb.EJBAccessException;
import javax.ejb.EJBException;
import javax.servlet.http.HttpServletRequest;

/**
 * Tractament global de les excepcions en els controladors.
 * 
 * @author Límit Tecnologies
 */
@Slf4j
@ControllerAdvice(basePackages = {"es.caib.ripea.back.controller"})
public class RipeaJspExceptionHandler {

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ModelAndView handleNotFoundException(
			NotFoundException ex) {
		ModelAndView model = new ModelAndView("util/error");
		ErrorObject errorObject = new ErrorObject(
				HttpStatus.NOT_FOUND.value(),
				ex.getMessage());
		errorObject.setNotFound(true);
		errorObject.setThrowable(ex);
		model.addObject("errorObject", errorObject);
		return model;
	}

	@ExceptionHandler(SistemaExternException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleSistemaExternException(
			SistemaExternException ex) {
		ModelAndView model = new ModelAndView("util/error");
		ErrorObject errorObject = new ErrorObject(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				ex.getMessage());
		errorObject.setSistemaExtern(true);
		errorObject.setThrowable(ex);
		errorObject.setStackTrace(ExceptionUtils.getStackTrace(ex));
		model.addObject("errorObject", errorObject);
		return model;
	}

	/*@ExceptionHandler(MultipartException.class)
	public final String handleMultipartException(
			MultipartException ex,
			HttpServletRequest request) {
		log.error("Multipart Excepcion", ex);
		if (ex.getCause().getMessage().contains("No queda espacio en el dispositivo")) {
			return getModalControllerReturnValueError(request, null, "error.multipart.noQuedaEspacioEnElDispositivo", null, ex.getCause());
		} else {
			return getModalControllerReturnValueErrorMessageText(request, null, ex.getCause().getCause().getMessage(), ex.getCause().getCause());
		}
	}*/

	@ExceptionHandler(value = { AccessDeniedException.class, EJBAccessException.class })
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ModelAndView handleAccessDeniedException(
			Exception ex) {
		ModelAndView model = new ModelAndView("util/error");
		ErrorObject errorObject = new ErrorObject(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				ex.getMessage());
		errorObject.setAccessDenied(true);
		ErrorObject.AccessDeniedSource accessDeniedSource = ErrorObject.AccessDeniedSource.SPRING;
		if (ex instanceof EJBAccessException) {
			accessDeniedSource = ErrorObject.AccessDeniedSource.EJB;
		}
		errorObject.setAccessDeniedSource(accessDeniedSource != null ? accessDeniedSource : ErrorObject.AccessDeniedSource.SPRING);
		errorObject.setThrowable(ex);
		errorObject.setStackTrace(ExceptionUtils.getStackTrace(ex));
		model.addObject("errorObject", errorObject);
		return model;
	}

	@ExceptionHandler(EJBException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleEJBException(
			EJBException ex,
			HttpServletRequest request) {
		Throwable cause = ex.getCause();
		if (cause instanceof NotFoundException) {
			return handleNotFoundException((NotFoundException)cause);
		} else if (cause instanceof SistemaExternException) {
			return handleSistemaExternException((SistemaExternException)cause);
		} else {
			return handleAllUncaughtException(cause, request);
		}
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleAllUncaughtException(
			Throwable ex,
			HttpServletRequest request) {
		log.error("Error al processar la petició HTTP al recurs " + request.getRequestURI(), ex);
		ModelAndView model = new ModelAndView("util/error");
		ErrorObject errorObject = new ErrorObject(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				ex.getMessage());
		errorObject.setThrowable(ex);
		errorObject.setStackTrace(ExceptionUtils.getStackTrace(ex));
		errorObject.setRequestUri(request.getRequestURI());
		errorObject.setExceptionMessage(ex.getMessage());
		model.addObject("errorObject", errorObject);
		return model;
	}
}