package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.entity.DadaEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

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
public class NodeResourceEntity extends ContingutResourceEntity {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(
			name = "metanode_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "metanode_node_fk"))
	protected MetaNodeEntity metaNode;

	@OneToMany(
			mappedBy = "node",
			cascade = { CascadeType.ALL },
			fetch = FetchType.LAZY)
	protected Set<DadaEntity> dades;

}
