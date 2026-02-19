package es.caib.ripea.service.intf.model;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
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
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = EntitatResource.PERSPECTIVE_PERMISOS_CODE),
        }
)
public class EntitatResource extends BaseAuditableResource<Long> {

	public static final String PERSPECTIVE_PERMISOS_CODE	= "PERMISOS";
	
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
	private Date dataSincronitzacio;
	private Date dataActualitzacio;
	private boolean permetreEnviamentPostal;

    //Dades de configuracio modo light
    private byte[] logoImgBytes;
    private byte[] faviconImgBytes;
    private byte[] menuImgBytes;
    private String capsaleraColorFons;
    private String capsaleraColorLletra;

    //Dades de configuracio modo dark
    private byte[] blackLogoImgBytes;
    private byte[] blackFaviconImgBytes;
    private byte[] blackMenuImgBytes;
    private String blackCapsaleraColorFons;
    private String blackCapsaleraColorLletra;
    
    @Transient private int numPermisos;
    
    @Transient @ResourceField(onChangeActive = true) private FileReference logoImgFile;
    @Transient @ResourceField(onChangeActive = true) private FileReference faviconImgFile;
    @Transient @ResourceField(onChangeActive = true) private FileReference menuImgFile;
    
    @Transient @ResourceField(onChangeActive = true) private FileReference blackLogoImgFile;
    @Transient @ResourceField(onChangeActive = true) private FileReference blackFaviconImgFile;
    @Transient @ResourceField(onChangeActive = true) private FileReference blackMenuImgFile;
    
    private static final long serialVersionUID = 5467286889478459953L;
}