package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.ContingutMovimentResource;
import org.hibernate.annotations.ForeignKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "cont_mov")
@Getter
@Setter
@NoArgsConstructor
public class ContingutMovimentResourceEntity extends BaseAuditableEntity<ContingutMovimentResource> {

    @Column(name = "comentari", length = 256)
    protected String comentari;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "contingut_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "contingut_contmov_fk")
    protected ContingutResourceEntity contingut;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "origen_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "origen_contmov_fk")
    protected ContingutResourceEntity origen;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "desti_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "desti_contmov_fk")
    protected ContingutResourceEntity desti;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "remitent_codi")
    @ForeignKey(name = BaseConfig.DB_PREFIX + "remitent_contmov_fk")
    protected UsuariResourceEntity remitent;
}
