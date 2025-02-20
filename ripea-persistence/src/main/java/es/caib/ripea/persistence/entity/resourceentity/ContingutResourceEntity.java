package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import es.caib.ripea.service.intf.model.ExpedientResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

/**
 * Entitat de base de dades que representa un contingut.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "contingut")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class ContingutResourceEntity extends BaseAuditableEntity<ExpedientResource> {

	@Column(name = "nom", length = 256, nullable = false)
	protected String nom;
	@Column(name = "tipus", nullable = false)
	protected ContingutTipusEnumDto tipus;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(
			name = "pare_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "pare_contingut_fk"))
	protected ContingutResourceEntity pare;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "entitat_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "entitat_contingut_fk"))
	protected EntitatEntity entitat;

	@OneToMany(
			mappedBy = "pare",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	protected Set<ContingutResourceEntity> fills;

}
