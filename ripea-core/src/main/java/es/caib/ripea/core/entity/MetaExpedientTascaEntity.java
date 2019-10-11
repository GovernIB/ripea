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
		name = "ipa_metaexp_tasca",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"codi", "meta_expedient_id"})
		})
@EntityListeners(AuditingEntityListener.class)
public class MetaExpedientTascaEntity extends RipeaAuditable<Long> {

	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	@Column(name = "descripcio", length = 1024, nullable = false)
	private String descripcio;
	@Column(name = "responsable", length = 64)
	private String responsable;
	@Column(name = "activa", nullable = false)
	private boolean activa;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "meta_expedient_id")
	@ForeignKey(name = "ipa_metaexp_metaexptas_fk")
	private MetaExpedientEntity metaExpedient;

	public String getCodi() {
		return codi;
	}
	public String getNom() {
		return nom;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public String getResponsable() {
		return responsable;
	}
	public boolean isActiva() {
		return activa;
	}
	public MetaExpedientEntity getMetaExpedient() {
		return metaExpedient;
	}

	public void update(
			String codi,
			String nom,
			String descripcio,
			String responsable) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.responsable = responsable;
	}
	public void updateActiva(
			boolean activa) {
		this.activa = activa;
	}

	public static Builder getBuilder(
			String codi,
			String nom,
			String descripcio,
			String responsable,
			MetaExpedientEntity metaExpedient) {
		return new Builder(
				codi,
				nom,
				descripcio,
				responsable,
				metaExpedient);
	}
	public static class Builder {
		MetaExpedientTascaEntity built;
		Builder(
				String codi,
				String nom,
				String descripcio,
				String responsable,
				MetaExpedientEntity metaExpedient) {
			built = new MetaExpedientTascaEntity();
			built.codi = codi;
			built.nom = nom;
			built.descripcio = descripcio;
			built.responsable = responsable;
			built.metaExpedient = metaExpedient;
			built.activa = true;
		}
		public MetaExpedientTascaEntity build() {
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
		MetaExpedientTascaEntity other = (MetaExpedientTascaEntity) obj;
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

	private static final long serialVersionUID = -2299453443943600172L;

}
