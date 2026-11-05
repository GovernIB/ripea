package es.caib.ripea.back.error;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import es.caib.ripea.back.base.error.ErrorResponse;
import es.caib.ripea.service.intf.exception.AccioValidacioException;
import lombok.extern.slf4j.Slf4j;

/**
 * Tractament de les excepcions pròpies de RIPEA a l'API REST de la interfície REACT, abans que el tractament global de
 * base-boot (GlobalExceptionHandler), que no es pot modificar.
 *
 * Spring cerca el tractament també a les causes de l'excepció: l'excepció arriba embolcallada en una EJBException.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(basePackages = { "es.caib.ripea.back.base.controller", "es.caib.ripea.back.resourcecontroller" })
public class RipeaResourceExceptionHandler {

	/** Codi dels errors de validació detectats en executar l'acció. */
	private static final String CODI_ERROR_ACCIO = "AccioValidacio";

	/**
	 * Error de validació en executar una acció: es retorna com els errors de validació del formulari (HTTP 422 amb els
	 * errors per camp), de manera que el formulari REACT marca els camps i substitueix els errors d'enviaments anteriors.
	 */
	@ExceptionHandler(AccioValidacioException.class)
	public ResponseEntity<Object> handleAccioValidacioException(AccioValidacioException ex) {
		log.debug("Errors de validació en executar l'acció " + ex.getCode() + ": " + ex.getErrorsCamps());
		HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
		ErrorResponse errorResponse = new ErrorResponse(status.value(), ex.getErrorMessage());
		// Un sol error per camp: el formulari només mostra el primer error de cada camp
		ex.getErrorsCamps().forEach((camp, errors) -> errorResponse.addValidationError(
				camp,
				null,
				CODI_ERROR_ACCIO,
				new String[] { CODI_ERROR_ACCIO },
				null,
				String.join(", ", errors)));
		return ResponseEntity
				.status(status)
				.header("Content-Type", "application/problem+json; charset=utf-8")
				.body(errorResponse);
	}

}
