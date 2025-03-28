package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import es.caib.ripea.service.intf.model.MetaDadaResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "metadada")
@Getter
@Setter
@NoArgsConstructor
public class MetaDadaResourceEntity extends BaseAuditableEntity<MetaDadaResource> {

    @Column(name = "codi", length = 64, nullable = false)
    private String codi;
    @Column(name = "nom", length = 256, nullable = false)
    private String nom;
    @Column(name = "tipus", nullable = false)
    private MetaDadaTipusEnumDto tipus;
    @Column(name = "multiplicitat", nullable = false)
    private MultiplicitatEnumDto multiplicitat;
    @Column(name = "valor")
    private String valor;
    @Column(name = "descripcio", length = 1024)
    private String descripcio;
    @Column(name = "activa")
    private boolean activa;
    @Column(name = "read_only")
    private boolean readOnly;
    @Column(name = "ordre")
    private int ordre;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "meta_node_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "metanode_metadada_fk")
    private MetaNodeResourceEntity metaNode;

    @Column(name = "no_aplica")
    private boolean noAplica;
    @Column(name = "enviable")
    private boolean enviable;
    @Column(name = "metadada_arxiu")
    private String metadadaArxiu;

    @Version
    private long version = 0;
}
