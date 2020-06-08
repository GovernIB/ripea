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
public class CipherException extends RuntimeException {

	public CipherException(
			String message) {
		super(message);
	}

	public CipherException(
			String message,
			Throwable cause) {
		super(message, cause);
	}

}
