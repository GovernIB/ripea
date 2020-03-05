/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

/**
 * Informació d'un MetaDocument.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TipusDocumentalDto implements Serializable {
	
	private Long id;
	private String codi;
	private String nom;
	private EntitatDto entitat;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public EntitatDto getEntitat() {
		return entitat;
	}
	public void setEntitat(EntitatDto entitat) {
		this.entitat = entitat;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
