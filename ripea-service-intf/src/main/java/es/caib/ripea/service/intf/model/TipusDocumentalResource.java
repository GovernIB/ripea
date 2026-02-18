package es.caib.ripea.service.intf.model;

import javax.validation.constraints.NotNull;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(quickFilterFields = { "codi", "nomEspanyol", "nomCatala" }, descriptionField = "nom")
public class TipusDocumentalResource extends BaseAuditableResource<Long> {

	private static final long serialVersionUID = -6481622072359705767L;
	
	@NotNull
    private String codi;
    @NotNull
    private String nomEspanyol;
    @NotNull
    private String nomCatala;

    protected ResourceReference<EntitatResource, Long> entitat;
}