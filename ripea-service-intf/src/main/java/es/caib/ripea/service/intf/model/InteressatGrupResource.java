package es.caib.ripea.service.intf.model;

import java.util.List;

import javax.validation.constraints.NotEmpty;
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

/**
 * Classe Resource que representa un grup d'interessats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "nom" },
        descriptionField = "nom",
        artifacts = {
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = InteressatGrupResource.PERSPECTIVE_INTERESSATS_CODE,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = InteressatGrupResource.ACTION_DELETE_GRUP,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = InteressatGrupResource.ACTION_CREATE_INTERESSAT,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = InteressatGrupResource.ACTION_IMPORT_INTERESSAT,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = InteressatGrupResource.ACTION_DELETE_INTERESSAT,
                        requiresId = true),
        }
)
public class InteressatGrupResource extends BaseAuditableResource<Long> {

	private static final long serialVersionUID = -7610080463642542721L;
	
	public static final String PERSPECTIVE_INTERESSATS_CODE = "INTERESSATS";
	public static final String ACTION_CREATE_GRUP = "GREATE_GRUP";
	public static final String ACTION_DELETE_GRUP = "DELETE_GRUP";
    public static final String ACTION_CREATE_INTERESSAT  = "CREATE_INTERESSAT";
    public static final String ACTION_IMPORT_INTERESSAT  = "IMPORT_INTERESSAT";
    public static final String ACTION_DELETE_INTERESSAT  = "DELETE_INTERESSAT";
    
	@Size(max = 255)
	@NotEmpty
	protected String nom;    
	
	@Size(max = 1024)
	protected String descripcio;
	
	protected ResourceReference<ExpedientResource, Long> expedient;
	
	protected List<ResourceReference<InteressatResource, Long>> interessats;
	
	@Transient
	protected List<InteressatResource> interessatsDetallats;
	
}