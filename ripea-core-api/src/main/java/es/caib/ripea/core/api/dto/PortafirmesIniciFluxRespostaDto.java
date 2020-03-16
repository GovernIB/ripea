package es.caib.ripea.core.api.dto;

import java.io.Serializable;

public class PortafirmesIniciFluxRespostaDto implements Serializable {

	private String idTransaccio;
	private String urlRedireccio;
	
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


	private static final long serialVersionUID = -1279092736557310555L;

}
