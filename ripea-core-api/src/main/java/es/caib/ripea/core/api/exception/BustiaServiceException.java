/**
 * 
 */
package es.caib.ripea.core.api.exception;

/**
 * Excepció que indica un problema en les cridades al servei web
 * BustiaService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class BustiaServiceException extends RuntimeException {

	public BustiaServiceException(
			String message) {
		super(message);
	}

	public BustiaServiceException(
			String message,
			Throwable cause) {
		super(message, cause);
	}

}
