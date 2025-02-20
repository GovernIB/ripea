/**
 * 
 */
package es.caib.ripea.back.base.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Objecte que conté la informació dels missatges d'error de l'API REST.
 * 
 * TODO Fer aquesta classe compatible amb Problem JSON (https://datatracker.ietf.org/doc/html/rfc7807)
 * 
 * @author Límit Tecnologies
 */
@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

	protected final int status;
	protected final String title;
	protected String stackTrace;
	protected List<ValidationError> validationErrors;

	@Getter
	@Setter
	@RequiredArgsConstructor
	public static class ValidationError {
		private final String field;
		private final Object rejectedValue;
		private final String code;
		private final String[] codes;
		private final Object[] arguments;
		private final String title;
		public String getMessage() {
			return title;
		}
	}

	public void addValidationError(
			String field,
			Object rejectedValue,
			String code,
			String[] codes,
			Object[] arguments,
			String title) {
		if (Objects.isNull(validationErrors)) {
			validationErrors = new ArrayList<>();
		}
		validationErrors.add(new ValidationError(field, rejectedValue, code, codes, arguments, title));
	}

	public String getMessage() {
		return title;
	}

}
