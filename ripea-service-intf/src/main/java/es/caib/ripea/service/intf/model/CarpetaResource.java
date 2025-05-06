package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
        quickFilterFields = { "nom" },
        descriptionField = "nom"
)
public class CarpetaResource extends ContingutResource {
    @NotNull
	private ResourceReference<ExpedientResource, Long> expedientRelacionat;
}