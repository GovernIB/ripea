package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.DadaResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "dada")
@Getter
@Setter
@NoArgsConstructor
public class DadaResourceEntity extends BaseAuditableEntity<DadaResource> {

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "metadada_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "metadada_dada_fk")
    protected MetaDadaResourceEntity metaDada;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "node_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "node_dada_fk")
    protected NodeResourceEntity node;

    @Column(name = "valor", length = 256, nullable = false)
    protected String valor;
    @NotNull
    @Column(name = "ordre")
    protected Integer ordre;
    @Version
    private long version = 0;
}
