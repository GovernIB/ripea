package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ArxiuDetallDto;
import es.caib.ripea.service.intf.dto.DocumentAmbTipusDto;
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.dto.FileNameOption;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.dto.TipusImportEnumDto;
import es.caib.ripea.service.intf.registre.RegistreInteressat;
import es.caib.ripea.service.intf.resourcevalidation.ExpedientValid;
import es.caib.ripea.service.intf.resourcevalidation.ImportarDocumentValid;
import es.caib.ripea.service.intf.resourcevalidation.ImportarDocumentsZipValid;
import es.caib.ripea.service.intf.resourcevalidation.MassiveImportDocValid;
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
        		@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_BASE_CODE),
        		@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_AVISOS_CODE),        		
				@ResourceArtifact(
						type = ResourceArtifactType.REPORT,
						code = ExpedientResource.REPORT_MASSIVE_EXPORT_PDF_CODE,
						formClass = ExpedientResource.ExportarDocumentMassiu.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ContingutResource.PERSPECTIVE_PATH_CODE),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_NOTIFICACIONS_CADUCADES),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_DOCUMENTS_NO_MOGUTS),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_DOCUMENTS_OBLIGATORIS_TANCAR),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_AMB_PINBAL_CODE),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_PERMIS_CONTINGUT),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_AUDIT_CODE),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_INTERESSATS_CODE),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_COUNT),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_COUNT_RESUM),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_ESTAT_CODE),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_META_EXPEDIENT_CODE),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_RELACIONAT_CODE),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_ARXIU_EXPEDIENT),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_FOLLOWERS),
				@ResourceArtifact(
						type = ResourceArtifactType.FILTER,
						code = ExpedientResource.FILTER_CODE,
						formClass = ExpedientResource.ExpedientFilterForm.class),
				@ResourceArtifact(
						type = ResourceArtifactType.FILTER,
						code = ExpedientResource.MASSIVE_CANVI_ESTAT_FILTER_CODE,
						formClass = ExpedientResource.MassiveCanviEstatFilter.class),
				@ResourceArtifact(
						type = ResourceArtifactType.FILTER,
						code = ExpedientResource.MASSIVE_CUSTODIAR_FILTER_CODE,
						formClass = ExpedientResource.MassiveCustodiarFilter.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_MASSIVE_FOLLOW_CODE,
                        formClass = ExpedientResource.MassiveAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_MASSIVE_UNFOLLOW_CODE,
                        formClass = ExpedientResource.MassiveAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_MASSIVE_AGAFAR_CODE,
                        formClass = ExpedientResource.MassiveAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_MASSIVE_ALLIBERAR_CODE,
                        formClass = ExpedientResource.MassiveAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_MASSIVE_RETORNAR_CODE,
                        formClass = ExpedientResource.MassiveAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_MASSIVE_DELETE_CODE,
                        formClass = ExpedientResource.MassiveAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_TANCAR_CODE,
                        formClass = ExpedientResource.TancarExpedientFormAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_CANVI_PRIORITAT_CODE,
                        formClass = ExpedientResource.CanviPrioritatExpedientFormAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_CANVI_ESTAT_CODE,
                        formClass = ExpedientResource.CanviEstatExpedientFormAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_IMPORTAR_CODE,
                        formClass = ExpedientResource.ImportarExpedientFormAction.class,
                        requiresId = true),                
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_SYNC_ARXIU,
                        formClass = ExpedientResource.MassiveAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_GUARDAR_ARXIU,
                        formClass = ExpedientResource.MassiveAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_MOURE_TOT_CODE,
                        formClass = ExpedientResource.MoureTotFormAction.class,
                        requiresId = true),  
                @ResourceArtifact(
                        type = ResourceArtifactType.REPORT,
                        code = ExpedientResource.REPORT_EXPORT_SELECTED_DOCS,
                        formClass = ExpedientResource.MassiveAction.class,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_IMPORT_DOCS,
                        formClass = ExpedientResource.ImportarDocumentsForm.class,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_IMPORT_INTE,
                        formClass = ExpedientResource.ImportarInteressatsForm.class,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_IMPORT_DOCS_ZIP,
                        formClass = ExpedientResource.ImportarDocumentsZipForm.class,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_GET_PROGRES_SGD,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_CANCEL_IMPORT_SGD,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_GET_PROGRES_ZIP,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_CANCEL_IMPORT_ZIP,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_MASSIVE_REOBRIR_CODE,
                        formClass = ExpedientResource.MassiveAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_MASSIVE_RELACIONAR_CODE,
                        formClass = ExpedientResource.RelacionarAction.class,
                        requiresId = true),
				@ResourceArtifact(
						type = ResourceArtifactType.REPORT,
						code = ExpedientResource.REPORT_MASSIVE_EXPORT_GENERIC,
						formClass = ExpedientResource.ExportGenericForm.class),
				@ResourceArtifact(
						type = ResourceArtifactType.REPORT,
						code = ExpedientResource.REPORT_MASSIVE_EXPORT_INDEX_ENI,
						formClass = ExpedientResource.MassiveAction.class),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = ExpedientResource.ACTION_MASSIVE_IMPORT_DOCS,
						formClass = ExpedientResource.MassiveImportDocsAction.class),
				@ResourceArtifact(
						type = ResourceArtifactType.REPORT,
						code = ExpedientResource.REPORT_PLANTILLA_EXCEL_INTERESSATS,
                        requiresId = true),
				@ResourceArtifact(
						type = ResourceArtifactType.REPORT,
						code = ExpedientResource.REPORT_PLANTILLA_DADES_CSV,
                        requiresId = true),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_EN_PROCES_CANVI_ESTAT_CODE),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_EN_PROCES_TANCAMENT_CODE),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientResource.PERSPECTIVE_EN_PROCES_CUSTODIAR_CODE),
		})
