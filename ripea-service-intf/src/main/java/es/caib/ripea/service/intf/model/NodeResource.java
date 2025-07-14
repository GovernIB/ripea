package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
public class NodeResource extends ContingutResource {
    
	@Transient private boolean valid;
    @Transient private List<ValidacioErrorResource> errors;
    private ResourceReference<MetaNodeResource, Long> metaNode;
	private ResourceReference<DadaResource, Long> dades;
	
    @Getter
    @Setter
    public static class MassiveAction implements Serializable {
		private static final long serialVersionUID = -188470015982418846L;
		@NotNull
        @NotEmpty
        private List<Long> ids;
        private boolean massivo = false;
    }
}