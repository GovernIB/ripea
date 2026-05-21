package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.ExcepcioLogResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "excepcio_log")
@Getter
@Setter
@NoArgsConstructor
public class ExcepcioLogResourceEntity extends BaseAuditableEntity<ExcepcioLogResource> {

	@Column(name = "data", nullable = false)
	private Date data = new Date();

	@Column(name = "tipus", length = 512)
	private String tipus;

	@Column(name = "object_id", length = 512)
	private String objectId;

	@Column(name = "object_class", length = 512)
	private String objectClass;

	@Column(name = "uri", length = 1024)
	private String uri;

	@Column(name = "param1", length = 512)
	private String param1;

	@Column(name = "param2", length = 512)
	private String param2;

	@Column(name = "message", length = 4000)
	private String message;

	@Lob
	@Column(name = "stacktrace")
	private String stacktrace;

	@Column(name = "entitat_id")
	private Long entitatId;

}
