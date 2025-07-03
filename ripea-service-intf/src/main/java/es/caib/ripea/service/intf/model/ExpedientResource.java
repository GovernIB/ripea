package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.dto.FileNameOption;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.dto.TipusImportEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		quickFilterFields = { "numero", "nom" },
        descriptionField = "nom",
		artifacts = {
				@ResourceConfigArtifact(
						type = ResourceArtifactType.REPORT,
						code = ExpedientResource.ACTION_MASSIVE_EXPORT_PDF_CODE,
						formClass = ExpedientResource.ExportarDocumentMassiu.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ContingutResource.PERSPECTIVE_PATH_CODE),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_NOTIFICACIONS_CADUCADES),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_DOCUMENTS_NO_MOGUTS),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_DOCUMENTS_OBLIGATORIS_TANCAR),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_AMB_PINBAL_CODE),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_INTERESSATS_CODE),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_COUNT),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_ESTAT_CODE),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_RELACIONAT_CODE),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_ARXIU_EXPEDIENT),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_FOLLOWERS),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.FILTER,
						code = ExpedientResource.FILTER_CODE,
						formClass = ExpedientResource.ExpedientFilterForm.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_MASSIVE_FOLLOW_CODE,
                        formClass = ExpedientResource.MassiveAction.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_MASSIVE_UNFOLLOW_CODE,
                        formClass = ExpedientResource.MassiveAction.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_MASSIVE_AGAFAR_CODE,
                        formClass = ExpedientResource.MassiveAction.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_MASSIVE_ALLIBERAR_CODE,
                        formClass = ExpedientResource.MassiveAction.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_MASSIVE_RETORNAR_CODE,
                        formClass = ExpedientResource.MassiveAction.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_MASSIVE_DELETE_CODE,
                        formClass = ExpedientResource.MassiveAction.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_TANCAR_CODE,
                        formClass = ExpedientResource.TancarExpedientFormAction.class,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_IMPORTAR_CODE,
                        formClass = ExpedientResource.ImportarExpedientFormAction.class,
                        requiresId = true),                
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_SYNC_ARXIU,
                        formClass = ExpedientResource.MassiveAction.class),                
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.REPORT,
                        code = ExpedientResource.ACTION_EXPORT_SELECTED_DOCS,
                        formClass = ExpedientResource.MassiveAction.class,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_IMPORT_DOCS,
                        formClass = ExpedientResource.ImportarDocumentsForm.class,
                        requiresId = true),                
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_MASSIVE_REOBRIR_CODE,
                        formClass = ExpedientResource.MassiveAction.class),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.REPORT,
						code = ExpedientResource.ACTION_MASSIVE_EXPORT_ODS_CODE,
						formClass = ExpedientResource.MassiveAction.class),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.REPORT,
						code = ExpedientResource.ACTION_MASSIVE_EXPORT_CSV_CODE,
						formClass = ExpedientResource.MassiveAction.class),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.REPORT,
						code = ExpedientResource.ACTION_MASSIVE_EXPORT_INDEX_ZIP,
						formClass = ExpedientResource.MassiveAction.class),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.REPORT,
						code = ExpedientResource.ACTION_MASSIVE_EXPORT_INDEX_PDF,
						formClass = ExpedientResource.MassiveAction.class),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.REPORT,
						code = ExpedientResource.ACTION_MASSIVE_EXPORT_INDEX_XLS,
						formClass = ExpedientResource.MassiveAction.class),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.REPORT,
						code = ExpedientResource.ACTION_MASSIVE_EXPORT_INDEX_ENI,
						formClass = ExpedientResource.MassiveAction.class),				
				@ResourceConfigArtifact(
						type = ResourceArtifactType.REPORT,
						code = ExpedientResource.ACTION_MASSIVE_EXPORT_ENI,
						formClass = ExpedientResource.MassiveAction.class),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.REPORT,
						code = ExpedientResource.ACTION_MASSIVE_EXPORT_INSIDE,
						formClass = ExpedientResource.MassiveAction.class),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.REPORT,
						code = ExpedientResource.ACTION_PLANTILLA_EXCEL_INTERESSATS,
                        requiresId = true),
		})
