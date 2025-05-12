package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ArxiuDetallDto;
import es.caib.ripea.service.intf.dto.ArxiuEstatEnumDto;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.ripea.service.intf.dto.DocumentPublicacioTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentVersioDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.service.intf.dto.NtiOrigenEnumDto;
import es.caib.ripea.service.intf.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.service.intf.dto.ServeiTipusEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "nom", "fitxerNom" },
        descriptionField = "nom",
        artifacts = {
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = DocumentResource.PERSPECTIVE_COUNT_CODE),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = DocumentResource.PERSPECTIVE_VERSIONS_CODE),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = DocumentResource.PERSPECTIVE_ARXIU_DOCUMENT_CODE),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = DocumentResource.PERSPECTIVE_PATH_CODE),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_ENVIAR_VIA_EMAIL_CODE,
                        formClass = DocumentResource.EnviarViaEmailFormAction.class,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_MOURE_CODE,
                        formClass = DocumentResource.MoureFormAction.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_PUBLICAR_CODE,
                        formClass = DocumentResource.PublicarFormAction.class,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_NOTIFICAR_CODE,
                        formClass = DocumentResource.NotificarFormAction.class,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_ENVIAR_PORTAFIRMES_CODE,
                        formClass = DocumentResource.EnviarPortafirmesFormAction.class,
                        requiresId = true),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.ACTION,
						code = DocumentResource.ACTION_MASSIVE_NOTIFICAR_ZIP_CODE,
						formClass = DocumentResource.NotificarDocumentsZipFormAction.class),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.ACTION,
						code = DocumentResource.ACTION_MASSIVE_CANVI_TIPUS_CODE,
						formClass = DocumentResource.UpdateTipusDocumentFormAction.class),				
        })
public class DocumentResource extends NodeResource {

	public static final String PERSPECTIVE_COUNT_CODE = "COUNT";
    public static final String PERSPECTIVE_VERSIONS_CODE = "VERSIONS";
    public static final String PERSPECTIVE_ARXIU_DOCUMENT_CODE = "ARXIU_DOCUMENT";
    public static final String PERSPECTIVE_PATH_CODE = "PATH";
    public static final String ACTION_ENVIAR_VIA_EMAIL_CODE = "ENVIAR_VIA_EMAIL";
    public static final String ACTION_ENVIAR_PORTAFIRMES_CODE = "ENVIAR_PORTAFIRMES";
    public static final String ACTION_MOURE_CODE = "MOURE";
    public static final String ACTION_PUBLICAR_CODE = "PUBLICAR";
    public static final String ACTION_NOTIFICAR_CODE = "NOTIFICAR";
    public static final String ACTION_MASSIVE_NOTIFICAR_ZIP_CODE = "MASSIVE_NOTIFICAR_ZIP";
    public static final String ACTION_MASSIVE_CANVI_TIPUS_CODE = "MASSIVE_CANVI_TIPUS";

	@NotNull
	private DocumentTipusEnumDto documentTipus = DocumentTipusEnumDto.DIGITAL;
	private DocumentEstatEnumDto estat;
	@Size(max = 255)
	private String ubicacio;
//	@NotNull
	private Date data;
	@NotNull
	private Date dataCaptura = new Date();
	private Date custodiaData;
	@Size(max = 256)
	private String custodiaId;
	@Size(max = 256)
	private String custodiaCsv;
	@Size(max = 256)
	private String fitxerNom;
	@Size(max = 256)
	private String fitxerContentType;
	private Long fitxerTamany;
	private byte[] fitxerContingut;
	
	private boolean ambFirma;
	private String firmaNom;
	private String firmaContentType;
	private byte[] firmaContingut;
	
    @NotNull
    @Transient
    @ResourceField(onChangeActive = true)
    private FileReference adjunt;
    @Transient
    @ResourceField(onChangeActive = true)
    private FileReference firmaAdjunt;
    @Transient
    @ResourceField(onChangeActive = true)
    private FileReference original;
    @Transient
    @ResourceField(onChangeActive = true)
    private FileReference imprimible;    

	@Size(max = 32)
	private String versioDarrera;
	@NotNull
	private int versioCount = 0;
	@NotNull
	@Size(max = 5)
	private String ntiVersion = "1.0";
	@Size(max = 48)
	private String ntiIdentificador;
//	@NotNull
	@Size(max = 9)
	private String ntiOrgano;
	@NotNull
	private NtiOrigenEnumDto ntiOrigen;
	@NotNull
	private DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
//	@NotNull
	@Size(max = 4)
	private String ntiTipoDocumental;
	@Size(max = 48)
	private String ntiIdDocumentoOrigen;
	private DocumentNtiTipoFirmaEnumDto ntiTipoFirma;
	@Size(max = 256)
	private String ntiCsv;
	@Size(max = 512)
	private String ntiCsvRegulacion;
	@Size(max = 512)
	protected String descripcio;
	
	//Tipus de document firmat:
		//Document firmat putjat manualment
		//Document firmat des dels navegador
		//Document firmat que es rep des del portafirmes callback
		//Document que vene d'una anotació de registre
		//Document generat de les resposta de PINBAL
	
