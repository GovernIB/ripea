package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NodeResource extends ContingutResource {
    @Transient
    private boolean valid;
    @Transient
    private List<ValidacioErrorResource> errors;

	protected ResourceReference<MetaNodeResource, Long> metaNode;
}