@ExpedientValid
public class ExpedientResource extends NodeResource implements Serializable {

	private static final long serialVersionUID = 7440910672703796468L;
	
	public static final String REPORT_MASSIVE_EXPORT_PDF_CODE  = "EXPORT_DOC";
	public static final String REPORT_MASSIVE_EXPORT_GENERIC   = "EXPORT_GENERIC";
	public static final String REPORT_MASSIVE_EXPORT_INDEX_ENI = "EXPORT_INDEX_ENI";
	
	public static final String ACTION_MASSIVE_FOLLOW_CODE = "FOLLOW";
	public static final String ACTION_MASSIVE_UNFOLLOW_CODE = "UNFOLLOW";
	public static final String ACTION_MASSIVE_AGAFAR_CODE = "AGAFAR";
	public static final String ACTION_MASSIVE_ALLIBERAR_CODE = "ALLIBERAR";
	public static final String ACTION_MASSIVE_RETORNAR_CODE = "RETORNAR";
	public static final String ACTION_MASSIVE_DELETE_CODE = "ESBORRAR";
	public static final String ACTION_MASSIVE_REOBRIR_CODE = "REOBRIR";
	public static final String ACTION_MASSIVE_RELACIONAR_CODE = "RELACIONAR";
	public static final String ACTION_MASSIVE_IMPORT_DOCS = "IMPORT_DOCS_MASS";
	
	public static final String ACTION_TANCAR_CODE = "TANCAR";
	public static final String ACTION_CANVI_PRIORITAT_CODE = "CANVI_PRIORITAT";
	public static final String ACTION_CANVI_ESTAT_CODE = "CANVI_ESTAT";
	public static final String ACTION_IMPORTAR_CODE = "IMPORTAR";
	public static final String REPORT_EXPORT_SELECTED_DOCS = "EXPORT_SELECTED_DOCS";
	public static final String ACTION_SYNC_ARXIU = "SYNC_ARXIU";
	public static final String ACTION_GUARDAR_ARXIU = "GUARDAR_ARXIU";	
	public static final String ACTION_IMPORT_DOCS = "IMPORT_DOCS";
	public static final String ACTION_IMPORT_INTE = "IMPORT_INTE";
	public static final String ACTION_GET_PROGRES_SGD = "GET_PROGRES_SGD";
	public static final String ACTION_CANCEL_IMPORT_SGD = "CANCEL_IMPORT_SGD";
	public static final String ACTION_IMPORT_DOCS_ZIP = "IMPORT_DOCS_ZIP";
	public static final String ACTION_GET_PROGRES_ZIP = "GET_PROGRES_ZIP";
	public static final String ACTION_CANCEL_IMPORT_ZIP = "CANCEL_IMPORT_ZIP";
	public static final String ACTION_MOURE_TOT_CODE = "MOURE_TOT";
	public static final String REPORT_PLANTILLA_EXCEL_INTERESSATS = "PLANTILLA_EXCEL_INTERESSATS";
	public static final String REPORT_PLANTILLA_DADES_CSV = "PLANTILLA_DADES_CSV";
	