	// document signed in portafirmes that arrived in callback and was not saved in arxiu 
	@Size(max = 256)
	private String gesDocFirmatId;
	@Size(max = 512)
	private String nomFitxerFirmat;
	//document uploaded manually in ripea that was not saved in arxiu
	// document sense firma o amb firma adjunta
	@Size(max = 256)
	private String gesDocAdjuntId;
	// firma separada
	@Size(max = 256)
	private String gesDocAdjuntFirmaId;
	//ID del contingut original guardat al sistema de fitxers
	@Size(max = 36)
	private String gesDocOriginalId;
	// firma separada of document saved as esborrany in arxiu
	@Size(max = 36)
	private String arxiuUuidFirma;
	@Size(max = 64)
	private String pinbalIdpeticion;
	private boolean validacioFirmaCorrecte;
	@Size(max = 1000)
	private String validacioFirmaErrorMsg;
	private ArxiuEstatEnumDto annexArxiuEstat;
	private ArxiuEstatEnumDto arxiuEstat;
    @NotNull
	private DocumentFirmaTipusEnumDto documentFirmaTipus;
	private ResourceReference<ExpedientEstatResource, Long> expedientEstatAdditional;

    @Transient    private List<ParentPath> parentPath;
    @Transient
    public List<String> treePath;
    @NotNull
    @Transient
    @ResourceField(onChangeActive = true)
    public ResourceReference<MetaDocumentResource, Long> metaDocument;

    @Transient
    @ResourceField(onChangeActive = true)
    public Boolean hasFirma;

    @Transient
    private ArxiuDetallDto arxiu;
    @Transient
    private List<DocumentVersioDto> versions;
    @Transient
    private int NumMetaDades;
    @Transient
    private boolean documentDeAnotacio;
    
    @Getter
    @Setter
    public static class ParentPath {
        private Long id;
        private String nom;
        private String createdBy;
        private LocalDateTime createdDate;
        private ContingutTipusEnumDto tipus;

        public List<String> treePath;

        public ParentPath(Long id, String nom, String createdBy, LocalDateTime createdDate, ContingutTipusEnumDto tipus) {
            this.id = id;
            this.nom = nom;
            this.createdBy = createdBy;
            this.createdDate = createdDate;
            this.tipus = tipus;
        }
    }

    public String getFitxerExtension() {
        if (fitxerNom != null) {
            return fitxerNom.substring(
                    fitxerNom.lastIndexOf('.') + 1,
                    fitxerNom.length());
        } else {
            return "";
        }
    }

    @Getter
    @Setter
    public static class EnviarViaEmailFormAction implements Serializable {
        private String email;
        private List<ResourceReference<UsuariResource, String>> responsables = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class MoureFormAction extends ExpedientResource.MassiveAction {
        private String contingut;
        @NotNull
        private ResourceReference<ExpedientResource, Long> expedient;
        private ResourceReference<CarpetaResource, Long> carpeta;
        private String motiu;
        @NotNull
        private Action action = Action.MOURE;

        public enum Action {
            MOURE,
            COPIAR
        }
    }

    @Getter
    @Setter
    public static class PublicarFormAction implements Serializable {
        @NotNull
        private DocumentPublicacioTipusEnumDto tipus = DocumentPublicacioTipusEnumDto.BOIB;
        @NotNull
        private DocumentEnviamentEstatEnumDto estat = DocumentEnviamentEstatEnumDto.PENDENT;
        @NotNull
        private String assumpte;
        private Date dataPublicacio;
        @NotNull
        private Date enviatData;
        private String observacions;
    }

    @Getter
    @Setter
    @FieldNameConstants
    public static class NotificarFormAction implements Serializable {

        private DocumentNotificacioTipusEnumDto tipus;
        @NotNull
        private DocumentNotificacioEstatEnumDto estat = DocumentNotificacioEstatEnumDto.PENDENT;
        @NotEmpty
        private List<ResourceReference<InteressatResource, Long>> interessats = new ArrayList<>();
        @NotNull
        private String concepte;
        @NotNull
        private ServeiTipusEnumDto serveiTipus = ServeiTipusEnumDto.NORMAL;
//        @Field(type = Field.TYPE_TEXTAREA)
        private String descripcio;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Madrid")
        private Date dataProgramada;
        @NotNull
        @ResourceField(onChangeActive = true)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Madrid")
        private Date dataCaducitat;
        @NotNull
        @ResourceField(onChangeActive = true)
        private Integer duracio;
        private Integer retard;
        private Boolean entregaPostal;

        @Transient
        private boolean permetreEnviamentPostal;

        @Transient
        private ResourceReference<ExpedientResource, Long> expedient;
    }

    @Getter
    @Setter
    public static class EnviarPortafirmesFormAction implements Serializable {
        @NotNull
        private String motiu;
        @NotNull
        private PortafirmesPrioritatEnumDto prioritat = PortafirmesPrioritatEnumDto.NORMAL;
    	private Date dataInici;
    	private boolean enviarCorreu;
        private List<ResourceReference<DocumentResource, Long>> annexos;
        
        //Firma parcial
        private boolean firmaParcial;
        private boolean avisFirmaParcial;
        @Transient
        private boolean mostrarFirmaParcial;
        @Transient
        private boolean mostrarAvisFirmaParcial;

        // SIMPLE
        private List<ResourceReference<UsuariResource, String>> responsables = new ArrayList<>();
        private MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus;

        //FLUX
        @Transient
    	private String portafirmesEnviarFluxId;
        @Transient
    	private String portafirmesFluxNom;
        @Transient
    	private String portafirmesFluxDescripcio;
        @Transient
    	private MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    public static class UpdateTipusDocumentFormAction extends MassiveAction {
    	@NotNull
    	private ResourceReference<MetaDocumentResource, Long> metaDocument;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    public static class NotificarDocumentsZipFormAction extends UpdateTipusDocumentFormAction {
    	@NotNull
    	private NtiOrigenEnumDto ntiOrigen;
    	@NotNull
    	private DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
    }
}