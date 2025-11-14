package es.caib.ripea.persistence.entity.resourceentity;

import javax.persistence.*;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.MetaExpedientOrganGestorResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "metaexp_organ")
@Getter
@Setter
@NoArgsConstructor
public class MetaExpedientOrganGestorResourceEntity extends BaseAuditableEntity<MetaExpedientOrganGestorResource> {

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "meta_expedient_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "metaexp_metaexporg_fk"))
	private MetaExpedientResourceEntity metaExpedient;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(
			name = "organ_gestor_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "organ_metaexporg_fk"))
	private OrganGestorResourceEntity organGestor;

    public MetaExpedientOrganGestorResourceEntity(MetaExpedientResourceEntity metaExpedient, OrganGestorResourceEntity organGestor) {
        this.metaExpedient = metaExpedient;
        this.organGestor = organGestor;
    }
}