public class ExpedientResource extends NodeResource implements Serializable {

	private static final long serialVersionUID = 7440910672703796468L;
	
	public static final String ACTION_MASSIVE_EXPORT_PDF_CODE 	= "EXPORT_DOC";
	public static final String ACTION_MASSIVE_EXPORT_ODS_CODE 	= "EXPORT_EXCEL";
	public static final String ACTION_MASSIVE_EXPORT_CSV_CODE 	= "EXPORT_CSV";
	public static final String ACTION_MASSIVE_EXPORT_INDEX_ZIP 	= "EXPORT_INDEX_ZIP";
	public static final String ACTION_MASSIVE_EXPORT_INDEX_PDF 	= "EXPORT_INDEX_PDF";
	public static final String ACTION_MASSIVE_EXPORT_INDEX_XLS 	= "EXPORT_INDEX_XLS";
	public static final String ACTION_MASSIVE_EXPORT_INDEX_ENI 	= "EXPORT_INDEX_ENI";
	public static final String ACTION_MASSIVE_EXPORT_ENI 		= "EXPORT_ENI";
	public static final String ACTION_MASSIVE_EXPORT_INSIDE 	= "EXPORT_INSIDE";
	
	public static final String ACTION_MASSIVE_FOLLOW_CODE = "FOLLOW";
	public static final String ACTION_MASSIVE_UNFOLLOW_CODE = "UNFOLLOW";
	public static final String ACTION_MASSIVE_AGAFAR_CODE = "AGAFAR";
	public static final String ACTION_MASSIVE_ALLIBERAR_CODE = "ALLIBERAR";
	public static final String ACTION_MASSIVE_RETORNAR_CODE = "RETORNAR";
	public static final String ACTION_MASSIVE_DELETE_CODE = "ESBORRAR";
	public static final String ACTION_MASSIVE_REOBRIR_CODE = "REOBRIR";
	
	public static final String ACTION_TANCAR_CODE = "TANCAR";
	public static final String ACTION_IMPORTAR_CODE = "IMPORTAR";
	public static final String ACTION_EXPORT_SELECTED_DOCS = "EXPORT_SELECTED_DOCS";
	public static final String ACTION_SYNC_ARXIU = "SYNC_ARXIU";
	public static final String ACTION_IMPORT_DOCS = "IMPORT_DOCS";
	public static final String ACTION_PLANTILLA_EXCEL_INTERESSATS = "PLANTILLA_EXCEL_INTERESSATS";
	
	public static final String PERSPECTIVE_FOLLOWERS = "FOLLOWERS";
	public static final String PERSPECTIVE_ARXIU_EXPEDIENT = "ARXIU_EXPEDIENT";
	public static final String PERSPECTIVE_COUNT = "COUNT";
	public static final String PERSPECTIVE_INTERESSATS_CODE = "INTERESSATS_RESUM";
	public static final String PERSPECTIVE_ESTAT_CODE = "ESTAT";
	public static final String PERSPECTIVE_RELACIONAT_CODE = "RELACIONAT";
	public static final String PERSPECTIVE_NOTIFICACIONS_CADUCADES = "NOTIFICACIONS_CADUCADES";
	public static final String PERSPECTIVE_DOCUMENTS_NO_MOGUTS = "DOCUMENTS_NO_MOGUTS";
	public static final String PERSPECTIVE_DOCUMENTS_OBLIGATORIS_TANCAR = "DOCUMENTS_OBLIGATORIS_TANCAR";
	public static final String PERSPECTIVE_AMB_PINBAL_CODE = "AMB_PINBAL";

