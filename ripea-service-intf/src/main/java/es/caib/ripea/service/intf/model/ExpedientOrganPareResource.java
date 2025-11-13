package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@FieldNameConstants
@NoArgsConstructor
public class ExpedientOrganPareResource extends BaseAuditableResource<Long> {
	private static final long serialVersionUID = -3553083883424497644L;
	private ResourceReference<ExpedientResource, Long> expedient;
	private ResourceReference<MetaExpedientOrganGestorResource, Long> metaExpedientOrganGestor;
}
