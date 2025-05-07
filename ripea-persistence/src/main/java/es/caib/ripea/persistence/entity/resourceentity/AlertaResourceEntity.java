package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.AlertaResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "alerta")
@Getter
@Setter
@NoArgsConstructor
public class AlertaResourceEntity extends BaseAuditableEntity<AlertaResource> {

    @Column(name = "text", length = 1024, nullable = false)
    private String text;
    @Column(name = "error", length = 2048)
    private String error;
    @Column(name = "llegida", nullable = false)
    private Boolean llegida;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "contingut_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "contingut_alerta_fk")
    protected ContingutResourceEntity contingut;
}