	public static final String FILTER_CODE = "EXPEDIENT_FILTER";

	@NotNull
	private ExpedientEstatEnumDto estat = ExpedientEstatEnumDto.OBERT;
//	@NotNull
	@Size(max = 46)
	private String ntiClasificacionSia;	
//	@NotNull
	private Date ntiFechaApertura;
	@NotNull
	private ResourceReference<OrganGestorResource, Long> organGestor;
	@NotNull
    @ResourceField(onChangeActive = true)
	private Integer any = Year.now().getValue();
	@NotNull
	private Long sequencia;
//	@NotNull
	@Size(max = 256)
	private String codi;
	@Size(max = 64)
	private String numero;

	@NotNull
	@ResourceField(onChangeActive = true)
	private ResourceReference<MetaExpedientResource, Long> metaExpedient;
	private ResourceReference<UsuariResource, String> agafatPer;
	private ResourceReference<ExpedientEstatResource, Long> estatAdditional;
	private ResourceReference<GrupResource, Long> grup;
	private ResourceReference<MetaExpedientOrganGestorResource, Long> metaexpedientOrganGestorPares;

    @Transient
    @ResourceField(onChangeActive = true)
    private FileReference exportPdf;
    
    @Transient
    @ResourceField(onChangeActive = true)
    private FileReference exportExcel;
    
    @Transient
    @ResourceField(onChangeActive = true)
    private FileReference exportPdfEni;
    
    @Transient
    @ResourceField(onChangeActive = true)
    private FileReference exportEni;
    
    @Transient
    @ResourceField(onChangeActive = true)
    private FileReference exportInside;
	
	// Tancat
	private Date tancatData;
	@Size(max = 1024)
	private String tancatMotiu;
	private Date tancatProgramat;

	// Esborrat
	private int esborrat;
	private Date esborratData;

	// Arxiu
	private Date arxiuDataActualitzacio;
	private Date arxiuIntentData;
	private int arxiuReintents;
	private boolean arxiuPropagat;

	// Registre
	@Size(max = 4000)
	private String registresImportats;

	// NTI
	@NotNull
	@Size(max = 5)
	private String ntiVersion = "1.0";
//	@NotNull
	@Size(max = 52)
	private String ntiIdentificador;
//	@NotNull
	@Size(max = 9)
	private String ntiOrgano;

	// Sistra
	@Size(max = 16)
	private String sistraBantelNum;
	private boolean sistraPublicat;
	@Size(max = 9)
	private String sistraUnitatAdministrativa;
	@Size(max = 100)
	private String sistraClau;

	// Prioritat
	private PrioritatEnumDto prioritat = PrioritatEnumDto.B_NORMAL;
	@Size(max = 1024)
	private String prioritatMotiu;

    @Transient private ExpedientEstatResource estatAdditionalInfo;
    @Transient private List<InteressatResource> interessats;
    @Transient private List<ResourceReference<UsuariResource, String>> seguidors;
    @Transient private boolean seguidor = false;
    @Transient private int numComentaris;
    @Transient private int numSeguidors;
    @Transient private int numContingut;
    @Transient private boolean hasEsborranys;
    @Transient private int numDades;
    @Transient private int numMetaDades;
    @Transient private int numInteressats;
    @Transient private int numRemeses;
    @Transient private int numPublicacions;
    @Transient private int numAnotacions;
    @Transient private int numTasques;
    @Transient private int numAlert;
    @Transient private boolean disableOrganGestor = false;
    
    public String getTipusStr() {
        return this.getMetaExpedient() != null ? this.getMetaExpedient().getDescription() + " - " + ntiClasificacionSia : null;
    }

    @Transient
    private ArxiuDetallDto arxiu;

    @Transient private Date dataDarrerEnviament;
    @Transient private boolean potModificar;

