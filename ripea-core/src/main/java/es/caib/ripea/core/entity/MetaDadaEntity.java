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
import javax.persistence.Version;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.audit.RipeaAuditable;

/**
 * Classe del model de dades que representa una meta-dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(
		name = "ipa_metadada",
		uniqueConstraints = {
				@UniqueConstraint(name = "ipa_metadada_metanode_codi_uk", columnNames = { "meta_node_id", "codi" })
		}
)
@EntityListeners(AuditingEntityListener.class)
public class MetaDadaEntity extends RipeaAuditable<Long> {

	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	@Column(name = "tipus", nullable = false)
	private MetaDadaTipusEnumDto tipus;
	@Column(name = "multiplicitat", nullable = false)
	private MultiplicitatEnumDto multiplicitat;
	@Column(name = "valor")
	private String valor;
	@Column(name = "descripcio", length = 1024)
	private String descripcio;
	@Column(name = "activa")
	private boolean activa;
	@Column(name = "read_only")
	private boolean readOnly;
	@Column(name = "ordre")
	private int ordre;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "meta_node_id")
	@ForeignKey(name = "ipa_metanode_metadada_fk")
	private MetaNodeEntity metaNode;
	@Column(name = "no_aplica")
	private boolean noAplica;
	@Column(name = "enviable")
	private boolean enviable;
	@Column(name = "metadada_arxiu")
	private String metadadaArxiu;
	
	@Version
	private long version = 0;

	public String getCodi() {
		return codi;
	}
	public String getNom() {
		return nom;
	}
	public MetaDadaTipusEnumDto getTipus() {
		return tipus;
	}
	public MultiplicitatEnumDto getMultiplicitat() {
		return multiplicitat;
	}
	public String getValor() {
		return valor;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public boolean isActiva() {
		return activa;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public int getOrdre() {
		return ordre;
	}
	public MetaNodeEntity getMetaNode() {
		return metaNode;
	}
	public long getVersion() {
		return version;
	}
	public boolean isNoAplica() {
		return noAplica;
	}
	public boolean isEnviable() {
		return enviable;
	}
	public String getMetadadaArxiu() {
		return metadadaArxiu;
	}
	
	public void update(
			String codi,
			String nom,
			MetaDadaTipusEnumDto tipus,
			MultiplicitatEnumDto multiplicitat,
			Object valor,
			String descripcio,
			boolean readOnly,
			boolean noAplica,
			boolean enviable,
			String metadadaArxiu) {
		this.codi = codi;
		this.nom = nom;
		this.tipus = tipus;
		this.multiplicitat = multiplicitat;
		this.valor = DadaEntity.getDadaValorPerEmmagatzemar(tipus, valor);
		this.descripcio = descripcio;
		this.readOnly = readOnly;
		this.noAplica = noAplica;
		this.enviable = enviable;
		this.metadadaArxiu = metadadaArxiu;
	}
	public void update(
			String codi,
			String nom,
			MetaDadaTipusEnumDto tipus,
			String descripcio,
			boolean readOnly) {
		this.codi = codi;
		this.nom = nom;
		this.tipus = tipus;
		this.descripcio = descripcio;
		this.readOnly = readOnly;
	}
	public void updateActiva(boolean activa) {
		this.activa = activa;
	}
	public void updateOrdre(int ordre) {
		this.ordre = ordre;
	}

	public static Builder getBuilder(
			String codi,
			String nom,
			MetaDadaTipusEnumDto tipus,
			MultiplicitatEnumDto multiplicitat,
			Object valor,
			boolean readOnly,
			int ordre,
			MetaNodeEntity metaNode,
			boolean noAplica,
			boolean enviable,
			String metadadaArxiu) {
		return new Builder(
				codi,
				nom,
				tipus,
				multiplicitat,
				valor,
				readOnly,
				ordre,
				metaNode,
				noAplica,
				enviable,
				metadadaArxiu);
	}
	public static class Builder {
		MetaDadaEntity built;
		Builder(
				String codi,
				String nom,
				MetaDadaTipusEnumDto tipus,
				MultiplicitatEnumDto multiplicitat,
				Object valor,
				boolean readOnly,
				int ordre,
				MetaNodeEntity metaNode,
				boolean noAplica,
				boolean enviable,
				String metadadaArxiu) {
			built = new MetaDadaEntity();
			built.codi = codi;
			built.nom = nom;
			built.tipus = tipus;
			built.multiplicitat = multiplicitat;
			built.valor = DadaEntity.getDadaValorPerEmmagatzemar(tipus, valor);
			built.readOnly = readOnly;
			built.ordre = ordre;
			built.metaNode = metaNode;
			built.activa = true;
			built.noAplica = noAplica;
			built.enviable = enviable;
			built.metadadaArxiu = metadadaArxiu;
		}
		public Builder descripcio(String descripcio) {
			built.descripcio = descripcio;
			return this;
		}
		public MetaDadaEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result + ((metaNode == null) ? 0 : metaNode.hashCode());
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
		MetaDadaEntity other = (MetaDadaEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
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