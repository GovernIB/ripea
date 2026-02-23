package es.caib.ripea.service.intf.model;

import java.util.Date;
import java.util.Map;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.ripea.service.intf.dto.IntegracioDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "descripcio" },
        descriptionField = "descripcio",
        artifacts = {
            @ResourceArtifact(
                type = ResourceArtifactType.ACTION,
                code = IntegracioResource.ACTION_INTEGRACIONS_LIST
            )
        })
public class IntegracioResource extends BaseResource<Long> {

	public static final String ACTION_INTEGRACIONS_LIST = "INTEGRACIONS_LIST";
	
	private Long index;
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
		return this.index;
	}

	@Override
	public void setId(Long id) {
		this.index = id;
	}
}
