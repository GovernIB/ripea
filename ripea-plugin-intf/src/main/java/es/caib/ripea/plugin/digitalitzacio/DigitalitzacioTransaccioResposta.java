package es.caib.ripea.plugin.digitalitzacio;

public class DigitalitzacioTransaccioResposta {
	
	private String idTransaccio;
	private String urlRedireccio;
	private boolean returnScannedFile;
	private boolean returnSignedFile;
	
	public String getIdTransaccio() {
		return idTransaccio;
	}
	public void setIdTransaccio(String idTransaccio) {
		this.idTransaccio = idTransaccio;
	}
	public String getUrlRedireccio() {
		return urlRedireccio;
	}
	public void setUrlRedireccio(String urlRedireccio) {
		this.urlRedireccio = urlRedireccio;
	}
	public boolean isReturnScannedFile() {
		return returnScannedFile;
	}
	public void setReturnScannedFile(boolean returnScannedFile) {
		this.returnScannedFile = returnScannedFile;
	}
	public boolean isReturnSignedFile() {
		return returnSignedFile;
	}
	public void setReturnSignedFile(boolean returnSignedFile) {
		this.returnSignedFile = returnSignedFile;
	}
	
}
