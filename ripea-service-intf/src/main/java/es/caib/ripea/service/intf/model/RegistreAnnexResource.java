package es.caib.ripea.service.intf.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ArxiuEstatEnumDto;
import es.caib.ripea.service.intf.dto.ArxiuFirmaDto;
import es.caib.ripea.service.intf.dto.NtiTipoDocumentoEnumDto;
import es.caib.ripea.service.intf.dto.RegistreAnnexEstatEnumDto;
import es.caib.ripea.service.intf.dto.SicresTipoDocumentoEnumDto;
import es.caib.ripea.service.intf.dto.SicresValidezDocumentoEnumDto;
import es.caib.ripea.service.intf.model.NodeResource.MassiveAction;
import es.caib.ripea.service.intf.registre.RegistreAnnexFirmaTipusEnum;
import es.caib.ripea.service.intf.registre.RegistreAnnexNtiEstadoElaboracionEnum;
import es.caib.ripea.service.intf.registre.RegistreAnnexNtiOrigenEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		quickFilterFields = { "nom" },
		descriptionField = "observacions",
		artifacts = {
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = RegistreAnnexResource.PERSPECTIVE_FIRMES),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.REPORT,
                        code = RegistreAnnexResource.REPORT_DOWNLOAD_ANNEX,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = RegistreAnnexResource.ACTION_REINTENTAR_CODE,
                        formClass = MassiveAction.class,
                        requiresId = true),                
		}
)
public class RegistreAnnexResource extends BaseAuditableResource<Long> {

	private static final long serialVersionUID = 1245578504295972456L;
	
	public static final String REPORT_DOWNLOAD_ANNEX	= "DOWNLOAD_ANNEX";
	public static final String PERSPECTIVE_FIRMES 		= "FIRMES";
	public static final String ACTION_REINTENTAR_CODE	= "REINTENTAR";
	
    private String firmaPerfil;
    private long firmaTamany;
    private RegistreAnnexFirmaTipusEnum firmaTipus;
    private String firmaNom;
    private String nom;

    private Date ntiFechaCaptura;
    private RegistreAnnexNtiOrigenEnum ntiOrigen;
    private NtiTipoDocumentoEnumDto ntiTipoDocumental;
    private RegistreAnnexNtiEstadoElaboracionEnum ntiEstadoElaboracion;
    private String observacions;

    private SicresTipoDocumentoEnumDto sicresTipoDocumento;
    private SicresValidezDocumentoEnumDto sicresValidezDocumento;

    private long tamany;
    private String tipusMime;
    private String titol;
    private String uuid;

    private RegistreAnnexEstatEnumDto estat;
    private String error;

    private boolean validacioFirmaCorrecte;
    private String validacioFirmaErrorMsg;
    private ArxiuEstatEnumDto annexArxiuEstat;

    private ResourceReference<RegistreResource, Long> registre;
    private ResourceReference<DocumentResource, Long> document;
    
    @Transient private List<ArxiuFirmaDto> firmes;

    public String getFitxerExtension() {
        if (nom != null) {
            return nom.substring(
                    nom.lastIndexOf('.') + 1,
                    nom.length());
        } else {
            return "";
        }
    }
}
