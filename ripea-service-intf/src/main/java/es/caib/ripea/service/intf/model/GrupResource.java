package es.caib.ripea.service.intf.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(quickFilterFields = { "codi", "rol", "descripcio" }, descriptionField = "codi")
public class GrupResource extends BaseAuditableResource<Long> {
	private static final long serialVersionUID = 4151677501429687311L;
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