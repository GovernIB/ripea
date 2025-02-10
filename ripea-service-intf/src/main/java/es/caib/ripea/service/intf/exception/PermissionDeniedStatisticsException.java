/**
 * 
 */
package es.caib.ripea.service.intf.exception;

/**
 * Excepció que es produeix al intentar accedir a estadístiques sense permisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class PermissionDeniedStatisticsException extends RuntimeException {

	public PermissionDeniedStatisticsException(
			String message) {
		super(message);
	}

	public PermissionDeniedStatisticsException(
			String message,
			Throwable cause) {
		super(message, cause);
	}

}
