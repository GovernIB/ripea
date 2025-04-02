package es.caib.ripea.persistence.entity.resourceentity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import es.caib.ripea.persistence.base.entity.ResourceEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ExpedientSeguidorId;
import es.caib.ripea.service.intf.model.ExpedientSeguidorResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "expedient_seguidor")
@Getter
@Setter
@NoArgsConstructor
public class ExpedientSeguidorResourceEntity implements ResourceEntity<ExpedientSeguidorResource, ExpedientSeguidorId> {
	
	@EmbeddedId
    private ExpedientSeguidorId id;
	
	@Override	
	public ExpedientSeguidorId getId() {
		return id;
	}

	@Override
	public boolean isNew() {
		return null == getId();
	}
	
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("expedient_id")
    @JoinColumn(name = "expedient_id", referencedColumnName = "id")
	private ExpedientResourceEntity expedient;
	
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("seguidor_codi")
    @JoinColumn(name = "seguidor_codi", referencedColumnName = "codi")
	private UsuariResourceEntity seguidor;
}