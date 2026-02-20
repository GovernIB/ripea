package es.caib.ripea.persistence.entity.resourceentity;

import java.util.Date;

import es.caib.ripea.persistence.base.entity.ResourceEntity;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.model.ExcepcioLogResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExcepcioLogResourceEntity implements ResourceEntity<ExcepcioLogResource, Long> {

	private Long index;
	private EntitatDto entitat;
	private Date data = new Date();
	private Class<?> tipus;
	private Object objectId;
	private Class<?> objectClass;
	private String uri;
	private String param1;
	private String param2;
	private String message;
	private String stacktrace;
	
	@Override
	public Long getId() {
		return this.index;
	}

	@Override
	public boolean isNew() {
		return getId()==null;
	}
}