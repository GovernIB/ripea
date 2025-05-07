package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "text" }, descriptionField = "text")
public class AlertaResource extends BaseAuditableResource<Long> {

    private String text;
    private String error;
    private boolean llegida;

    private ResourceReference<ContingutResource, Long> contingut;

}