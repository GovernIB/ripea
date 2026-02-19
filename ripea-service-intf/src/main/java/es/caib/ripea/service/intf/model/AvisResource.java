package es.caib.ripea.service.intf.model;

import java.util.Date;

import javax.validation.constraints.NotNull;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.AvisNivellEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
        quickFilterFields = { "assumpte", "missatge" },
        descriptionField = "assumpte",
        artifacts = {
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = AvisResource.ACTION_MASSIVE_ACTIVE_CODE,
                        formClass = AvisResource.MassiveActiveFormAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = AvisResource.ACTION_MASSIVE_DELETE_CODE,
                        formClass = NodeResource.MassiveAction.class),
        }
)
public class AvisResource extends BaseAuditableResource<Long> {

    public static final String ACTION_MASSIVE_ACTIVE_CODE	= "MASSIVE_ACTIVE";
    public static final String ACTION_MASSIVE_DELETE_CODE   = "MASSIVE_DELETE";

    @NotNull private String assumpte;
    @NotNull private String missatge;
    @NotNull private Date dataInici = new Date();
    @NotNull private Date dataFinal;
	private Boolean actiu = true;
    @NotNull private AvisNivellEnumDto avisNivell = AvisNivellEnumDto.INFO;
	private Boolean avisAdministrador;
	protected ResourceReference<EntitatResource, Long> entitat;
	
	private static final long serialVersionUID = 1624417428355961779L;

    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    public static class MassiveActiveFormAction extends NodeResource.MassiveAction {
        @NotNull
        private Boolean active;
    }
}