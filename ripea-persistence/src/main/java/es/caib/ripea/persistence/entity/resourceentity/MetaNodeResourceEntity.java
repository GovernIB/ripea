/**
 * 
 */
package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.persistence.entity.MetaNodeTipusEnum;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Classe del model de dades que representa un meta-node.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "metanode")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class MetaNodeResourceEntity extends BaseAuditableEntity<MetaExpedientResource> {

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
	@JoinColumn(
			name = "entitat_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "entitat_metanode_fk"))
	protected EntitatResourceEntity entitat;
//	@OneToMany(
//			mappedBy = "metaNode",
//			fetch = FetchType.LAZY,
//			cascade = CascadeType.ALL,
//			orphanRemoval = true)
//	@OrderBy("ordre asc")
//	private Set<MetaDadaEntity> metaDades = new HashSet<MetaDadaEntity>();
//	@OneToMany(
//			mappedBy = "metaNode",
//			fetch = FetchType.LAZY)
//	protected Set<NodeResourceEntity> nodes = new HashSet<>();

}
