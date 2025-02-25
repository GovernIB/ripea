package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "nom" }, descriptionField = "nom")
public class CarpetaResource extends ContingutResource {
	private ResourceReference<ExpedientResource, Long> expedientRelacionat;
}