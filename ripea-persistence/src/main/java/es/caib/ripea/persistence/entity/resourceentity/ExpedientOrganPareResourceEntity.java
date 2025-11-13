package es.caib.ripea.persistence.entity.resourceentity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.ForeignKey;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.ExpedientOrganPareResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "expedient_organpare")
@Getter
@Setter
@NoArgsConstructor
public class ExpedientOrganPareResourceEntity extends BaseAuditableEntity<ExpedientOrganPareResource> {

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "expedient_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "exporgpare_expedient_fk"))
	private ExpedientResourceEntity expedient;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "meta_expedient_organ_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "exporgpare_metaexporg_fk"))
	private MetaExpedientOrganGestorResourceEntity metaExpedientOrganGestor;
}