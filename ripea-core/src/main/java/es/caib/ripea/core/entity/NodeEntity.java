/**
 * 
 */
package es.caib.ripea.core.entity;

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

	@ManyToOne(
			optional = true,
			fetch = FetchType.EAGER)
	@JoinColumn(name = "metanode_id")
	@ForeignKey(name = "ipa_metanode_node_fk")
	protected MetaNodeEntity metaNode;

	@OneToMany(mappedBy = "node", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
	protected Set<DadaEntity> dades;



	public MetaNodeEntity getMetaNode() {
		return metaNode;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
