package es.caib.ripea.persistence.entity.resourceentity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.MetaExpedientEstatResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entitat de base de dades que representa un node.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "expedient_estat")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class MetaExpedientEstatResourceEntity extends BaseAuditableEntity<MetaExpedientEstatResource> {

	@Column(name = "codi", length = 256, nullable = false)
	private String codi;
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	@Column(name = "ordre", nullable = false)
	private int ordre;
	@Column(name = "color", length = 256)
	private String color;
	@Column(name = "inicial")
	private boolean inicial;
	@Column(name = "responsable_codi")
	private String responsableCodi;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "metaexpedient_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "metaexp_expedientestat_fk"))
	private MetaExpedientResourceEntity metaExpedient;
	@OneToMany(
			mappedBy = "estatAdditional",
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY,
			orphanRemoval = true)
	private List<ExpedientResourceEntity> expedients;

}
