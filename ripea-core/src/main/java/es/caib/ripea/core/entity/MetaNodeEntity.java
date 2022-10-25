/**
 * 
 */
package es.caib.ripea.core.entity;

import es.caib.ripea.core.audit.RipeaAuditable;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Version;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe del model de dades que representa un meta-node.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_metanode")
@Inheritance(strategy=InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public abstract class MetaNodeEntity extends RipeaAuditable<Long> {

	@Column(name = "codi", length = 64, nullable = false)
	protected String codi;
	@Column(name = "nom", length = 256, nullable = false)
	protected String nom;
	@Column(name = "descripcio", length = 4000)
	protected String descripcio;
	@Column(name = "tipus", nullable = false)
	@Enumerated(EnumType.STRING)
	protected MetaNodeTipusEnum tipus;
	@Column(name = "actiu")
	protected boolean actiu = true;
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "ipa_entitat_metanode_fk")
	protected EntitatEntity entitat;
	@OneToMany(
			mappedBy = "metaNode",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	@OrderBy("ordre asc")
	private Set<MetaDadaEntity> metaDades = new HashSet<MetaDadaEntity>();
	@OneToMany(
			mappedBy = "metaNode",
			fetch = FetchType.LAZY)
	protected Set<NodeEntity> nodes = new HashSet<NodeEntity>();
	@Version
	private long version = 0;



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
	public Set<MetaDadaEntity> getMetaDades() {
		return metaDades;
	}
	public Set<NodeEntity> getNodes() {
		return nodes;
	}
	public boolean isActiu() {
		return actiu;
	}

	protected void update(
			String codi,
			String nom,
			String descripcio) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = truncateToFitUtf8ByteLength(descripcio, 4000);
	}

	public static String truncateToFitUtf8ByteLength(String s, int maxBytes) {
		if (s == null) {
			return null;
		}
		Charset charset = Charset.forName("UTF-8");
		CharsetDecoder decoder = charset.newDecoder();
		byte[] sba = s.getBytes(charset);
		if (sba.length <= maxBytes) {
			return s;
		}
		// Ensure truncation by having byte buffer = maxBytes
		ByteBuffer bb = ByteBuffer.wrap(sba, 0, maxBytes);
		CharBuffer cb = CharBuffer.allocate(maxBytes);
		// Ignore an incomplete character
		decoder.onMalformedInput(CodingErrorAction.IGNORE);
		decoder.decode(bb, cb, true);
		decoder.flush(cb);
		return new String(cb.array(), 0, cb.position());
	}

	public void updateActiu(
			boolean actiu) {
		this.actiu = actiu;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result + ((entitat == null) ? 0 : entitat.hashCode());
		result = prime * result + ((tipus == null) ? 0 : tipus.hashCode());
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
		MetaNodeEntity other = (MetaNodeEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		if (entitat == null) {
			if (other.entitat != null)
				return false;
		} else if (!entitat.equals(other.entitat))
			return false;
		if (tipus != other.tipus)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MetaNodeEntity: [" +
				"id: " + this.getId() + ", " +
				"codi: " + this.codi + ", " +
				"nom: " + this.nom + ", " +
				"descripcio: " + this.descripcio + ", " +
				"tipus: " + this.tipus + ", " +
				"actiu: " + this.actiu + ", " +
				"entitat: " + (this.entitat != null ? this.entitat.toString() : "NULL") + "]";
//				"metadades: " + this.metadades + "]";
	}
	
	private static final long serialVersionUID = -2299453443943600172L;

}
