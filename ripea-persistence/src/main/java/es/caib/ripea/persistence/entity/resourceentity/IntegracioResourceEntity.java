package es.caib.ripea.persistence.entity.resourceentity;

import java.util.Date;
import java.util.Map;

import es.caib.ripea.persistence.base.entity.ResourceEntity;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.ripea.service.intf.dto.IntegracioDto;
import es.caib.ripea.service.intf.model.IntegracioResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
public class IntegracioResourceEntity implements ResourceEntity<IntegracioResource, Long> {

	private Long index;
	@Id
	private Long timestamp;
	private Date data;
	private String descripcio;
	private String endpoint;
	private Map<String, String> parametres;
	private IntegracioDto integracio;
	private IntegracioAccioTipusEnumDto tipus;
	private long tempsInici;
	private long tempsResposta;
	private IntegracioAccioEstatEnumDto estat;
	private EntitatDto entitat;
	private String entitatCodi;
	private String errorDescripcio;
	private String excepcioMessage;
	private String excepcioStacktrace;
	
	@Override
	public Long getId() {
		return this.timestamp;
	}

	@Override
	public boolean isNew() {
		return getId()==null;
	}
}
