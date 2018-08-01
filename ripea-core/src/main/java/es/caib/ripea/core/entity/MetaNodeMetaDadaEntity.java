/**
 * 
 */
package es.caib.ripea.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.audit.RipeaAuditable;

/**
 * Classe del model de dades que representa una relació entre
 * meta-node i meta-dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="ipa_metanode_metadada",
		uniqueConstraints={@UniqueConstraint(columnNames = {"metanode_id", "metadada_id"})})
@EntityListeners(AuditingEntityListener.class)
public class MetaNodeMetaDadaEntity extends RipeaAuditable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "metanode_id")
	@ForeignKey(name = "ipa_metanode_metadadanode_fk")
	private MetaNodeEntity metaNode;
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "metadada_id")
	@ForeignKey(name = "ipa_metadada_metadadanode_fk")
	private MetaDadaEntity metaDada;
	@Column(name = "multiplicitat", nullable = false)
	@Enumerated(EnumType.STRING)
	private MultiplicitatEnumDto multiplicitat;
	@Column(name = "readonly")
	private boolean readOnly;
	private int ordre;



	public MetaNodeEntity getMetaNode() {
		return metaNode;
	}
	public MetaDadaEntity getMetaDada() {
		return metaDada;
	}
	public MultiplicitatEnumDto getMultiplicitat() {
		return multiplicitat;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public int getOrdre() {
		return ordre;
	}

	public void update(
			MultiplicitatEnumDto multiplicitat,
			boolean readOnly) {
		this.multiplicitat = multiplicitat;
		this.readOnly = readOnly;
	}
	public void updateOrdre(int ordre) {
		this.ordre = ordre;
	}

	/**
	 * Obté el Builder per a crear objectes de tipus meta-node - meta-dada.
	 * 
	 * @param metaNode
	 *            El meta-node.
	 * @param metaDada
	 *            La meta-dada.
	 * @param multiplicitat
	 *            El valor de l'atribut multiplicitat.
	 * @param readOnly
	 *            El valor de l'atribut readOnly.
	 * @param ordre
	 *            El valor de l'atribut ordre.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			MetaNodeEntity metaNode,
			MetaDadaEntity metaDada,
			MultiplicitatEnumDto multiplicitat,
			boolean readOnly,
			int ordre) {
		return new Builder(
				metaNode,
				metaDada,
				multiplicitat,
				readOnly,
				ordre);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		MetaNodeMetaDadaEntity built;
		Builder(
				MetaNodeEntity metaNode,
				MetaDadaEntity metaDada,
				MultiplicitatEnumDto multiplicitat,
				boolean readOnly,
				int ordre) {
			built = new MetaNodeMetaDadaEntity();
			built.metaNode = metaNode;
			built.metaDada = metaDada;
			built.multiplicitat = multiplicitat;
			built.readOnly = readOnly;
			built.ordre = ordre;
		}
		public MetaNodeMetaDadaEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((metaDada == null) ? 0 : metaDada.hashCode());
		result = prime * result
				+ ((metaNode == null) ? 0 : metaNode.hashCode());
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
		MetaNodeMetaDadaEntity other = (MetaNodeMetaDadaEntity) obj;
		if (metaDada == null) {
			if (other.metaDada != null)
				return false;
		} else if (!metaDada.equals(other.metaDada))
			return false;
		if (metaNode == null) {
			if (other.metaNode != null)
				return false;
		} else if (!metaNode.equals(other.metaNode))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
