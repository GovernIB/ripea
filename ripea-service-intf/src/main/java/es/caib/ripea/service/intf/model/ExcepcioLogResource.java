package es.caib.ripea.service.intf.model;

import java.util.Date;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.dto.EntitatDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
        quickFilterFields = { "assumpte", "missatge" },
        descriptionField = "assumpte"
)
public class ExcepcioLogResource implements Resource<Long> {

	private static final long serialVersionUID = -8318258483303373983L;
	
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
	public void setId(Long id) {
		this.index=id;
	}
}