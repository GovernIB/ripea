package es.caib.ripea.service.intf.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Transient;

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
		quickFilterFields = { "codi", "nom" },
		descriptionField = "nom",
		artifacts = {
				@ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaExpedientEstatResource.ACTION_REORDENAR_CODE,
                        formClass = Integer.class,
                        requiresId = true),   
		})
public class MetaExpedientEstatResource extends BaseAuditableResource<Long> {

	public static final String ACTION_REORDENAR_CODE			= "REORDENAR";
	
	@NotNull
	@Size(max = 256)
	private String codi;
	@NotNull
	@Size(max = 256)
	private String nom;
	@NotNull
	private int ordre;
	@Size(max = 256)
	private String color;
	private boolean inicial;
    private ResourceReference<UsuariResource, String> responsable;
	
	private ResourceReference<MetaExpedientResource, Long> metaExpedient;
	
	@Transient private boolean importar = true;
	@Transient private Long importacioId;
}