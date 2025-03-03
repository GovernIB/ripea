package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.ExpedientComentariResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "exp_comment")
@Getter
@Setter
@NoArgsConstructor
public class ExpedientComentariResourceEntity extends BaseAuditableEntity<ExpedientComentariResource> {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "expedient_id")
    protected ExpedientResourceEntity expedient;
    @Column(name = "text", length = 1024)
    protected String text;
}
