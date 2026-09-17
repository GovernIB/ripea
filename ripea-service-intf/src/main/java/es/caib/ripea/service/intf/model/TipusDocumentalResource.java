package es.caib.ripea.service.intf.model;

import javax.validation.constraints.NotNull;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		quickFilterFields = { "codi", "nomEspanyol", "nomCatala" },
		descriptionField = "nom",
		artifacts = {
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = TipusDocumentalResource.ACTION_ACTIVAR_CODE,
						requiresId = true),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = TipusDocumentalResource.ACTION_DESACTIVAR_CODE,
						requiresId = true),
		})
public class TipusDocumentalResource extends BaseAuditableResource<Long> {

	private static final long serialVersionUID = -6481622072359705767L;

	public static final String ACTION_ACTIVAR_CODE		= "ACTIVAR";
	public static final String ACTION_DESACTIVAR_CODE	= "DESACTIVAR";

	@NotNull
    private String codi;
    @NotNull
    private String nomEspanyol;
    @NotNull
    private String nomCatala;
    /** Només es modifica amb les accions ACTIVAR/DESACTIVAR: el formulari d'edició no el pot canviar. */
    private boolean actiu = true;

    protected ResourceReference<EntitatResource, Long> entitat;
}