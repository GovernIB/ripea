package es.caib.ripea.plugin.portafirmes;

import java.io.Serializable;

public class PortafirmesFluxInfo implements Serializable {

	private String fluxId;
	private String nom;
	private String descripcio;
	
	public String getFluxId() {
		return fluxId;
	}
	public void setFluxId(String fluxId) {
		this.fluxId = fluxId;
	}
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
	
	private static final long serialVersionUID = -1665824823934702923L;

}
