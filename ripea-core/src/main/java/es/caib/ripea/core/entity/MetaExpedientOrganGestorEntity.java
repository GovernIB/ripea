/**
 * 
 */
package es.caib.ripea.core.entity;

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
 * Classe del model de dades que representa una relació entre meta-expedient i organ.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_metaexp_organ")
@EntityListeners(AuditingEntityListener.class)
public class MetaExpedientOrganGestorEntity extends RipeaAuditable<Long> {

	@ManyToOne(
			optional = false,
			fetch = FetchType.EAGER)
	@JoinColumn(name = "meta_expedient_id")
	@ForeignKey(name = "ipa_metaexp_metaexporg_fk")
	private MetaExpedientEntity metaExpedient;
	@ManyToOne(
			optional = false,
			fetch = FetchType.EAGER)
	@JoinColumn(name = "organ_gestor_id")
	@ForeignKey(name = "ipa_organ_metaexporg_fk")
	private OrganGestorEntity organGestor;

	public MetaExpedientEntity getMetaExpedient() {
		return metaExpedient;
	}
	public OrganGestorEntity getOrganGestor() {
		return organGestor;
	}

	/**
	 * Obté el Builder per a crear objectes de tipus meta-expedient.
	 * 
	 * @param any
	 *            El valor de l'atribut any.
	 * @param metaExpedient
	 *            El valor de l'atribut metaExpedient.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			MetaExpedientEntity metaExpedient,
			OrganGestorEntity organGestor) {
		return new Builder(
				metaExpedient,
				organGestor);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		MetaExpedientOrganGestorEntity built;
		Builder(
				MetaExpedientEntity metaExpedient,
				OrganGestorEntity organGestor) {
			built = new MetaExpedientOrganGestorEntity();
			built.metaExpedient = metaExpedient;
			built.organGestor = organGestor;
		}
		public MetaExpedientOrganGestorEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((metaExpedient == null) ? 0 : metaExpedient.hashCode());
		result = prime * result + ((organGestor == null) ? 0 : organGestor.hashCode());
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
		MetaExpedientOrganGestorEntity other = (MetaExpedientOrganGestorEntity)obj;
		if (metaExpedient == null) {
			if (other.metaExpedient != null)
				return false;
		} else if (!metaExpedient.equals(other.metaExpedient))
			return false;
		if (organGestor == null) {
			if (other.organGestor != null)
				return false;
		} else if (!organGestor.equals(other.organGestor))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
