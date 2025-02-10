/**
 * 
 */
package es.caib.ripea.core.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * Classe del model de dades que representa un meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = BaseConfig.DB_PREFIX + "metaexp_seq",
uniqueConstraints = {
		@UniqueConstraint(columnNames = {
				"anio",
				"meta_expedient_id"})})
@EntityListeners(AuditingEntityListener.class)
public class MetaExpedientSequenciaEntity extends RipeaAuditable<Long> {

	@Column(name = "anio")
	private int any;
	@Column(name = "valor")
	private long valor;
	@ManyToOne(
			optional = true,
			fetch = FetchType.EAGER)
	@JoinColumn(name = "meta_expedient_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "metaexp_metaexpseq_fk")
	private MetaExpedientEntity metaExpedient;



	public int getAny() {
		return any;
	}
	public long getValor() {
		return valor;
	}
	public MetaExpedientEntity getMetaExpedient() {
		return metaExpedient;
	}

	public void incrementar() {
		this.valor++;
	}
	public void updateValor(long valor) {
		this.valor = valor;
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
			int any,
			MetaExpedientEntity metaExpedient) {
		return new Builder(
				any,
				metaExpedient);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		MetaExpedientSequenciaEntity built;
		Builder(
				int any,
				MetaExpedientEntity metaExpedient) {
			built = new MetaExpedientSequenciaEntity();
			built.any = any;
			built.valor = 1;
			built.metaExpedient = metaExpedient;
		}
		public MetaExpedientSequenciaEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + any;
		result = prime * result
				+ ((metaExpedient == null) ? 0 : metaExpedient.hashCode());
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
		MetaExpedientSequenciaEntity other = (MetaExpedientSequenciaEntity) obj;
		if (any != other.any)
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
