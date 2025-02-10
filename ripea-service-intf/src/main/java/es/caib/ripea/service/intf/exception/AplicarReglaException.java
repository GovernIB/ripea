/**
 * 
 */
package es.caib.ripea.service.intf.exception;

/**
 * Excepció que es llança des d'una tasca programada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class AplicarReglaException extends RuntimeException {

	public AplicarReglaException(
			String message) {
		super(message);
	}

}
