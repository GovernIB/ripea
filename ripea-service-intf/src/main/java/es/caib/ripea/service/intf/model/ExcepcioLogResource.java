package es.caib.ripea.service.intf.model;

import java.util.Date;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.Resource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
        quickFilterFields = { "uri", "message" },
        descriptionField = "message"
)
public class ExcepcioLogResource implements Resource<Long> {

	private static final long serialVersionUID = -8318258483303373983L;

	private Long id;
	private Long entitatId;
	private Date data;
	private String tipus;
	private String objectId;
	private String objectClass;
	private String uri;
	private String param1;
	private String param2;
	private String message;
	private String stacktrace;

	@Override
	public Long getId() {
		return this.id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}
}
