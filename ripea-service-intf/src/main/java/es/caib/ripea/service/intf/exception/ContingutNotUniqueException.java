/**
 * 
 */
package es.caib.ripea.service.intf.exception;

/**
 * Excepció que es llança per errors validant un objecte o el seu estat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ContingutNotUniqueException extends ValidationException {

	public ContingutNotUniqueException () {
		super("No es poden crear Carpetes o Documents repetits");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1328063274223441494L;
}
