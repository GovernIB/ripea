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
public class SistemaExternCodiMissatgeException extends SistemaExternException {

	private String codi;
	private String missatge;

	public SistemaExternCodiMissatgeException(
			String missatge) {
		super(missatge);
	}
	
	public SistemaExternCodiMissatgeException(
			String codi,
			String missatge) {
		super("[" + codi + "] " + missatge);
		this.codi = codi;
		this.missatge = missatge;
	}

	public SistemaExternCodiMissatgeException(
			String codi,
			String missatge,
			Throwable cause) {
		super("[" + codi + "] " + missatge, cause);
		this.codi = codi;
		this.missatge = missatge;
	}

	public String getCodi() {
		return codi;
	}
	public String getMissatge() {
		return missatge;
	}

}
