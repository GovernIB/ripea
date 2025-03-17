package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "id", "text" }, descriptionField = "text")
public class ExpedientTascaComentariResource extends BaseAuditableResource<Long> {

//    private Long id;
    @NotNull
    private String text;
    @NotNull
    private ResourceReference<ExpedientTascaResource, Long> expedientTasca;

}
