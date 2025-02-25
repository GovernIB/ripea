package es.caib.ripea.service.intf.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "codi", "nom" }, descriptionField = "nom")
public class ExpedientEstatResource extends BaseAuditableResource<Long> {

	@NotNull
	@Size(max = 256)
	private String codi;
	@NotNull
	@Size(max = 256)
	private String nom;
	@NotNull
	private int ordre;
	@Size(max = 256)
	private String color;
	private boolean inicial;
	private String responsableCodi;
	
	private ResourceReference<MetaExpedientResource, Long> metaExpedient;
}