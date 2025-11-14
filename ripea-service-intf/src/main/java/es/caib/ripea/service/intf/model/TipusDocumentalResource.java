package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(quickFilterFields = { "codi", "nomEspanyol", "nomCatala" }, descriptionField = "nom")
public class TipusDocumentalResource extends BaseAuditableResource<Long> {

    @NotNull
    private String codi;
//	private String codiEspecific;
    @NotNull
    private String nomEspanyol;
    @NotNull
    private String nomCatala;

    protected ResourceReference<EntitatResource, Long> entitat;
}