package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

@Getter
@Setter
@NoArgsConstructor
public class NodeResource extends ContingutResource {
    @Transient
    private boolean valid;

	protected ResourceReference<MetaNodeResource, Long> metaNode;
}