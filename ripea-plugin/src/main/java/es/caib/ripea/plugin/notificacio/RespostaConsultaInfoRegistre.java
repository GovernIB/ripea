/**
 *
 */
package es.caib.ripea.plugin.notificacio;

import java.util.Date;

/**
 * Informació retornada per la consulta de l'estat d'un enviament.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaConsultaInfoRegistre {

	private Date dataRegistre;
	private int numRegistre;
	private String numRegistreFormatat;
	private byte[] justificant;
	private boolean error;
	private Date errorData;
	private String errorDescripcio;
	
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
	public int getNumRegistre() {
		return numRegistre;
	}
	public void setNumRegistre(int numRegistre) {
		this.numRegistre = numRegistre;
	}
	public String getNumRegistreFormatat() {
		return numRegistreFormatat;
	}
	public void setNumRegistreFormatat(String numRegistreFormatat) {
		this.numRegistreFormatat = numRegistreFormatat;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public Date getErrorData() {
		return errorData;
	}
	public void setErrorData(Date errorData) {
		this.errorData = errorData;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}
	
}
