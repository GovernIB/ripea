package es.caib.ripea.service.intf.model;

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
@ResourceConfig(quickFilterFields = { "codi", "nom", "descripcio", "url" }, descriptionField = "nom")
public class URLInstruccioResource extends BaseAuditableResource<Long> {

	private static final long serialVersionUID = 1301398896789809114L;
	
	private String codi;
	private String nom;
	private String descripcio;
	private String url;
	
	protected ResourceReference<EntitatResource, Long> entitat;
}
