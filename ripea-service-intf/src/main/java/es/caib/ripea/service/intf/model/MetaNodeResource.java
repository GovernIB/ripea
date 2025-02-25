package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.MetaNodeTipusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "codi", "nom" }, descriptionField = "nom")
public class MetaNodeResource extends BaseResource<Long> {

	protected String codi;
	protected String nom;
	protected String descripcio;
	protected MetaNodeTipusEnum tipus;
	protected boolean actiu = true;
	
	protected ResourceReference<EntitatResource, Long> entitat;
}