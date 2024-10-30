package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import lombok.ToString;

@ToString
public class IntegracioDto implements Serializable {

	private String codi;
	private String nom;
	private String endpoint;

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
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	private static final long serialVersionUID = -139254994389509932L;
}