package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.persistence.entity.config.MapToJsonConverter;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.ripea.service.intf.dto.IntegracioCodiEnum;
import es.caib.ripea.service.intf.model.IntegracioResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "integracio_accio")
@Getter
@Setter
@NoArgsConstructor
public class IntegracioResourceEntity extends BaseAuditableEntity<IntegracioResource> {

	@Column(name = "data", nullable = false)
	private Date data;

	@Enumerated(EnumType.STRING)
	@Column(name = "integracio_codi", nullable = false, length = 64)
	private IntegracioCodiEnum codi;

	@Column(name = "descripcio", length = 1024)
	private String descripcio;

	@Column(name = "endpoint", length = 1024)
	private String endpoint;

	@Lob
	@Convert(converter = MapToJsonConverter.class)
	@Column(name = "parametres")
	private Map<String, String> parametres;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipus", length = 32)
	private IntegracioAccioTipusEnumDto tipus;

	@Column(name = "temps_resposta")
	private long tempsResposta;

	@Enumerated(EnumType.STRING)
	@Column(name = "estat", nullable = false, length = 16)
	private IntegracioAccioEstatEnumDto estat;

	@Column(name = "entitat_codi", length = 64)
	private String entitatCodi;

	@Column(name = "error_descripcio", length = 2000)
	private String errorDescripcio;

	@Column(name = "excepcio_message", length = 2000)
	private String excepcioMessage;

	@Lob
	@Column(name = "excepcio_stacktrace")
	private String excepcioStacktrace;

}
