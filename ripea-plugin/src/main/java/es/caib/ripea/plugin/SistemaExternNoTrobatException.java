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
public class SistemaExternNoTrobatException extends SistemaExternCodiMissatgeException {

	private String id;

	public SistemaExternNoTrobatException(
			String id,
			String codi,
			String missatge) {
		super(codi, missatge);
		this.id = id;
	}

	public SistemaExternNoTrobatException(
			String id,
			String codi,
			String missatge,
			Throwable cause) {
		super(codi, missatge, cause);
		this.id = id;
	}

	public String getId() {
		return id;
	}

}
