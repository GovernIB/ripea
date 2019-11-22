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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.audit.RipeaAuditable;

/**
 * Classe del model de dades que representa una tasca d'un meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(
		name = "ipa_metaexp_domini",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"codi", "meta_expedient_id"})
		})
@EntityListeners(AuditingEntityListener.class)
public class MetaExpedientDominiEntity extends RipeaAuditable<Long> {

	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	@Column(name = "descripcio", length = 1024)
	private String descripcio;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "meta_expedient_id")
	@ForeignKey(name = "ipa_metaexp_metaexpdom_fk")
	private MetaExpedientEntity metaExpedient;
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "ipa_entitat_metexp_metaedom_fk")
	protected EntitatEntity entitat;
	
	public String getCodi() {
		return codi;
	}
	public String getNom() {
		return nom;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public EntitatEntity getEntitat() {
		return entitat;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public MetaExpedientEntity getMetaExpedient() {
		return metaExpedient;
	}

	public void update(
			String codi,
			String nom,
			String descripcio) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
	}
	
	public static Builder getBuilder(
			String codi,
			String nom,
			String descripcio,
			EntitatEntity entitat,
			MetaExpedientEntity metaExpedient) {
		return new Builder(
				codi,
				nom,
				descripcio,
				entitat,
				metaExpedient);
	}
	public static class Builder {
		MetaExpedientDominiEntity built;
		Builder(
				String codi,
				String nom,
				String descripcio,
				EntitatEntity entitat,
				MetaExpedientEntity metaExpedient) {
			built = new MetaExpedientDominiEntity();
			built.codi = codi;
			built.nom = nom;
			built.descripcio = descripcio;
			built.entitat = entitat;
			built.metaExpedient = metaExpedient;
		}
		public MetaExpedientDominiEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result + ((metaExpedient == null) ? 0 : metaExpedient.hashCode());
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
		MetaExpedientDominiEntity other = (MetaExpedientDominiEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		if (metaExpedient == null) {
			if (other.metaExpedient != null)
				return false;
		} else if (!metaExpedient.equals(other.metaExpedient))
			return false;
		return true;
	}
	
	private static final long serialVersionUID = -5248524362190811653L;
}
