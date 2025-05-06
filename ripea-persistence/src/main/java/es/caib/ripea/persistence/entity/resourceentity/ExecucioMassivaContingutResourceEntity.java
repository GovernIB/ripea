package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExecucioMassivaEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.model.ExecucioMassivaContingutResource;
import es.caib.ripea.service.intf.model.ExecucioMassivaResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "massiva_contingut")
@Getter
@Setter
@NoArgsConstructor
public class ExecucioMassivaContingutResourceEntity extends BaseAuditableEntity<ExecucioMassivaContingutResource> {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "datA_inici")
    private Date dataInici;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_fi")
    private Date dataFi;
    @Column(name = "estat")
    @Enumerated(EnumType.STRING)
    private ExecucioMassivaEstatDto estat;
    @Column(name = "error", length = 2046)
    private String error;
    @Column(name = "ordre", nullable = false)
    private int ordre;

    @Column(name = "element_id")
    private Long elementId;
    @Column(name = "element_nom", length = 256)
    private String elementNom;
    @Enumerated(EnumType.STRING)
    @Column(name = "element_tipus", length = 16)
    private ElementTipusEnumDto elementTipus;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "execucio_massiva_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "exmas_exmascon_fk")
    private ExecucioMassivaResourceEntity execucioMassiva;
}
