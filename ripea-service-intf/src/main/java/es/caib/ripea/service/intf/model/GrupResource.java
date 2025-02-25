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
@ResourceConfig(quickFilterFields = { "codi", "rol", "descripcio" }, descriptionField = "codi")
public class GrupResource extends BaseAuditableResource<Long> {

	@NotNull
	@Size(max = 50)
	private String codi;
	@NotNull
	@Size(max = 50)
	private String rol;
	@NotNull
	@Size(max = 512)
	private String descripcio;
	
	@NotNull
	private ResourceReference<EntitatResource, Long> entitat;
	private ResourceReference<OrganGestorResource, Long> organGestor;
}