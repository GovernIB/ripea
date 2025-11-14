package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.MetaExpedientComentariResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "metaexp_comment")
@Getter
@Setter
@NoArgsConstructor
public class MetaExpedientComentariResourceEntity extends BaseAuditableEntity<MetaExpedientComentariResource> {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "meta_expedient_id")
    protected MetaExpedientResourceEntity metaExpedient;
    @Column(name = "text", length = 1024)
    protected String text;
}
