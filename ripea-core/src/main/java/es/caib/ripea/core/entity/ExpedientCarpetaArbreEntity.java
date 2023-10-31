/**
 * 
 */
package es.caib.ripea.core.entity;


import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * Classe del model de dades que representa una carpeta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Subselect("select " + 
			" c.id as id, " + 
			" c.nom as nom, " + 
			" c.pare_id as pare_id, " +
			" c.expedient_id as expedient_id, " + 
			" c.entitat_id as entitat " +
			" from ipa_contingut c " + 
			" where c.tipus != 2 " + 
			" and c.esborrat = 0")
@Immutable
public class ExpedientCarpetaArbreEntity {

	@Id
	@Column(name = "id")
	protected Long id;

	@Column(name = "nom")
	protected String nom;
	
	@Column(name = "entitat")
	protected Long entitat;

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "pare_id")
	@ForeignKey(name = "ipa_pare_contingut_fk")
	protected ExpedientCarpetaArbreEntity pare;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "expedient_id")
	@ForeignKey(name = "ipa_expedient_contingut_fk")
	protected ExpedientEntity expedient;
	
	@OneToMany(
			mappedBy = "pare", 
			fetch = FetchType.LAZY, 
			cascade = CascadeType.ALL, 
			orphanRemoval = true)
	protected Set<ExpedientCarpetaArbreEntity> fills = new HashSet<ExpedientCarpetaArbreEntity>();

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

	public ExpedientCarpetaArbreEntity getPare() {
		return pare;
	}

	public void setPare(ExpedientCarpetaArbreEntity pare) {
		this.pare = pare;
	}

	public ExpedientEntity getExpedient() {
		return expedient;
	}

	public void setExpedient(ExpedientEntity expedient) {
		this.expedient = expedient;
	}

	public Set<ExpedientCarpetaArbreEntity> getFills() {
		return fills;
	}

	public void setFills(Set<ExpedientCarpetaArbreEntity> fills) {
		this.fills = fills;
	}

}