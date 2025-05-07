package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
        quickFilterFields = {"text"},
        descriptionField = "text",
        artifacts = {
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = AlertaResource.ACTION_MASSIVE_LLEGIT_CODE,
                        formClass = ExpedientResource.MassiveAction.class),
        }
)
public class AlertaResource extends BaseAuditableResource<Long> {

    public static final String ACTION_MASSIVE_LLEGIT_CODE = "LLEGIT";

    private String text;
    private String error;
    private boolean llegida;

    private ResourceReference<ContingutResource, Long> contingut;

}