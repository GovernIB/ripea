package es.caib.ripea.persistence.entity.resourceentity;

import java.util.Date;

import javax.persistence.*;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.AvisNivellEnumDto;
import es.caib.ripea.service.intf.model.AvisResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "avis")
@Getter
@Setter
@NoArgsConstructor
public class AvisResourceEntity extends BaseAuditableEntity<AvisResource> {
	@Column(name = "assumpte", length = 256, nullable = false)
	private String assumpte;
	@Column(name = "missatge", length = 2048, nullable = false)
	private String missatge;
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inici", nullable = false)
	private Date dataInici;
	@Temporal(TemporalType.DATE)
	@Column(name = "data_final")
	private Date dataFinal;
	@Column(name = "actiu", nullable = false)
	private Boolean actiu;
	@Column(name = "avis_nivell", length = 2048, nullable = false)
	@Enumerated(EnumType.STRING)
	private AvisNivellEnumDto avisNivell;
	@Column(name = "avis_admin", nullable = false)
	private Boolean avisAdministrador;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "entitat_id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "avis_entitat_fk")
	)
	private EntitatResourceEntity entitat;
}
