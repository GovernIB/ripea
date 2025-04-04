package es.caib.ripea.persistence.entity.resourceentity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.GrupResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "metaexp_organ")
@Getter
@Setter
@NoArgsConstructor
public class MetaExpedientOrganGestorResourceEntity extends BaseAuditableEntity<GrupResource> {

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "meta_expedient_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "metaexp_metaexporg_fk"))
	private MetaExpedientResourceEntity metaExpedient;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "organ_gestor_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "organ_metaexporg_fk"))
	private OrganGestorResourceEntity organGestor;
}