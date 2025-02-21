package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entitat de base de dades que representa un node.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "node")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class NodeResourceEntity extends ContingutResourceEntity {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(
			name = "metanode_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "metanode_node_fk"))
	protected MetaNodeResourceEntity metaNode;

//	@OneToMany(
//			mappedBy = "node",
//			cascade = { CascadeType.ALL },
//			fetch = FetchType.LAZY)
//	protected Set<DadaEntity> dades;

}
