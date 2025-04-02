package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.MetaExpedientSequenciaResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "metaexp_seq")
@Getter
@Setter
@NoArgsConstructor
public class MetaExpedientSequenciaResourceEntity extends BaseAuditableEntity<MetaExpedientSequenciaResource> {
    @Column(name = "anio")
    private int any;
    @Column(name = "valor")
    private Long valor;
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "meta_expedient_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "metaexp_metaexpseq_fk")
    private MetaExpedientResourceEntity metaExpedient;
}