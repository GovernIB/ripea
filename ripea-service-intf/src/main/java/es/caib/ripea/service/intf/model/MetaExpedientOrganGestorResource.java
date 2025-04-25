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
public class MetaExpedientOrganGestorResource extends BaseAuditableResource<Long> {
	private static final long serialVersionUID = 2631650800467571945L;
	private ResourceReference<MetaExpedientResource, Long> metaExpedient;
	private ResourceReference<OrganGestorResource, Long> organGestor;
}