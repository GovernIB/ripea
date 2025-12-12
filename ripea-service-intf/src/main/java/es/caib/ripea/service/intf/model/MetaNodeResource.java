package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.MetaNodeTipusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(quickFilterFields = { "codi", "nom" }, descriptionField = "nom")
public class MetaNodeResource extends BaseAuditableResource<Long> {
	private static final long serialVersionUID = 5537648887756445019L;
	protected String codi;
	protected String nom;
	protected String descripcio;
	protected MetaNodeTipusEnum tipus;
	protected boolean actiu = true;	
	protected ResourceReference<EntitatResource, Long> entitat;
}