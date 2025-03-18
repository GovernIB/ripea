package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.ExpedientTascaComentariResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "exp_tasca_comment")
@Getter
@Setter
@NoArgsConstructor
public class ExpedientTascaComentariResourceEntity extends BaseAuditableEntity<ExpedientTascaComentariResource> {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "exp_tasca_id")
    protected ExpedientTascaResourceEntity expedientTasca;
    @Column(name = "text", length = 1024)
    protected String text;
}
