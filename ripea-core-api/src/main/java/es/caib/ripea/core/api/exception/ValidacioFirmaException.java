/**
 * 
 */
package es.caib.ripea.core.api.exception;

/**
 * Excepció que es llança quan falla la validacio de firma d'un document
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidacioFirmaException extends RuntimeException {

	public ValidacioFirmaException (String message) {
		super(message);
	}

	private static final long serialVersionUID = -254549990317846157L;

}
