package es.caib.ripea.plugin.digitalitzacio;

public class DigitalitzacioResultat {

	private boolean error;
	private String errorDescripcio;
	private DigitalitzacioEstat estat;
	private byte[] contingut;
	private String nomDocument;
	private String mimeType;
	private String eniTipoFirma;
	
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
	public DigitalitzacioEstat getEstat() {
		return estat;
	}
	public void setEstat(DigitalitzacioEstat estat) {
		this.estat = estat;
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
	public String getEniTipoFirma() {
		return eniTipoFirma;
	}
	public void setEniTipoFirma(String eniTipoFirma) {
		this.eniTipoFirma = eniTipoFirma;
	}
	
	
}
