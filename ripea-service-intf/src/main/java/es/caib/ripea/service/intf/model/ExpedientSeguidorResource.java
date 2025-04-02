package es.caib.ripea.service.intf.model;
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ExpedientSeguidorId;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExpedientSeguidorResource implements Resource<ExpedientSeguidorId> {
	private static final long serialVersionUID = -6197265931691287149L;
	private ExpedientSeguidorId id;
	private ResourceReference<ExpedientResource, Long> expedient;
	private ResourceReference<UsuariResource, String> seguidor;
}