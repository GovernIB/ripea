package es.caib.ripea.plugin.portafirmes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PortafirmesFluxInfo implements Serializable {

	private String nom;
	private String descripcio;
	
	private List<PortafirmesFluxSigner> signers = new ArrayList<PortafirmesFluxSigner>();
	
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
	
	public List<PortafirmesFluxSigner> getSigners() {
		return signers;
	}
	public void setSigners(List<PortafirmesFluxSigner> signers) {
		this.signers = signers;
	}

	private static final long serialVersionUID = -1665824823934702923L;

}
