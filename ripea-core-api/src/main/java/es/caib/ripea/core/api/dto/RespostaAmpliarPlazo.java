package es.caib.ripea.core.api.dto;

import java.util.List;

public class RespostaAmpliarPlazo {

	private boolean error;
	private String respostaCodi;
	private String respostaDescripcio;
	private String errorDescripcio;
	protected String documentNum;
	protected String nomInteressat;
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
	public String getDocumentNum() {
		return documentNum;
	}
	public void setDocumentNum(
			String documentNum) {
		this.documentNum = documentNum;
	}
	public String getNomInteressat() {
		return nomInteressat;
	}
	public void setNomInteressat(
			String nomInteressat) {
		this.nomInteressat = nomInteressat;
	}
	public String getRespostaDescripcio() {
		return respostaDescripcio;
	}
	public void setRespostaDescripcio(
			String respostaDescripcio) {
		this.respostaDescripcio = respostaDescripcio;
	}
}