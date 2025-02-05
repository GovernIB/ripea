package es.caib.ripea.plugin.notificacio;

import java.util.List;

public class RespostaAmpliarPlazo {

	private boolean error;
	private String respostaCodi;
	private String errorDescripcio;
	private List<AmpliacioPlazo> ampliacionsPlazo;
	
	public String getRespostaCodi() {
		return respostaCodi;
	}
	public void setRespostaCodi(
			String respostaCodi) {
		this.respostaCodi = respostaCodi;
	}
	public boolean isError() {
		return error;
	}
	public void setError(
			boolean error) {
		this.error = error;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(
			String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}
	public List<AmpliacioPlazo> getAmpliacionsPlazo() {
		return ampliacionsPlazo;
	}
	public void setAmpliacionsPlazo(
			List<AmpliacioPlazo> ampliacionsPlazo) {
		this.ampliacionsPlazo = ampliacionsPlazo;
	}
}