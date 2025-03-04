package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "codi", "rol", "descripcio" }, descriptionField = "codi")
public class MetaExpedientSequenciaResource extends BaseAuditableResource<Long> {

    @NotNull
    private int any;
    @NotNull
    private Long valor;
	
	@NotNull
	private ResourceReference<MetaExpedientResource, Long> metaExpedient;
}