package es.caib.ripea.service.intf.model;

import java.io.Serializable;

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
        quickFilterFields = { "codi", "rol", "descripcio" },
        descriptionField = "descripcio",
        artifacts = {
        		@ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = GrupResource.PERSPECTIVE_COUNT_PERMISOS),        		
        		@ResourceArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = GrupResource.FILTER_CODE,
                        formClass = GrupResource.FormFilter.class),
        }
)
public class GrupResource extends BaseAuditableResource<Long> {

    public static final String FILTER_CODE = "FILTER";
    public static final String PERSPECTIVE_COUNT_PERMISOS = "COUNT_PERMISOS";

	@NotNull
	@Size(max = 50)
	private String codi;
	@NotNull
	@Size(max = 512)
	private String descripcio;

	private ResourceReference<EntitatResource, Long> entitat;
	private ResourceReference<OrganGestorResource, Long> organGestor;

	@Transient private int numPermisos;
	
	@Transient private boolean importar = true;
	
    @Getter
    @Setter
    public static class FormFilter implements Serializable {
		private static final long serialVersionUID = -2372525041331550867L;
		private String codi;
        private String descripcio;
        private ResourceReference<OrganGestorResource, Long> organGestor;
    }
}