	public static final String PERSPECTIVE_BASE_CODE = "BASIC";
	public static final String PERSPECTIVE_AVISOS_CODE = "AVISOS";
	public static final String PERSPECTIVE_FOLLOWERS = "FOLLOWERS";
	public static final String PERSPECTIVE_ARXIU_EXPEDIENT = "ARXIU_EXPEDIENT";
	public static final String PERSPECTIVE_COUNT = "COUNT";
	public static final String PERSPECTIVE_COUNT_RESUM = "COUNT_RESUM";
	public static final String PERSPECTIVE_INTERESSATS_CODE = "INTERESSATS_RESUM";
	public static final String PERSPECTIVE_ESTAT_CODE = "ESTAT";
	public static final String PERSPECTIVE_META_EXPEDIENT_CODE = "META_EXPEDIENT";
	public static final String PERSPECTIVE_RELACIONAT_CODE = "RELACIONAT";
	public static final String PERSPECTIVE_NOTIFICACIONS_CADUCADES = "NOTIFICACIONS_CADUCADES";
	public static final String PERSPECTIVE_DOCUMENTS_NO_MOGUTS = "DOCUMENTS_NO_MOGUTS";
	public static final String PERSPECTIVE_DOCUMENTS_OBLIGATORIS_TANCAR = "DOCUMENTS_OBLIGATORIS_TANCAR";
	public static final String PERSPECTIVE_AMB_PINBAL_CODE = "AMB_PINBAL";
	public static final String PERSPECTIVE_PERMIS_CONTINGUT = "PERMIS_CONTINGUT";
	public static final String PERSPECTIVE_AUDIT_CODE = "AUDITORIA";
	public static final String PERSPECTIVE_EN_PROCES_CANVI_ESTAT_CODE = "EN_PROCES_CANVI_ESTAT";
	public static final String PERSPECTIVE_EN_PROCES_TANCAMENT_CODE = "EN_PROCES_TANCAMENT";
	public static final String PERSPECTIVE_EN_PROCES_CUSTODIAR_CODE = "EN_PROCES_CUSTODIAR";
	

	public static final String FILTER_CODE = "EXPEDIENT_FILTER";
	public static final String MASSIVE_CANVI_ESTAT_FILTER_CODE = "MASSIVE_CANVI_ESTAT_FILTER";
	public static final String MASSIVE_CUSTODIAR_FILTER_CODE = "MASSIVE_CUSTODIAR_FILTER";

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

	//Descripció alternativa pels selectors d'expedient
	public String getNumeroINom() {
		return numero != null && !numero.isEmpty() ? numero + " - " + getNom() : getNom();
	}

	@NotNull
	@ResourceField(onChangeActive = true)
	private ResourceReference<MetaExpedientResource, Long> metaExpedient;
    @Transient private MetaExpedientResource metaExpedientInfo;
	private ResourceReference<UsuariResource, String> agafatPer;
	private ResourceReference<MetaExpedientEstatResource, Long> estatAdditional;
    @Transient boolean gestioAmbGrupsActiva;
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

    @Transient private MetaExpedientEstatResource estatAdditionalInfo;
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
    @Transient private boolean asignarSeguidor = false;
    @Transient private ArxiuDetallDto arxiu;
    @Transient private Date dataDarrerEnviament;
    @Transient private boolean potModificar;
    @Transient private boolean potModificarContingut;
    @Transient private List<ResourceReference<ExpedientResource, Long>> relacionatsPer;
    @Transient private List<ResourceReference<ExpedientResource, Long>> relacionatsAmb;
    @Transient private List<ResourceReference<DocumentResource, Long>> documentObligatorisAlTancar = new ArrayList<>();
    @Transient private boolean conteDocuments;
    @Transient private boolean conteDocumentsFirmats;
    @Transient private boolean conteDocumentsEnProcessDeFirma;
    @Transient private boolean conteDocumentsDePortafirmesNoCustodiats;
    @Transient private boolean conteDocumentsDeAnotacionesNoMogutsASerieFinal;
    @Transient private boolean conteDocumentsPendentsReintentsArxiu;
    @Transient private boolean conteNotificacionsCaducades;
    @Transient private boolean potTancar;
    @Transient private String tancarDisabledMessage;
    @Transient private boolean usuariActualWrite;
    @Transient private boolean errorLastEnviament;
    @Transient private boolean errorLastNotificacio;
    @Transient private boolean ambEnviamentsPendents;
    @Transient private boolean ambNotificacionsPendents;
    @Transient private boolean ambDocumentsPinbal;
    @Transient private boolean creacioCarpetesActiva;
    @Transient private boolean isPendentExecucioMassiva;
    @Transient private boolean hideTasca;
    @Transient private Long execucioMassivaCanviEstatId;
    @Transient private Long execucioMassivaTancamentId;
    @Transient private Long execucioMassivaCustodiarId;

