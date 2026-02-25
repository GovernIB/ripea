package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
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
    quickFilterFields = { "nom", "observacions" },
    descriptionField = "nom",
    artifacts = {
            @ResourceArtifact(
                    type = ResourceArtifactType.ACTION,
                    code = BackGroundTaskResource.ACTION_RESTART_TASK,
                    formClass = BackGroundTaskResource.MassiveRestartTaskForm.class),
        })        
public class BackGroundTaskResource extends BaseResource<String> {

	public static final String ACTION_RESTART_TASK	= "RESTART_TASK";
	
	private String nom;
	private String estat;
	private String tempsExecucio;
	private String dataInici;
	private String properaExecucio;
	private String observacions;
    
    @Getter
    @Setter
    public static class MassiveRestartTaskForm implements Serializable {
		@NotNull
        @NotEmpty
        private List<String> ids;
        private boolean massivo = false;
    }
}