package es.caib.ripea.core.api.dto;

import java.util.Date;


public class DocumentEnviamentInteressatDto {
	
	private Long id;
	protected InteressatDto interessat;
	
	private String enviamentReferencia;
	private String enviamentDatatEstat;
	private Date enviamentDatatData;
	private String enviamentDatatOrigen;
	private Date enviamentCertificacioData;
	private String enviamentCertificacioOrigen;
	protected Boolean error;
	protected String errorDescripcio;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public InteressatDto getInteressat() {
		return interessat;
	}
	public void setInteressat(InteressatDto interessat) {
		this.interessat = interessat;
	}
	public String getEnviamentReferencia() {
		return enviamentReferencia;
	}
	public void setEnviamentReferencia(String enviamentReferencia) {
		this.enviamentReferencia = enviamentReferencia;
	}
	public String getEnviamentDatatEstat() {
		return enviamentDatatEstat;
	}
	public void setEnviamentDatatEstat(String enviamentDatatEstat) {
		this.enviamentDatatEstat = enviamentDatatEstat;
	}
	public Date getEnviamentDatatData() {
		return enviamentDatatData;
	}
	public void setEnviamentDatatData(Date enviamentDatatData) {
		this.enviamentDatatData = enviamentDatatData;
	}
	public String getEnviamentDatatOrigen() {
		return enviamentDatatOrigen;
	}
	public void setEnviamentDatatOrigen(String enviamentDatatOrigen) {
		this.enviamentDatatOrigen = enviamentDatatOrigen;
	}
	public Date getEnviamentCertificacioData() {
		return enviamentCertificacioData;
	}
	public void setEnviamentCertificacioData(Date enviamentCertificacioData) {
		this.enviamentCertificacioData = enviamentCertificacioData;
	}
	public String getEnviamentCertificacioOrigen() {
		return enviamentCertificacioOrigen;
	}
	public void setEnviamentCertificacioOrigen(String enviamentCertificacioOrigen) {
		this.enviamentCertificacioOrigen = enviamentCertificacioOrigen;
	}
	public Boolean getError() {
		return error;
	}
	public void setError(Boolean error) {
		this.error = error;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}
	
}
