package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.model.DocumentNotificacioResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("DocumentNotificacioEntity")
public class DocumentNotificacioResourceEntity extends DocumentEnviamentResourceEntity<DocumentNotificacioResource> {

    @Column(name = "not_tipus")
    private DocumentNotificacioTipusEnumDto tipus;
    @Column(name = "not_data_prog")
    @Temporal(TemporalType.DATE)
    private Date dataProgramada;
    @Column(name = "not_retard")
    private Integer retard;
    @Column(name = "not_data_caducitat")
    @Temporal(TemporalType.DATE)
    private Date dataCaducitat;
    @Column(name = "not_env_id", length = 100)
    private String notificacioIdentificador;
    @Column(name = "not_env_cert_arxiuid", length = 50)
    private String enviamentCertificacioArxiuId;
    @Enumerated(EnumType.STRING)
    @Column(name = "servei_tipus", length = 10)
    private ServeiTipusEnumDto serveiTipusEnum;
    @Column(name="entrega_postal")
    private Boolean entregaPostal;
    @Column(name="not_env_registre_data")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registreData;
    @Column(name="not_env_registre_numero", length = 19)
    private Integer registreNumero;
    @Column(name="not_env_registre_num_formatat", length = 50)
    private String registreNumeroFormatat;
    @ManyToOne(optional = true)
    @JoinColumn(name = "not_emisor_id")
    private OrganGestorResourceEntity emisor;
    @Column(name = "not_data_enviada")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEnviada;
    @Column(name = "not_data_finalitzada")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataFinalitzada;
//    @OneToMany(
//            mappedBy = "notificacio",
//            fetch = FetchType.LAZY,
//            orphanRemoval = true)
//    private Set<DocumentEnviamentInteressatEntity> documentEnviamentInteressats = new HashSet<DocumentEnviamentInteressatEntity>();
    @Column(name = "notificacio_estat")
    @Enumerated(EnumType.STRING)
    protected DocumentNotificacioEstatEnumDto notificacioEstat;

}