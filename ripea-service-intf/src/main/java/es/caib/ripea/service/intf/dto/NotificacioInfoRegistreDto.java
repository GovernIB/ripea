package es.caib.ripea.service.intf.dto;

import java.util.Date;

public class NotificacioInfoRegistreDto {
	
	private Date dataRegistre;
	private String numRegistreFormatat;
	private boolean error;
	private Date errorData;
	private String errorDescripcio;
	private byte[] justificant; 
	
	public Date getDataRegistre() {
		return dataRegistre;
	}
	public void setDataRegistre(Date dataRegistre) {
		this.dataRegistre = dataRegistre;
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
	public byte[] getJustificant() {
		return justificant;
	}
	public void setJustificant(byte[] justificant) {
		this.justificant = justificant;
	}

}
