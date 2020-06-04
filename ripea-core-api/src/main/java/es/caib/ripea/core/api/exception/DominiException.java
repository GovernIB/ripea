/**
 * 
 */
package es.caib.ripea.core.api.exception;

/**
 * Excepció que es produeix al accedir a un sistema extern.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DominiException extends RuntimeException {

	public DominiException(
			String message) {
		super(message);
	}

	public DominiException(
			String message,
			Throwable cause) {
		super(message, cause);
	}

}
