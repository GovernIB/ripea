package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.persistence.base.entity.ResourceEntity;
import es.caib.ripea.persistence.entity.ContingutLogEntity;
import es.caib.ripea.persistence.entity.ContingutMovimentEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import es.caib.ripea.service.intf.model.CarpetaResource;
import es.caib.ripea.service.intf.model.ContingutLogResource;
import es.caib.ripea.service.intf.model.ContingutResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "cont_log")
@Getter
@Setter
@NoArgsConstructor
public class ContingutLogResourceEntity extends BaseAuditableEntity<ContingutLogResource> {

    @Column(name = "tipus", nullable = false)
    private LogTipusEnumDto tipus;
    @Column(name = "objecte_tipus")
    private LogObjecteTipusEnumDto objecteTipus;
    @Column(name = "objecte_log_tipus")
    private LogTipusEnumDto objecteLogTipus;
    @Column(name = "param1", length = 256)
    private String param1;
    @Column(name = "param2", length = 256)
    private String param2;

    @Column(name = "objecte_id", length = 64)
    private String objecteId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "contingut_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "contingut_contlog_fk")
    protected ContingutResourceEntity contingut;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "contmov_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "contmov_contlog_fk")
    protected ContingutMovimentResourceEntity moviment;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "pare_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "pare_contlog_fk")
    protected ContingutLogResourceEntity pare;
}
