package es.caib.ripea.service.intf.model;

import javax.validation.constraints.NotNull;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "id", "text" }, descriptionField = "text")
public class ExpedientComentariResource extends BaseAuditableResource<Long> {
    private static final long serialVersionUID = -5261297101962115056L;
	@NotNull
    private String text;
    @NotNull
    private ResourceReference<ExpedientResource, Long> expedient;
}