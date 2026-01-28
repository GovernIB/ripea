package es.caib.ripea.service.intf.model;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentTipusGenericEnumDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import es.caib.ripea.service.intf.dto.NtiOrigenEnumDto;
import es.caib.ripea.service.intf.resourcevalidation.MetaDocumentValid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@MetaDocumentValid
@ResourceConfig(
		quickFilterFields = { "codi", "nom" },
		descriptionField = "nom",
		artifacts = {
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = MetaDocumentResource.PERSPECTIVE_COUNT_METADADES),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaDocumentResource.ACTION_MARCAR_DEFECTE_CODE,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaDocumentResource.ACTION_DESMARCAR_DEFECTE_CODE,
                        requiresId = true),           
        })
public class MetaDocumentResource extends MetaNodeResource {

	public static final String PERSPECTIVE_COUNT_METADADES		= "COUNT_METADADES";
	public static final String ACTION_MARCAR_DEFECTE_CODE		= "MARCAR_DEFECTE";
	public static final String ACTION_DESMARCAR_DEFECTE_CODE	= "DESMARCAR_DEFECTE";

    @NotNull 
    private MultiplicitatEnumDto multiplicitat = MultiplicitatEnumDto.M_1;
	private boolean firmaPortafirmesActiva;
	@Size(max=64)
	private String portafirmesDocumentTipus;
	private List<ResourceReference<MetaDocumentFluxPortafibResource, Long>> fluxosFirma;
	@ResourceField(descriptionField = "nomAndNif")
	private List<ResourceReference<UsuariResource, String>> portafirmesResponsables;
    private MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus = MetaDocumentFirmaSequenciaTipusEnumDto.SERIE;
    @Size(max=64)
	private String portafirmesCustodiaTipus;
	private boolean firmaPassarelaActiva;
	@Size(max=64)
	private String firmaPassarelaCustodiaTipus;
	private String plantillaNom;
	private String plantillaContentType;
	private byte[] plantillaContingut;	
	private int ordre;
    @NotNull 
    private NtiOrigenEnumDto ntiOrigen;
    @NotNull 
    private DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
    @NotNull @ResourceField(enumType = true) 
    private String ntiTipoDocumental;
	private boolean firmaBiometricaActiva;
	private boolean biometricaLectura;
	private MetaDocumentTipusGenericEnumDto metaDocumentTipusGeneric;
    private MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus = MetaDocumentFirmaFluxTipusEnumDto.SIMPLE;
	private String codiPropi;
	private boolean pinbalActiu;
	private ResourceReference<MetaExpedientResource, Long> metaExpedient;
	private ResourceReference<PinbalServeiResource, Long> pinbalServei;
	protected String pinbalFinalitat;
	private boolean pinbalUtilitzarCifOrgan;
	private boolean perDefecte;

    @Transient private int numMetadades;
    @Transient private FileReference plantilla;
    
    @Transient private boolean importar = true;
    @Transient private List<MetaDadaResource> metaDadesImportacio;
    @Transient private Long importacioId;
    
    private static final long serialVersionUID = -4446427656169703518L;
}