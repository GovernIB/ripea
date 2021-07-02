/**
 * 
 */
package es.caib.ripea.core.api.exception;

/**
 * Excepció retornada en cas de problemes al fer consultes a PINBAL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class PinbalException extends RuntimeException {

	public PinbalException(Throwable cause) {
		super(cause);
	}

	public PinbalException(String message) {
		super(message);
	}

	public PinbalException(
			String message,
			Throwable cause) {
		super(message, cause);
	}

}