    private List<ResourceReference<ExpedientResource, Long>> relacionatsPer = new ArrayList<>();
    private List<ResourceReference<ExpedientResource, Long>> relacionatsAmb = new ArrayList<>();
    @Transient private List<ResourceReference<DocumentResource, Long>> documentObligatorisAlTancar = new ArrayList<>();

    @Transient private boolean conteDocuments;
    @Transient private boolean conteDocumentsFirmats;
    @Transient private boolean conteDocumentsEnProcessDeFirma;
    @Transient private boolean conteDocumentsDePortafirmesNoCustodiats;
    @Transient private boolean conteDocumentsDeAnotacionesNoMogutsASerieFinal;
    @Transient private boolean conteDocumentsPendentsReintentsArxiu;
    @Transient private boolean conteNotificacionsCaducades;
    @Transient private boolean potTancar;
    @Transient private boolean usuariActualWrite;
    @Transient private boolean errorLastEnviament;
    @Transient private boolean errorLastNotificacio;
    @Transient private boolean ambEnviamentsPendents;
    @Transient private boolean ambNotificacionsPendents;
    @Transient private boolean ambDocumentsPinbal;
    @Transient private boolean creacioCarpetesActiva;

    @Getter
	@Setter
    @NoArgsConstructor
    @FieldNameConstants
	public static class ExpedientFilterForm implements Serializable {
		private static final long serialVersionUID = 647178646210565833L;
		private String numero;
        private String nom;
//        private ExpedientEstatEnumDto estat = ExpedientEstatEnumDto.OBERT;
//        private ResourceReference<ExpedientEstatResource, Long> estatAdditional;
        @ResourceField(enumType = true)
        private String estat = "0";
        private String interessat;
        private ResourceReference<OrganGestorResource, Long> organGestor;
        private ResourceReference<MetaExpedientResource, Long> metaExpedient;
        private ResourceReference<DominiResource, Long> domini;
        @ResourceField(enumType = true)
        private String dominiValor;
        @ResourceField(onChangeActive = true)
        private LocalDateTime dataCreacioInici = LocalDateTime.now().withMonth(LocalDateTime.now().getMonth().getValue()-3);
        @ResourceField(onChangeActive = true)
        private LocalDateTime dataCreacioFinal;
        private String numeroRegistre;
        @ResourceField(namedQueries = {"BY_PERMISOS_USUARI"})
        private ResourceReference<GrupResource, Long> grup;
        @ResourceField(onChangeActive = true)
        private ResourceReference<UsuariResource, String> agafatPer;
        @ResourceField(onChangeActive = true)
        private Boolean agafat;
        private Boolean pendentFirmar;
        private Boolean seguit;
	}

    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    public static class ExportarDocumentMassiu extends MassiveAction {
		private boolean carpetes = true;
        private boolean versioImprimible = false;
        private FileNameOption nomFitxer = FileNameOption.ORIGINAL;
    }
    
    @Getter
    @Setter
    public static class TancarExpedientFormAction implements Serializable {
        @NotNull
        private String motiu;
        private List<Long> documentsPerFirmar;
    }
    
    @Getter
    @Setter
    public static class ImportarExpedientFormAction implements Serializable {
        @NotNull
        private ResourceReference<ExpedientResource, Long> expedientOrigen;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    public static class ImportarDocumentsForm implements Serializable {
    	@NotNull
    	private TipusImportEnumDto tipusImportacio = TipusImportEnumDto.NUMERO_REGISTRE;
    	private String codiEni;
    	private String numeroRegistre;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Madrid")
        private Date dataPresentacio;
    	private ResourceReference<CarpetaResource, Long> carpeta;
        private String novaCarpetaNom;
    }
    
    public boolean estaRelacionatAmb(Long id) {
    	if (this.getRelacionatsAmb()!=null) {
    		for (ResourceReference<ExpedientResource, Long> relacionatAmb: this.getRelacionatsAmb()) {
    			if (relacionatAmb.getId().equals(id)) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
}