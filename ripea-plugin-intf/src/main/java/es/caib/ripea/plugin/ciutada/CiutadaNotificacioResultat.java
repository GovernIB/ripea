/**
 * 
 */
package es.caib.ripea.plugin.ciutada;

import java.util.Date;

/**
 * Dades resultants de fer una notificació telemàtica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CiutadaNotificacioResultat {

	private Date registreData;
	private String registreNumero;



	public Date getRegistreData() {
		return registreData;
	}
	public void setRegistreData(Date registreData) {
		this.registreData = registreData;
	}
	public String getRegistreNumero() {
		return registreNumero;
	}
	public void setRegistreNumero(String registreNumero) {
		this.registreNumero = registreNumero;
	}

}
