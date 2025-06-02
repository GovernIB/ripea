package es.caib.ripea.service.intf.dto;

import java.io.Serializable;

public class PortafirmesFluxRespostaDto implements Serializable {

	private String nom;
	private String descripcio;
	private String fluxId;
	private boolean error;
	private PortafirmesFluxEstatDto estat;
	private boolean isUsuariActual;
	private boolean processada = false; //Processada per el client SSE
	private String usuari; //Usuari que ha iniciat el proces de firma
	
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getFluxId() {
		return fluxId;
	}
	public void setFluxId(String fluxId) {
		this.fluxId = fluxId;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public PortafirmesFluxEstatDto getEstat() {
		return estat;
	}
	public void setEstat(PortafirmesFluxEstatDto estat) {
		this.estat = estat;
	}

	public boolean isProcessada() {
		return processada;
	}
	public void setProcessada(boolean processada) {
		this.processada = processada;
	}
	public boolean isUsuariActual() {
		return isUsuariActual;
	}
	public void setUsuariActual(boolean isUsuariActual) {
		this.isUsuariActual = isUsuariActual;
	}

	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}

	private static final long serialVersionUID = -6768802833333049841L;
}
