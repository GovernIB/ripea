package es.caib.ripea.persistence.entity.resourceentity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import es.caib.ripea.persistence.base.entity.ResourceEntity;
import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.CarpetaResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "carpeta")
@Getter
@Setter
@NoArgsConstructor
public class CarpetaResourceEntity extends ContingutEntity implements ResourceEntity<CarpetaResource, Long> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "expedient_relacionat",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "carpeta_exprel_fk"))
	private ExpedientResourceEntity expedientRelacionat;
}
