/**
 * 
 */
package es.caib.ripea.core.api.exception;

/**
 * Excepció que es llança per errors validant un objecte o el seu estat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientTancarSenseDocumentsDefinitiusException extends ValidationException {

	public ExpedientTancarSenseDocumentsDefinitiusException () {
		super("No es pot tancar un expedient sense cap document definitiu");
	}

}
