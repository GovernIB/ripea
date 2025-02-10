package es.caib.ripea.service.intf.dto;

import java.io.Serializable;
import java.util.List;

public class PortafirmesFluxInfoDto implements Serializable {

	private String nom;
	private String descripcio;

	private List<PortafirmesFluxSignerDto> destinataris;
	
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
	public List<PortafirmesFluxSignerDto> getDestinataris() {
		return destinataris;
	}
	public void setDestinataris(List<PortafirmesFluxSignerDto> destinataris) {
		this.destinataris = destinataris;
	}

	private static final long serialVersionUID = 3290339330233626534L;
}
