package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import es.caib.ripea.service.intf.resourcevalidation.URLInstruccioValida;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(quickFilterFields = { "codi", "nom", "descripcio", "url" }, descriptionField = "nom")
public class URLInstruccioResource extends BaseAuditableResource<Long> {

	private static final long serialVersionUID = 1301398896789809114L;
	
	@NotEmpty private String codi;
	@NotEmpty private String nom;
	private String descripcio;
	@NotEmpty @URLInstruccioValida private String url;
	
	protected ResourceReference<EntitatResource, Long> entitat;
}
