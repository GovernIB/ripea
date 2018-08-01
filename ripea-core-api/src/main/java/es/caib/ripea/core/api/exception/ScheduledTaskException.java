/**
 * 
 */
package es.caib.ripea.core.api.exception;

/**
 * Excepció que es llança des d'una tasca programada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ScheduledTaskException extends RuntimeException {

	public ScheduledTaskException(
			String message) {
		super(message);
	}

}
