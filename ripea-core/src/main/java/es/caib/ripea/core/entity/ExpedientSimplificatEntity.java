package es.caib.ripea.core.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

/**
 * Classe del model de dades que representa una expedient simplificat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Subselect("select " + 
			" c.id as expedient_id, " + 
			" c.nom as nom, " + 
			" c.entitat_id as entitat " +
			" from ipa_contingut c " + 
			" where c.tipus = 0 " + 
			" and c.esborrat = 0")
@Immutable
public class ExpedientSimplificatEntity {

	@Id
	@Column(name = "expedient_id")
	protected Long id;

	@Column(name = "nom")
	protected String nom;
	
	@Column(name = "entitat")
	protected Long entitat;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public Long getEntitat() {
		return entitat;
	}

	public void setEntitat(Long entitat) {
		this.entitat = entitat;
	}
	
}
