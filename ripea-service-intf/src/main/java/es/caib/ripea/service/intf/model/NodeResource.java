package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NodeResource extends ContingutResource {
	protected ResourceReference<MetaNodeResource, Long> metaNode;
}