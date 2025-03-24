package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.model.MetaExpedientTascaResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "metaexp_tasca")
@Getter
@Setter
@NoArgsConstructor
public class MetaExpedientTascaResourceEntity extends BaseAuditableEntity<MetaExpedientTascaResource> {

    @Column(name = "codi", length = 64, nullable = false)
    private String codi;
    @Column(name = "nom", length = 256, nullable = false)
    private String nom;
    @Column(name = "descripcio", length = 1024, nullable = false)
    private String descripcio;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "responsable")
    protected UsuariResourceEntity responsable;
    @Column(name = "activa", nullable = false)
    private boolean activa;
    @Temporal(TemporalType.DATE)
    @Column(name = "data_limit")
    private Date dataLimit;
    @Column(name = "DURACIO")
    private Integer duracio;
    @Column(name = "PRIORITAT", length = 16)
    @Enumerated(EnumType.STRING)
    private PrioritatEnumDto prioritat;

    @ManyToOne
    @JoinColumn(name = "estat_crear_tasca_id")
    private ExpedientEstatResourceEntity estatCrearTasca;

    @ManyToOne
    @JoinColumn(name = "estat_finalitzar_tasca_id")
    private ExpedientEstatResourceEntity estatFinalitzarTasca;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "meta_expedient_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "metaexp_metaexptas_fk")
    private MetaExpedientResourceEntity metaExpedient;

//    @OneToMany(
//            mappedBy = "metaExpedientTasca",
//            cascade = CascadeType.ALL,
//            fetch = FetchType.LAZY,
//            orphanRemoval = true)
//    private List<MetaExpedientTascaValidacioEntity> validacions = new ArrayList<MetaExpedientTascaValidacioEntity>();
}
