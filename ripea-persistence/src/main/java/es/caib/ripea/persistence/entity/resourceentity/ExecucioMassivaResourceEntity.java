package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExecucioMassivaContingutEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.model.ExecucioMassivaResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "execucio_massiva")
@Getter
@Setter
@NoArgsConstructor
public class ExecucioMassivaResourceEntity extends BaseAuditableEntity<ExecucioMassivaResource> {

    @Column(name = "tipus")
    @Enumerated(EnumType.STRING)
    private ExecucioMassivaTipusDto tipus;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_inici")
    private Date dataInici;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_fi")
    private Date dataFi;
    //	Paràmetres generació de ZIP
    @Column(name = "zip_carpetes")
    private Boolean carpetes;
    @Column(name = "zip_imprimible")
    private Boolean versioImprimible;
    @Column(name = "zip_nomFitxer")
    @Enumerated(EnumType.STRING)
    private FileNameOption nomFitxer;
    // Enviament a Portafirmes
    @Column(name = "pfirmes_motiu", length = 256)
    private String motiu;
    @Column(name = "pfirmes_priori")
    @Enumerated(EnumType.STRING)
    private PortafirmesPrioritatEnumDto prioritat = PortafirmesPrioritatEnumDto.NORMAL;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "pfirmes_datcad")
    private Date dataCaducitat;
    // S'emmagatzema com a String separat per comes; el recurs l'exposa com a String[]
    // (la conversió es fa a ExecucioMassivaResourceServiceImpl.afterConversion). El nom
    // ha de ser diferent del camp del recurs per evitar que el mapeig genèric intenti
    // assignar un String a un String[] (argument type mismatch).
    @Column(name = "pfirmes_responsables")
    private String portafirmesResponsablesString;
    @Column(name = "pfirmes_seqtipus")
    @Enumerated(EnumType.STRING)
    private MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus;
    @Column(name = "pfirmes_fluxid")
    private String portafirmesFluxId;
    @Column(name = "pfirmes_transid")
    private String portafirmesTransaccioId;
    @Column(name = "enviar_correu")
    private Boolean enviarCorreu;

    @ManyToOne(optional = false)
    @JoinColumn(name = "entitat_id")
    private EntitatEntity entitat;
    @Column(name = "rol_actual")
    private String rolActual;
    @Column(name = "pfirmes_avis_firma_parcial")
    private Boolean portafirmesAvisFirmaParcial;
    @Column(name = "document_nom")
    private String documentNom;

    @Column(name = "pfirmes_firma_parcial")
    private Boolean portafirmesFirmaParcial;

    @OneToMany(
            mappedBy = "execucioMassiva",
            cascade = {CascadeType.ALL},
            fetch = FetchType.EAGER)
    private List<ExecucioMassivaContingutResourceEntity> continguts = new ArrayList<>();
}
