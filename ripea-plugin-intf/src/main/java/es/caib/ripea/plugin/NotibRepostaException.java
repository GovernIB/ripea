/**
 * 
 */
package es.caib.ripea.plugin;

/**
 * Indica que l'element especificat no s'ha trobat en el sistema extern.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class NotibRepostaException extends SistemaExternException {

	private String id;
	
	public NotibRepostaException(String message) {
		super(message);
	}
	
	public NotibRepostaException(Throwable cause) {
		super(cause);
	}

	public String getId() {
		return id;
	}

}
