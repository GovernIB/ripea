package es.caib.ripea.service.intf.dto;

import lombok.ToString;

import java.io.Serializable;

@ToString
public class IntegracioDto implements Serializable {

	private String codi;
	private String nom;

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

	private static final long serialVersionUID = -139254994389509932L;
}