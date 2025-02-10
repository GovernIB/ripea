/**
 * 
 */
package es.caib.ripea.core.persistence;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

/**
 * Classe del model de dades que representa un node.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_node")
@Inheritance(strategy=InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public abstract class NodeEntity extends ContingutEntity {

	@Getter
	@ManyToOne(
			optional = true,
			fetch = FetchType.EAGER)
	@JoinColumn(name = "metanode_id")
	@ForeignKey(name = "ipa_metanode_node_fk")
	protected MetaNodeEntity metaNode;

	@OneToMany(mappedBy = "node", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
	protected Set<DadaEntity> dades;

	@Override
	public String toString() {
		return "NodeEntity: [" +
				"contingut: " + super.toString() + ", " +
				"metanode: " + (this.metaNode != null ? this.metaNode.toString() : null) + "]";
	}
	
	private static final long serialVersionUID = -2299453443943600172L;

}
