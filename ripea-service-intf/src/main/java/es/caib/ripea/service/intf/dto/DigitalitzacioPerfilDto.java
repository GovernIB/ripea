package es.caib.ripea.service.intf.dto;

import java.io.Serializable;

public class DigitalitzacioPerfilDto implements Serializable {

	private String codi;
	private String nom;
	private String descripcio;
	private int tipus;
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
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
	public int getTipus() {
		return tipus;
	}
	public void setTipus(int tipus) {
		this.tipus = tipus;
	}
	
	public DigitalitzacioPerfilDto() {
	}
	public DigitalitzacioPerfilDto(
			String codi,
			String nom,
			String descripcio,
			int tipus) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.tipus = tipus;
	}




	private static final long serialVersionUID = -632750619340383222L;

}
