package es.caib.ripea.core.api.dto;

public class DigitalitzacioResultatDto {

	private boolean error;
	private String errorDescripcio;
	private DigitalitzacioErrorTipusDto errorTipus;
	private byte[] contingut;
	private String nomDocument;
	private String mimeType;
	
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}
	public byte[] getContingut() {
		return contingut;
	}
	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
	}
	public DigitalitzacioErrorTipusDto getErrorTipus() {
		return errorTipus;
	}
	public void setErrorTipus(DigitalitzacioErrorTipusDto errorTipus) {
		this.errorTipus = errorTipus;
	}
	public String getNomDocument() {
		return nomDocument;
	}
	public void setNomDocument(String nomDocument) {
		this.nomDocument = nomDocument;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
		
}
