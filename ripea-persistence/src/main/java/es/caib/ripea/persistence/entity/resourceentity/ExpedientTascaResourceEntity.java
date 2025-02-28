package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.model.ExpedientTascaResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "expedient_tasca")
@Getter
@Setter
@NoArgsConstructor
public class ExpedientTascaResourceEntity extends BaseAuditableEntity<ExpedientTascaResource> {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "expedient_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "exp_exptasc_fk")
    private ExpedientResourceEntity expedient;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "metaexp_tasca_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "metaexptasca_exptasc_fk")
    private MetaExpedientTascaResourceEntity metaExpedientTasca;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_actual_codi")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "usuari_exptasc_fk")
    private UsuariResourceEntity responsableActual;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = BaseConfig.DB_PREFIX + "expedient_tasca_resp",
            joinColumns = {@JoinColumn(name = "tasca_id", referencedColumnName="id")},
            inverseJoinColumns = {@JoinColumn(name = "responsable_codi")})
    @org.hibernate.annotations.ForeignKey(
            name = BaseConfig.DB_PREFIX + "expedient_tasca_fk",
            inverseName = BaseConfig.DB_PREFIX + "expedient_tascaresp_fk")
    private List<UsuariResourceEntity> responsables = new ArrayList<UsuariResourceEntity>();

    @OneToOne
    @JoinColumn(name = "delegat")
    private UsuariResourceEntity delegat;

//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = BaseConfig.DB_PREFIX + "expedient_tasca_obse",
//            joinColumns = {@JoinColumn(name = "tasca_id", referencedColumnName="id")},
//            inverseJoinColumns = {@JoinColumn(name = "observador_codi")})
//    @org.hibernate.annotations.ForeignKey(
//            name = BaseConfig.DB_PREFIX + "expedient_obse_tasca_fk",
//            inverseName = BaseConfig.DB_PREFIX + "expedient_tascaobse_fk")
//    private List<UsuariResourceEntity> observadors = new ArrayList<UsuariResourceEntity>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_inici", nullable = false)
    private Date dataInici;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_fi")
    private Date dataFi;

    @Column(name = "estat", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private TascaEstatEnumDto estat;

    @Column(name = "motiu_rebuig", length = 1024)
    private String motiuRebuig;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_limit")
    private Date dataLimit;

    @Column(name = "DURACIO")
    private Integer duracio;

    @Column(name = "PRIORITAT", length = 16)
    @Enumerated(EnumType.STRING)
    private PrioritatEnumDto prioritat;

//	@Column(name = "comentari", length = 1024)
//	private String comentari;

//    @OneToMany(
//            mappedBy = "expedientTasca",
//            cascade = CascadeType.ALL,
//            orphanRemoval = true)
//    @OrderBy("createdDate")
//    private List<ExpedientTascaComentariEntity> comentaris = new ArrayList<>();

    @Column(name = "titol", length = 255)
    private String titol;

    @Column(name = "observacions", length = 1024)
    private String observacions;
}
