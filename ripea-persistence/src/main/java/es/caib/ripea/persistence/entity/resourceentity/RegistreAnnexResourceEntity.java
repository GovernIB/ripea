package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.distribucio.rest.client.integracio.domini.*;
import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.model.RegistreAnnexResource;
import es.caib.ripea.service.intf.registre.RegistreAnnexFirmaTipusEnum;
import es.caib.ripea.service.intf.registre.RegistreAnnexNtiEstadoElaboracionEnum;
import es.caib.ripea.service.intf.registre.RegistreAnnexNtiOrigenEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "registre_annex")
@Getter
@Setter
@NoArgsConstructor
public class RegistreAnnexResourceEntity extends BaseAuditableEntity<RegistreAnnexResource> {

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "contingut")
    private byte[] contingut;
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "firma_contingut")
    private byte[] firmaContingut;
    @Enumerated(EnumType.STRING)
    @Column(name = "firma_perfil", length = 20)
    private FirmaPerfil firmaPerfil;
    @Column(name = "firma_tamany")
    private long firmaTamany;
    @Enumerated(EnumType.STRING)
    @Column(name = "firma_tipus", length = 10)
    private RegistreAnnexFirmaTipusEnum firmaTipus;
    @Column(name = "firma_nom", length = 80)
    private String firmaNom;
    @Column(name = "nom", length = 80, nullable = false)
    private String nom;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "nti_fecha_captura", nullable = false)
    private Date ntiFechaCaptura;
    @Enumerated(EnumType.STRING)
    @Column(name = "nti_origen", length = 20, nullable = false)
    private RegistreAnnexNtiOrigenEnum ntiOrigen;
    @Enumerated(EnumType.STRING)
    @Column(name = "nti_tipo_doc", length = 20, nullable = false)
    private NtiTipoDocumentoEnumDto ntiTipoDocumental;
    @Column(name = "observacions", length = 50)
    private String observacions;
    @Enumerated(EnumType.STRING)
    @Column(name = "sicres_tipo_doc", length = 20 , nullable = false)
    private SicresTipoDocumentoEnumDto sicresTipoDocumento;
    @Enumerated(EnumType.STRING)
    @Column(name = "sicres_validez_doc", length = 30)
    private SicresValidezDocumentoEnumDto sicresValidezDocumento;
    @Enumerated(EnumType.STRING)
    @Column(name = "nti_estado_elaboracio", length = 50, nullable = false)
    private RegistreAnnexNtiEstadoElaboracionEnum ntiEstadoElaboracion;
    @Column(name = "tamany", nullable = false)
    private long tamany;
    @Column(name = "tipus_mime", length = 255)
    private String tipusMime;
    @Column(name = "titol", length = 200, nullable = false)
    private String titol;
    @Column(name = "uuid", length = 100)
    private String uuid;

    // TODO: when document is created and moved in arxiu estat should be changed to MOGUT
    @Enumerated(EnumType.STRING)
    @Column(name = "estat", length = 20, nullable = false)
    private RegistreAnnexEstatEnumDto estat;
    @Column(name = "error", length = 4000)
    private String error;

    @Column(name = "val_ok")
    private boolean validacioFirmaCorrecte;
    @Column(name = "val_error")
    private String validacioFirmaErrorMsg;
    @Enumerated(EnumType.STRING)
    @Column(name = "annex_estat")
    private ArxiuEstatEnumDto annexArxiuEstat;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "registre_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "annex_registre_fk")
    private RegistreResourceEntity registre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "document_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "annex_document_fk")
    private DocumentResourceEntity document;
}