package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.model.ConsultaPinbalResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "consulta_pinbal")
@Getter
@Setter
@NoArgsConstructor
public class ConsultaPinbalResourceEntity extends BaseAuditableEntity<ConsultaPinbalResource> {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "entitat_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "entitat_pinbal_fk")
    protected EntitatResourceEntity entitat;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "servei", referencedColumnName = "codi", insertable = false, updatable = false)
    private PinbalServeiResourceEntity servei;
    @Column(name = "servei", length = 64, nullable = false)
    private String serveiCodi;

    @Enumerated(EnumType.STRING)
    @Column(name = "estat", length = 10, nullable = false)
    private ConsultaPinbalEstatEnumDto estat;

    @Column(name = "pinbal_idpeticion", length = 64)
    private String pinbalIdpeticion;

    @Column(name = "error", length = 4000)
    private String error;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "expedient_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "expedient_pinbal_fk")
    private ExpedientResourceEntity expedient;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "metaexpedient_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "metaexp_pinbal_fk")
    private MetaExpedientResourceEntity metaExpedient;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "document_pinbal_fk")
    private DocumentResourceEntity document;
}