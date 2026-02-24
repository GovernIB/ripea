package es.caib.ripea.persistence.entity.resourceentity;

import java.util.Date;

import es.caib.ripea.persistence.base.entity.ResourceEntity;
import es.caib.ripea.service.intf.dto.MonitorTascaEstatEnum;
import es.caib.ripea.service.intf.model.BackGroundTaskResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BackGroundTaskResourceEntity implements ResourceEntity<BackGroundTaskResource, String> {

	private String id;
	private String codi;
	private MonitorTascaEstatEnum estat;
	private Date dataInici;
	private Date dataFi;
	private Date properaExecucio;
	private String observacions;
	
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public boolean isNew() {
		return getId()==null;
	}
}
