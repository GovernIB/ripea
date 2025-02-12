/**
 * 
 */
package es.caib.ripea.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.Getter;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Set;

/**
 * Classe del model de dades que representa un node.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "node")
@Inheritance(strategy=InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public abstract class NodeEntity extends ContingutEntity {

	@Getter
	@ManyToOne(
			optional = true,
			fetch = FetchType.EAGER)
	@JoinColumn(name = "metanode_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "metanode_node_fk")
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
