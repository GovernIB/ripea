/**
 *
 */
package es.caib.ripea.core.api.dto;

import java.util.Date;

/**
 * Informació del registre d'una notificació d'un document.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class DocumentEnviamentRegistreDto {

	private String numeroRegistreFormatat;
	private Date dataRegistre;
	private byte[] justificant;
	
	public String getNumeroRegistreFormatat() {
		return numeroRegistreFormatat;
	}
	public void setNumeroRegistreFormatat(String numeroRegistreFormatat) {
		this.numeroRegistreFormatat = numeroRegistreFormatat;
	}
	public Date getDataRegistre() {
		return dataRegistre;
	}
	public void setDataRegistre(Date dataRegistre) {
		this.dataRegistre = dataRegistre;
	}
	public byte[] getJustificant() {
		return justificant;
	}
	public void setJustificant(byte[] justificant) {
		this.justificant = justificant;
	}
	
}
