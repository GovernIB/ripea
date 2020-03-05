/**
 * 
 */
package es.caib.ripea.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.audit.RipeaAuditable;

/**
 * Classe del model de dades que representa un meta-document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_tipus_documental")
@EntityListeners(AuditingEntityListener.class)
public class TipusDocumentalEntity extends RipeaAuditable<Long> {

	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "ipa_entitat_tipus_doc_fk")
	protected EntitatEntity entitat;
	
	public String getCodi() {
		return codi;
	}

	public String getNom() {
		return nom;
	}

	public EntitatEntity getEntitat() {
		return entitat;
	}

	public void update(
			String codi,
			String nom) {
		this.codi = codi;
		this.nom = nom;
	}

	public static Builder getBuilder(
			String codi,
			String nom,
			EntitatEntity entitat) {
		return new Builder(
				codi,
				nom,
				entitat);
	}
	public static class Builder {
		TipusDocumentalEntity built;
		Builder(
				String codi,
				String nom,
				EntitatEntity entitat) {
			built = new TipusDocumentalEntity();
			built.codi = codi;
			built.nom = nom;
			built.entitat = entitat;
			
		}
		public TipusDocumentalEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TipusDocumentalEntity other = (TipusDocumentalEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		}
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}