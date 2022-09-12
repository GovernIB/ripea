/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import lombok.ToString;


/**
 * Dades d'una integració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
