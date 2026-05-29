package es.caib.ripea.service.intf.model;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentTipusGenericEnumDto;
import es.caib.ripea.service.intf.dto.MetaExpedientRevisioEstatEnumDto;
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
				@ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = MetaDocumentResource.PERSPECTIVE_COUNT_METADADES),
				@ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = MetaDocumentResource.PERSPECTIVE_REVISIO_ESTAT),
				@ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = MetaDocumentResource.PERSPECTIVE_PORTAFIRMES_RESPONSABLES),
				@ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaDocumentResource.ACTION_ACTIVAR_CODE,
                        requiresId = true),
				@ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaDocumentResource.ACTION_DESACTIVAR_CODE,
                        requiresId = true),
				@ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaDocumentResource.ACTION_MARCAR_DEFECTE_CODE,
                        requiresId = true),
				@ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaDocumentResource.ACTION_DESMARCAR_DEFECTE_CODE,
                        requiresId = true),
				@ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaDocumentResource.ACTION_REORDENAR_CODE,
                        formClass = Integer.class,
                        requiresId = true),                   
        })
public class MetaDocumentResource extends MetaNodeResource {

	public static final String PERSPECTIVE_COUNT_METADADES			= "COUNT_METADADES";
    public static final String PERSPECTIVE_REVISIO_ESTAT        	= "REVISIO_ESTAT";
    public static final String PERSPECTIVE_PORTAFIRMES_RESPONSABLES = "PORTAFIRMES_RESPONSABLES";
	public static final String ACTION_ACTIVAR_CODE				= "ACTIVAR";
	public static final String ACTION_DESACTIVAR_CODE			= "DESACTIVAR";
	public static final String ACTION_MARCAR_DEFECTE_CODE		= "MARCAR_DEFECTE";
	public static final String ACTION_DESMARCAR_DEFECTE_CODE	= "DESMARCAR_DEFECTE";
	public static final String ACTION_REORDENAR_CODE			= "REORDENAR";

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
    @Transient private MetaExpedientRevisioEstatEnumDto metaExpedientRevisioEstat;
    
    private static final long serialVersionUID = -4446427656169703518L;
}