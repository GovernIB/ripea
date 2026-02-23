package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.BaseResource;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.ripea.service.intf.dto.IntegracioDto;
import es.caib.ripea.service.intf.resourcevalidation.ImportarDocumentsZipValid;
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
                code = IntegracioResource.ACTION_INTEGRACIONS_LIST),
            @ResourceArtifact(
                    type = ResourceArtifactType.ACTION,
                    code = IntegracioResource.ACTION_DIAGNOSTIC_PLUGIN,
                    formClass = IntegracioResource.DiagnosticResetForm.class),
            @ResourceArtifact(
                    type = ResourceArtifactType.ACTION,
                    code = IntegracioResource.ACTION_REINICIAR_PLUGIN,
                    formClass = IntegracioResource.DiagnosticResetForm.class),
        })
public class IntegracioResource extends BaseResource<Long> {

	public static final String ACTION_INTEGRACIONS_LIST	= "INTEGRACIONS_LIST";
	public static final String ACTION_DIAGNOSTIC_PLUGIN	= "DIAGNOSTIC_PLUGIN";
	public static final String ACTION_REINICIAR_PLUGIN	= "REINICIAR_PLUGIN";
	
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
	
    @Getter
    @Setter
    public static class DiagnosticResetForm implements Serializable {
        private String codiIntegracio;
        @ResourceField(onChangeActive = true)
        private ResourceReference<EntitatResource, Long> entitat;
        private ResourceReference<OrganGestorResource, Long> organ;
    }
}