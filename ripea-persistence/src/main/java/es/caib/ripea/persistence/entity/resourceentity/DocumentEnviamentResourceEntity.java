package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.DocumentEnviamentAnnexEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.service.intf.dto.ServeiTipusEnumDto;
import es.caib.ripea.service.intf.model.DocumentEnviamentResource;
import es.caib.ripea.service.intf.model.InteressatResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "document_enviament")
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorColumn(name="dtype")
public abstract class DocumentEnviamentResourceEntity<R extends DocumentEnviamentResource> extends BaseAuditableEntity<R> {

    @Column(name = "estat", nullable = false)
    @Enumerated(EnumType.STRING)
    protected DocumentEnviamentEstatEnumDto estat;
    @Column(name = "assumpte", length = 256, nullable = false)
    protected String assumpte;
    @Column(name = "observacions", length = 256)
    protected String observacions;
    @Column(name = "enviat_data")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date enviatData;
    @Column(name = "processat_data")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date processatData;
    @Column(name = "cancelat_data")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date cancelatData;
    @Column(name = "error")
    protected boolean error;
    @Column(name = "error_desc", length = ERROR_DESC_TAMANY)
    protected String errorDescripcio;
    @Column(name = "intent_num")
    protected int intentNum;
    @Column(name = "intent_data")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date intentData;
    @Column(name = "intent_proxim_data")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date intentProximData;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "expedient_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "expedient_docenv_fk")
    protected ExpedientResourceEntity expedient;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "document_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "document_docenv_fk")
    protected DocumentResourceEntity document;

//    @OneToMany(
//            cascade = CascadeType.ALL,
//            fetch = FetchType.LAZY,
//            mappedBy = "documentEnviament",
//            orphanRemoval = true)
//    protected List<DocumentEnviamentAnnexEntity> annexos;

    private static final int ERROR_DESC_TAMANY = 2000;
}