/**
 * 
 */
package es.caib.ripea.service.intf.exception;

/**
 * Excepció retornada en cas de problemes al fer consultes a PINBAL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class PinbalException extends RuntimeException {

	private String metode;
	
	public PinbalException(Throwable cause) {
		super(cause);
	}
	
	public PinbalException(Throwable cause, String metode) {
		super(cause);
		this.metode = metode;
	}

	public PinbalException(String message) {
		super(message);
	}

	public PinbalException(
			String message,
			Throwable cause) {
		super(message, cause);
	}
	
	public PinbalException(
			String message,
			Throwable cause, 
			String metode) {
		super(message, cause);
		this.metode = metode;
	}

	public String getMetode() {
		return metode;
	}


}
