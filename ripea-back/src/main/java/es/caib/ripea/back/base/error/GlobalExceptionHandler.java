/**
 * 
 */
package es.caib.ripea.back.base.error;

import es.caib.ripea.back.resourcecontroller.ExpedientResourceController;
import es.caib.ripea.service.intf.base.exception.*;
import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Tractament global de les excepcions en els controladors.
 * 
 * @author Límit Tecnologies
 */
@Slf4j
@ControllerAdvice(basePackageClasses = { ExpedientResourceController.class })
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	public static final String REQUEST_PARAM_TRACE = "trace";

	@Autowired
	private MessageSource messageSource;

	@Value("${reflectoring.trace:true}")
	private boolean printStackTrace;

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<Object> handleNotFoundException(
			NotFoundException ex,
			WebRequest request) {
		log.warn(ex.getMessage());
		return buildErrorResponse(
				ex,
				ex.getMessage(),
				HttpStatus.NOT_FOUND,
				request);
	}

	@ExceptionHandler(ResourceAlreadyExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ResponseEntity<Object> handleResourceAlreadyExistsException(
			ResourceAlreadyExistsException ex,
			WebRequest request) {
		log.warn(ex.getMessage());
		return buildErrorResponse(
				ex,
				ex.getMessage(),
				HttpStatus.CONFLICT,
				request);
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ResponseEntity<Object> handleAccessDeniedException(
			AccessDeniedException ex,
			WebRequest request) {
		return buildErrorResponse(
				ex,
				ex.getMessage(),
				HttpStatus.FORBIDDEN,
				request);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleDataIntegrityViolationException(
			DataIntegrityViolationException ex,
			WebRequest request) {
		if (ex.getCause() != null && ex.getCause().getClass().getSimpleName().equals("ConstraintViolationException")) {
			String constraintName = null;
			try {
				Field constraintNameField = ex.getCause().getClass().getDeclaredField("constraintName");
				constraintNameField.setAccessible(true);
				constraintName = (String)constraintNameField.get(ex.getCause());
			} catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
			}
			String errorMessage;
			if (constraintName != null) {
				if (constraintName.contains(".")) {
					constraintName = constraintName.split("\\.")[1];
				}
				String constraintNameUpper = constraintName.toUpperCase();
				try {
					String errorKey = BaseConfig.BASE_PACKAGE + ".error.handling.DataIntegrityViolationException." + constraintNameUpper + ".message";
					errorMessage = messageSource.getMessage(
							errorKey,
							null,
							request.getLocale());
				} catch (NoSuchMessageException nmex) {
					String errorKey = BaseConfig.BASE_PACKAGE + ".error.handling.ConstraintViolationException.message";
					if (constraintNameUpper.endsWith("_FK")) {
						errorKey = BaseConfig.BASE_PACKAGE + ".error.handling.ForeigKeyViolationException.message";
					} else if (constraintNameUpper.endsWith("_UK")) {
						errorKey = BaseConfig.BASE_PACKAGE + ".error.handling.UniqueKeyViolationException.message";
					} else if (constraintNameUpper.endsWith("_PK")) {
						errorKey = BaseConfig.BASE_PACKAGE + ".error.handling.PrimaryKeyViolationException.message";
					}
					errorMessage = messageSource.getMessage(
							errorKey,
							new String[] { constraintName },
							request.getLocale());
				}
			} else {
				errorMessage = ex.getMessage();
			}
			return buildConstraintValidationErrorResponse(
					ex,
					errorMessage,
					HttpStatus.INTERNAL_SERVER_ERROR,
					request,
					constraintName);
		} else {
			return buildErrorResponse(
					ex,
					ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR,
					request);
		}
	}

	@ExceptionHandler(ResourceNotCreatedException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleResourceNotCreatedException(
			ResourceNotCreatedException ex,
			WebRequest request) {
		return buildModificationCanceledErrorResponse(
				ex,
				ex.getReason(),
				HttpStatus.INTERNAL_SERVER_ERROR,
				request,
				null);
	}

	@ExceptionHandler(ResourceNotUpdatedException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleResourceNotUpdatedException(
			ResourceNotUpdatedException ex,
			WebRequest request) {
		return buildModificationCanceledErrorResponse(
				ex,
				ex.getReason(),
				HttpStatus.INTERNAL_SERVER_ERROR,
				request,
				null);
	}

	@ExceptionHandler(ResourceNotDeletedException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleResourceNotDeletedException(
			ResourceNotDeletedException ex,
			WebRequest request) {
		return buildModificationCanceledErrorResponse(
				ex,
				ex.getReason(),
				HttpStatus.INTERNAL_SERVER_ERROR,
				request,
				null);
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleAllUncaughtException(
			RuntimeException ex,
			WebRequest request) {
		log.error("Uncaught exception", ex);
		return buildErrorResponse(
				ex,
				ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "Unknown error",
				HttpStatus.INTERNAL_SERVER_ERROR,
				request);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {
		HttpStatus responseStatus = HttpStatus.UNPROCESSABLE_ENTITY;
		ErrorResponse errorResponse = new ErrorResponse(
				responseStatus.value(),
				messageSource.getMessage(
						BaseConfig.BASE_PACKAGE + ".error.handling.MethodArgumentNotValidException.message",
						new String[] { "validationErrors" },
						request.getLocale())); // "Validation error. Check 'validationErrors' field for details."
		for (ObjectError objectError: ex.getBindingResult().getGlobalErrors()) {
			errorResponse.addValidationError(
					null,
					null,
					objectError.getCode(),
					objectError.getCodes(),
					objectError.getArguments(),
					objectError.getDefaultMessage());
		}
		for (FieldError fieldError: ex.getBindingResult().getFieldErrors()) {
			errorResponse.addValidationError(
					fieldError.getField(),
					fieldError.getRejectedValue(),
					fieldError.getCode(),
					fieldError.getCodes(),
					fieldError.getArguments(),
					fieldError.getDefaultMessage());
		}
		return toErrorResponseEntity(responseStatus, errorResponse);
	}

	@Override
	public ResponseEntity<Object> handleExceptionInternal(
			Exception ex,
			Object body,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {
		log.error("Internal exception", ex);
		return buildErrorResponse(ex, status, request);
	}

	private ResponseEntity<Object> buildErrorResponse(
			Exception ex,
			HttpStatus httpStatus,
			WebRequest request) {
		return buildErrorResponse(ex, ex.getMessage(), httpStatus, request);
	}

	private ResponseEntity<Object> buildErrorResponse(
			Exception ex,
			String message,
			HttpStatus httpStatus,
			WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
		if (printStackTrace && isTraceOn(request)) {
			errorResponse.setStackTrace(ExceptionUtils.getStackTrace(ex));
		}
		return toErrorResponseEntity(httpStatus, errorResponse);
	}

	private ResponseEntity<Object> buildExternalSystemErrorResponse(
			Exception ex,
			String message,
			HttpStatus httpStatus,
			Throwable traceException) {
		ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
		if (printStackTrace && traceException != null) {
			errorResponse.setStackTrace(ExceptionUtils.getStackTrace(traceException));
		}
		return toErrorResponseEntity(httpStatus, errorResponse);
	}

	private ResponseEntity<Object> buildModificationCanceledErrorResponse(
			Exception ex,
			String message,
			HttpStatus httpStatus,
			WebRequest request,
			String action) {
		ErrorResponse errorResponse = new ModificationCanceledErrorResponse(
				httpStatus.value(),
				message,
				action);
		if (printStackTrace && isTraceOn(request)) {
			errorResponse.setStackTrace(ExceptionUtils.getStackTrace(ex));
		}
		return toErrorResponseEntity(httpStatus, errorResponse);
	}

	private ResponseEntity<Object> buildConstraintValidationErrorResponse(
			Exception ex,
			String message,
			HttpStatus httpStatus,
			WebRequest request,
			String constraintName) {
		ErrorResponse errorResponse;
		if (constraintName != null) {
			errorResponse = new ConstraintValidationErrorResponse(
					httpStatus.value(),
					message,
					constraintName);
		} else {
			errorResponse = new ErrorResponse(httpStatus.value(), message);
		}
		if (printStackTrace && isTraceOn(request)) {
			errorResponse.setStackTrace(ExceptionUtils.getStackTrace(ex));
		}
		return toErrorResponseEntity(httpStatus, errorResponse);
	}

	private boolean isTraceOn(WebRequest request) {
		String[] value = request.getParameterValues(REQUEST_PARAM_TRACE);
		return Objects.nonNull(value) && value.length > 0 && (value[0].isEmpty() || value[0].contentEquals("true"));
	}

	private ResponseEntity<Object> toErrorResponseEntity(
			HttpStatus httpStatus,
			ErrorResponse errorResponse) {
		return ResponseEntity.
				status(httpStatus).
				header("Content-Type", "application/problem+json; charset=utf-8").
				body(errorResponse);
	}

}
