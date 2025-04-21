package es.caib.ripea.service.intf.model;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
        quickFilterFields = { "codi", "nom" },
        descriptionField = "nom",
        artifacts = {
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = EntitatResource.PERSPECTIVE_IMAGE),
        }
)
public class EntitatResource extends BaseAuditableResource<Long> {

    public static final String PERSPECTIVE_IMAGE = "IMAGE";

	@NotNull
	@Size(max = 64)
	private String codi;
	@NotNull
	@Size(max = 256)
	private String nom;
	@Size(max = 1024)
	private String descripcio;
	@NotNull
	@Size(max = 9)
	private String cif;
	@NotNull
	@Size(max = 9)
	private String unitatArrel;
	private boolean activa;
    private byte[] logoImgBytes;
	@Size(max = 7)
	private String capsaleraColorFons;
	@Size(max = 7)
	private String capsaleraColorLletra;
	private Date dataSincronitzacio;
	private Date dataActualitzacio;
	private boolean permetreEnviamentPostal;
}