    @Getter
	@Setter
    @NoArgsConstructor
    @FieldNameConstants
	public static class ExpedientFilterForm implements Serializable {
		private static final long serialVersionUID = 647178646210565833L;
		private String numero;
        private String nom;
        @ResourceField(enumType = true)
        private String estatCustom = "0";
        private String interessat;
        private ResourceReference<OrganGestorResource, Long> organGestor;
        private ResourceReference<MetaExpedientResource, Long> metaExpedient;
        private ResourceReference<DominiResource, Long> domini;
        @ResourceField(enumType = true)
        private String dominiValor;
        @ResourceField(onChangeActive = true)
        private LocalDateTime dataCreacioInici = LocalDateTime.now().minusMonths(3);
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
    @NoArgsConstructor
    @FieldNameConstants
    public static class ExportGenericForm extends MassiveAction {
    	private boolean exportarExcel		= false;
    	private boolean exportarCsv			= false;
    	private boolean exportarIndexXls 	= false; //isExportacioExcelActiva --> es.caib.ripea.expedient.exportacio.excel
    	private boolean exportarIndexZip 	= false;
    	private boolean exportarIndexPdf 	= false;
    	private boolean inlourerEstructEni	= false;
    	private boolean exportarEni 		= false;
    	private boolean exportarInside 		= false; //isExportacioInsideActiva --> es.caib.ripea.expedient.exportar.inside
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    @MassiveImportDocValid
    public static class MassiveImportDocsAction extends MassiveAction {
        @NotNull private boolean totsExpedientsMateixProcediment;
        @NotNull @NotEmpty
        private List<DocumentAmbTipusDto> documents;
        @Transient private Long metaExpedientId;
        @Transient private FileReference file;
        @Transient @ResourceField(enumType = true)
        private String tipusDocument;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    public static class RelacionarAction implements Serializable {
        private List<Long> ids;
        private Action action;
        public enum Action {
            ADD,
            REMOVE,
        }
    }
    
    @Getter
    @Setter
    public static class TancarExpedientFormAction extends MassiveAction {
        @NotNull
        private String motiu;
        private List<Long> documentsPerFirmar;
    }
    
    @Getter
    @Setter
    public static class CanviEstatExpedientFormAction extends MassiveAction {
        @Transient private String nom;
        @NotNull
        private ResourceReference<MetaExpedientEstatResource, Long> estatAdditional;
    }
    
    @Getter
    @Setter
    public static class CanviPrioritatExpedientFormAction extends MassiveAction {
        @Transient private String nom;
        @NotNull
        private PrioritatEnumDto prioritat;
    	@Size(max = 1024)
    	private String prioritatMotiu;
    }
    
    @Getter
    @Setter
    public static class ImportarExpedientFormAction implements Serializable {
        @NotNull
        private ResourceReference<ExpedientResource, Long> expedientOrigen;
    }
    
    @Getter
    @Setter
    public static class MoureTotFormAction implements Serializable {
    	@NotNull
    	@ResourceField(descriptionField = "numeroINom")
        private ResourceReference<ExpedientResource, Long> expedientDesti;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    @ImportarDocumentValid
    public static class ImportarDocumentsForm implements Serializable {
    	@NotNull
    	private TipusImportEnumDto tipusImportacio = TipusImportEnumDto.NUMERO_REGISTRE;
    	private String codiEni;
    	private String numeroRegistre;
    	private boolean importarInteressats;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone="Europe/Madrid")
        private Date dataPresentacio;
    	private ResourceReference<CarpetaResource, Long> carpeta;
        private String novaCarpetaNom;
        private List<RegistreInteressat> interessats;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    public static class ImportarInteressatsForm implements Serializable {
    	@NotNull private String numeroRegistre;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    @ImportarDocumentsZipValid
    public static class ImportarDocumentsZipForm implements Serializable {
//    	@NotNull
    	@ResourceField(onChangeActive = true)
    	private FileReference documentZip;
        @NotNull @NotEmpty
    	private List<ImportacioZipDocument> documentsZip;

        private ResourceReference<CarpetaResource, Long> carpeta;

        @Transient
        private String nom;
        @Transient
        private ResourceReference<MetaDocumentResource, Long> tipusDocument;
    }

    @Getter
    @Setter
    public static class MassiveCanviEstatFilter implements Serializable {
        private ResourceReference<MetaExpedientResource, Long> procediment;
        private String nom;
        private ResourceReference<ExpedientResource, Long> expedient;
        private Date dataCreacioInici;
        private Date dataCreacioFi;
        @ResourceField(enumType = true)
        private String estatCustom;
        private PrioritatEnumDto prioritat;
        private ResourceReference<GrupResource, Long> grup;
        private boolean mostrarGrups;
    }

    @Getter
    @Setter
    public static class MassiveCustodiarFilter implements Serializable {
        private String nom;
        private ResourceReference<MetaExpedientResource, Long> procediment;
        private ResourceReference<ExpedientResource, Long> expedient;
        private Date dataCreacioInici;
        private Date dataCreacioFi;
        private ResourceReference<GrupResource, Long> grup;
        private boolean mostrarGrups;
    }
    
}