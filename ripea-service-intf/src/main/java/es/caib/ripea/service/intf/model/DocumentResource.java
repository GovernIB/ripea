package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
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
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ArxiuDetallDto;
import es.caib.ripea.service.intf.dto.ArxiuEstatEnumDto;
import es.caib.ripea.service.intf.dto.ArxiuFirmaDto;
import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.ripea.service.intf.dto.DocumentPublicacioTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentTipusFirmaEnumDto;
import es.caib.ripea.service.intf.dto.DocumentVersioDto;
import es.caib.ripea.service.intf.dto.MetaDocumentDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.service.intf.dto.NtiOrigenEnumDto;
import es.caib.ripea.service.intf.dto.PinbalConsentimentEnumDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.service.intf.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.service.intf.dto.ServeiTipusEnumDto;
import es.caib.ripea.service.intf.dto.SexeEnumDto;
import es.caib.ripea.service.intf.dto.SiNoEnumDto;
import es.caib.ripea.service.intf.dto.TipusPassaportEnumDto;
import es.caib.ripea.service.intf.dto.VersioDocumentEnum;
import es.caib.ripea.service.intf.dto.ViaFirmaTipusDestinatariEnum;
import es.caib.ripea.service.intf.resourcevalidation.AdjuntValid;
import es.caib.ripea.service.intf.resourcevalidation.DocPinbalValid;
import es.caib.ripea.service.intf.resourcevalidation.EnviarPortafirmesValid;
import es.caib.ripea.service.intf.resourcevalidation.ViaFirmaValid;
import es.caib.ripea.service.intf.utils.Utils;
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
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = DocumentResource.PERSPECTIVE_COUNT_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = DocumentResource.PERSPECTIVE_VERSIONS_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = DocumentResource.PERSPECTIVE_ARXIU_DOCUMENT_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ContingutResource.PERSPECTIVE_PATH_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = DocumentResource.PERSPECTIVE_FIRMES_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = DocumentResource.PERSPECTIVE_PROCEDIMENT_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = DocumentResource.PERSPECTIVE_EN_PROCES_PORTAFIB_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = DocumentResource.PERSPECTIVE_EN_PROCES_FIRMA_WEB_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = DocumentResource.PERSPECTIVE_EN_PROCES_CUSTODIAR_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = DocumentResource.PERSPECTIVE_RESUM_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_ENVIAR_VIA_EMAIL_CODE,
                        formClass = DocumentResource.EnviarViaEmailFormAction.class,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_GET_CSV_LINK,
                        formClass = NodeResource.MassiveAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_CONVERTIR_DEFINITIU,
                        formClass = NodeResource.MassiveAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_GUARDAR_ARXIU,
                        formClass = NodeResource.MassiveAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_FIRMA_WEB_INI,
                        formClass = DocumentResource.IniciarFirmaNavegador.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_VIA_FIRMA,
                        formClass = DocumentResource.ViaFirmaForm.class,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_NEW_DOC_PINBAL,
                        formClass = DocumentResource.NewDocPinbalForm.class),            
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_MOURE_CODE,
                        formClass = DocumentResource.MoureFormAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_PUBLICAR_CODE,
                        formClass = DocumentResource.PublicarFormAction.class,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_NOTIFICAR_CODE,
                        formClass = DocumentResource.NotificarFormAction.class,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_ENVIAR_PORTAFIRMES_CODE,
                        formClass = DocumentResource.EnviarPortafirmesFormAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_RESUM_IA,
                        formClass = DocumentResource.ResumIaFormAction.class),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = DocumentResource.ACTION_MASSIVE_NOTIFICAR_ZIP_CODE,
						formClass = DocumentResource.NotificarDocumentsZipFormAction.class),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = DocumentResource.ACTION_MASSIVE_CANVI_TIPUS_CODE,
						formClass = DocumentResource.UpdateTipusDocumentFormAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.REPORT,
                        code = DocumentResource.REPORT_DESCARREGAR_MASSIU,
                        formClass = DocumentResource.MassiveAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.REPORT,
                        code = DocumentResource.REPORT_DESCARREGAR_VERSIO_CODE,
                        formClass = DocumentResource.DescarregarVersionFormAction.class,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = DocumentResource.MASSIVE_PORTAFIRMES_FILTER_CODE,
                        formClass = DocumentResource.MassivePortafirmesFilter.class),
        })
@AdjuntValid(groups = {Resource.OnCreate.class, Resource.OnUpdate.class})
public class DocumentResource extends NodeResource {

    public static final String MASSIVE_PORTAFIRMES_FILTER_CODE = "MASSIVE_PORTAFIRMES_FILTER";

	public static final String PERSPECTIVE_COUNT_CODE = "COUNT";
    public static final String PERSPECTIVE_VERSIONS_CODE = "VERSIONS";
    public static final String PERSPECTIVE_ARXIU_DOCUMENT_CODE = "ARXIU_DOCUMENT";
    public static final String PERSPECTIVE_FIRMES_CODE = "FIRMES";
    public static final String PERSPECTIVE_PROCEDIMENT_CODE = "PROCEDIMENT";
    public static final String PERSPECTIVE_EN_PROCES_PORTAFIB_CODE = "EN_PROCES_PORTAFIB";
    public static final String PERSPECTIVE_EN_PROCES_FIRMA_WEB_CODE = "EN_PROCES_FIRMA_WEB";
    public static final String PERSPECTIVE_EN_PROCES_CUSTODIAR_CODE = "EN_PROCES_CUSTODIAR";
    public static final String PERSPECTIVE_RESUM_CODE = "RESUM";
    
    public static final String ACTION_ENVIAR_VIA_EMAIL_CODE = "ENVIAR_VIA_EMAIL";
    public static final String ACTION_ENVIAR_PORTAFIRMES_CODE = "ENVIAR_PORTAFIRMES";
    public static final String ACTION_RESUM_IA = "RESUM_IA";
    public static final String ACTION_MOURE_CODE = "MOURE";
    public static final String ACTION_PUBLICAR_CODE = "PUBLICAR";
    public static final String ACTION_NOTIFICAR_CODE = "NOTIFICAR";
    public static final String ACTION_GET_CSV_LINK = "GET_CSV_LINK";
    public static final String ACTION_CONVERTIR_DEFINITIU = "CONVERTIR_DEFINITIU";
    public static final String ACTION_GUARDAR_ARXIU = "GUARDAR_ARXIU";
  //Flux de firma i firma en navegador
    public static final String ACTION_FIRMA_WEB_INI = "FIRMA_WEB_INI";
    public static final String ACTION_NEW_DOC_PINBAL = "NEW_DOC_PINBAL";
    public static final String ACTION_VIA_FIRMA = "VIA_FIRMA";
	//Accions massives desde la pipella de contingut
	public static final String REPORT_DESCARREGAR_MASSIU = "DESCARREGAR_MASSIU";
    public static final String ACTION_MASSIVE_NOTIFICAR_ZIP_CODE = "MASSIVE_NOTIFICAR_ZIP";
    public static final String ACTION_MASSIVE_CANVI_TIPUS_CODE = "MASSIVE_CANVI_TIPUS";
    public static final String REPORT_DESCARREGAR_VERSIO_CODE = "DESCARREGAR_VERSIO";

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

	private boolean ordrePatch;

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
    
	private ResourceReference<MetaExpedientEstatResource, Long> expedientEstatAdditional;
    @NotNull
    private ResourceReference<ExpedientResource, Long> expedient;
    
    @NotNull
    @ResourceField(onChangeActive = true)
    public ResourceReference<MetaDocumentResource, Long> metaDocument;
    
    @ResourceField(onChangeActive = true)
    public Boolean firmaParcial;
    @Transient
    @ResourceField(onChangeActive = true)
    public Boolean hasFirma;
    
    @Transient private List<ResourceReference<RegistreAnnexResource, Long>> annexos;
    @Transient private ArxiuDetallDto arxiu;
    @Transient private List<DocumentVersioDto> versions;
    @Transient private List<ArxiuFirmaDto> firmes;
    @Transient private int NumMetaDades;
    @Transient private boolean documentDeAnotacio;
    @Transient private boolean ambNotificacions;
    @Transient private String estatDarreraNotificacio;
    @Transient private boolean errorDarreraNotificacio;
    @Transient private boolean funcionariHabilitatDigitalib;
    @Transient private boolean errorEnviamentPortafirmes;
    // El document prove d'un annex d'anotacio que ha quedat amb error: esta pendent de
    // moure a la serie documental del procediment. Equivalent a DocumentEntity.isPendentMoverArxiu().
    @Transient private boolean pendentMoverArxiu;
    @Transient private boolean pluginSummarizeActiu;
    @ResourceField(enumType = true, onChangeActive = true)
    @Transient private String digitalitzacioPerfil;
    @Transient private String digitalitzacioProcesUrl;
    @Transient private MetaDocumentResource metaDocumentInfo;
    @Transient private ResourceReference<CarpetaResource, Long> carpeta;
    @Transient private boolean isDeteccioFirmaAutomaticaActiva;
    @Transient private ResourceReference<MetaExpedientResource, Long> metaExpedient;
    @Transient private String csvLinkUrl;
    @Transient private Long execucioMassivaPortafibId;
    @Transient private Long execucioMassivaFirmaWebId;
    @Transient private Long execucioMassivaCustodiarId;

    public String getFitxerExtension() {
        if (fitxerNom != null) {
            return fitxerNom.substring(
                    fitxerNom.lastIndexOf('.') + 1,
                    fitxerNom.length());
        } else {
            return "";
        }
    }

	public Long getOrdreLong() {
		return (long)ordre;
	}

    public String getFitxerContentType() {
    	if (Utils.hasValue(this.fitxerContentType)) { return this.fitxerContentType; }
    	if (this.adjunt!=null && Utils.hasValue(this.adjunt.getContentType())) { return this.adjunt.getContentType(); }
    	return "application/octet-stream";
    }
    
    public String getFirmaContentType() {
    	if (Utils.hasValue(this.firmaContentType)) { return this.firmaContentType; }
    	if (this.firmaAdjunt!=null && Utils.hasValue(this.firmaAdjunt.getContentType())) { return this.firmaAdjunt.getContentType(); }
    	return "application/octet-stream";
    }
    
    public boolean isFirmaParcial() {
    	return DocumentEstatEnumDto.FIRMA_PARCIAL.equals(this.estat);
    }
    
    @Getter
    @Setter
    public static class EnviarViaEmailFormAction implements Serializable {
        @NotNull
    	private VersioDocumentEnum versioDocument = VersioDocumentEnum.IMPRIMIBLE;
        private String email;
        private List<ResourceReference<UsuariResource, String>> responsables = new ArrayList<>();
        private boolean disableVersioDocument = false;
    }
    
    @Getter
    @Setter
    public static class IniciarFirmaNavegador extends MassiveAction {
    	@NotNull
    	private String motiu;
    }
    
    @Getter
    @Setter
    @FieldNameConstants
    @ViaFirmaValid
    public static class ViaFirmaForm implements Serializable {
    	@Size(max=256)
    	private String titol;
    	@Size(max=256)
    	private String descripcio;
    	@NotNull
    	private ViaFirmaTipusDestinatariEnum tipusDestinatari = ViaFirmaTipusDestinatariEnum.TABLET;
        @Transient
        @ResourceField(enumType = true, onChangeActive = true)
    	private String codiUsuariViaFirma;
    	@Transient 
    	private boolean isDispositiusEnabled = false;
    	@ResourceField(enumType = true)
    	private String viaFirmaDispositiuCodi;
        @ResourceField(onChangeActive = true)
    	private ResourceReference<InteressatResource, Long> interessat;
    	@NotNull
    	private String signantNif;
    	@NotNull
    	private String signantNom;
    	private String signantEmail;
    	@Size(max=256)
    	private String observacions;
    	private Boolean firmaParcial = false;
    	private Boolean emplenable = false;
    	private Boolean validateCodeEnabled = false;
    	private String validateCode;
    	private Boolean rebreCorreu = false;
    }
    
    @Getter
    @Setter
    @FieldNameConstants
    @DocPinbalValid
    public static class NewDocPinbalForm implements Serializable {
        @NotNull
        private ResourceReference<ExpedientResource, Long> expedient;

    	@NotNull
        @ResourceField(onChangeActive = true, springFilter = "actiu : true")
    	private ResourceReference<MetaDocumentResource, Long> tipusDocument;
    	@NotNull
    	private String finalitat;
    	@NotNull
    	private ResourceReference<InteressatResource, Long> titular;
    	@NotNull
    	private PinbalConsentimentEnumDto consentiment = PinbalConsentimentEnumDto.SI;
    	@NotNull
    	private String codiServeiPinbal;

    	@ResourceField(enumType = true)
        private String comunitatAutonoma = "04";
    	@ResourceField(enumType = true)
        private String provincia = "07";
        @ResourceField(enumType = true)
        private String municipi;
        private Date dataConsulta;
        private Date dataNaixement;
        private Date dataCaducidad;
        private Date dataExpedicion;
        private SiNoEnumDto consentimentTipusDiscapacitat = SiNoEnumDto.SI;
        private String numeroTitol;
        @ResourceField(enumType = true)
        private String nacionalitat = "724";
        @ResourceField(enumType = true)
        private String paisNaixament = "724";
        private boolean ausenciaSegundoApellido;
        private SexeEnumDto sexe;
        @ResourceField(enumType = true)
        private String provinciaNaixament = "07";
        private String poblacioNaixament;
        @ResourceField(enumType = true)
        private String municipiNaixament;
        private String nomPare;
        private String nomMare;
        private String telefon;
        private String email;
        private Integer nombreAnysHistoric;
        private Integer exercici;
        private String numeroSoporte;
        private TipusPassaportEnumDto tipusPassaport;

        private String registreCivil;
        private String tom;
        private String pagina;
        private Date dataRegistre;
        @ResourceField(enumType = true)
        private String municipiRegistre;

        private Integer curs;
    }
    
    @Getter
    @Setter
    public static class MoureFormAction extends ExpedientResource.MassiveAction {
        private String contingut;
        @NotNull
        private ResourceReference<ExpedientResource, Long> expedient;
        private ResourceReference<CarpetaResource, Long> carpeta;
        private String motiu;
        private String carpetaNova;
        @NotNull
        private Action action = Action.MOURE;

        public enum Action {
            MOURE,
            COPIAR,
            VINCULAR
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
        @NotNull
        @ResourceField(onChangeActive = true)
        private DocumentNotificacioTipusEnumDto tipus = DocumentNotificacioTipusEnumDto.NOTIFICACIO;
        @NotNull
        private DocumentNotificacioEstatEnumDto estat = DocumentNotificacioEstatEnumDto.PENDENT;
        @ResourceField(onChangeActive = true)
        private List<ResourceReference<InteressatGrupResource, Long>> grups = new ArrayList<>();
        @NotNull
        @NotEmpty
        @ResourceField(onChangeActive = true)
        private List<ResourceReference<InteressatResource, Long>> interessats = new ArrayList<>();
        @NotNull
        private String concepte;
        @NotNull
        private ServeiTipusEnumDto serveiTipus = ServeiTipusEnumDto.NORMAL;
//        @Field(type = Field.TYPE_TEXTAREA)
        private String descripcio;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone="Europe/Madrid")
        private Date dataProgramada;
        @NotNull
        @ResourceField(onChangeActive = true)
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone="Europe/Madrid")
        private Date dataCaducitat;
        @NotNull
        @ResourceField(onChangeActive = true)
        private Integer duracio;
        private Integer retard;
        private Boolean entregaPostal;

        @Transient
        private boolean permetreEnviamentPostal;
        @Transient
        private List<ResourceReference<InteressatResource, Long>> interessatsAmbAvis = new ArrayList<>();
        @Transient 
        private boolean administracioSir = false;
        @Transient
        private boolean administracioSirFormat;
        @Transient
        private ResourceReference<ExpedientResource, Long> expedient;
    }

    @Getter
    @Setter
    @FieldNameConstants
    @EnviarPortafirmesValid
    public static class EnviarPortafirmesFormAction extends MassiveAction {
		private static final long serialVersionUID = -763974048421192748L;
		@NotNull
        private String motiu;
        @NotNull
        private PortafirmesPrioritatEnumDto prioritat = PortafirmesPrioritatEnumDto.NORMAL;
        private List<ResourceReference<DocumentResource, Long>> annexos = new ArrayList<>();
        
        // Firma parcial
        private boolean firmaParcial;
        private boolean avisFirmaParcial;
        @Transient
        private boolean mostrarFirmaParcial;
        @Transient
        private boolean mostrarAvisFirmaParcial;

        // SIMPLE
        @ResourceField(descriptionField = "nomAndNif")
        private List<ResourceReference<UsuariResource, String>> responsables = new ArrayList<>();
        private String nifsManuals;
        @ResourceField(enumType = true)
        private List<String> carrecs = new ArrayList<>();
        private MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus = MetaDocumentFirmaSequenciaTipusEnumDto.SERIE;

        // FLUX
        @Transient private Long metaDocumentId;
        @Transient
        @ResourceField(enumType = true, onChangeActive = true)
    	private String portafirmesEnviarFluxId;
        @Transient private String portafirmesFluxUrl;
        @Transient private String urlInicioFlujoFirma;
        @Transient private MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus;
        @Transient private PortafirmesFluxRespostaDto fluxCreat;
        @Transient private String idTransaccio;
        
        //CAMPS NOMES VISIBLES A ACCIO MASSIVA
        private Date dataInici = Calendar.getInstance().getTime();
        private boolean enviarCorreu;
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
    public static class ResumIaFormAction implements Serializable {
    	@NotNull
    	private FileReference adjunt;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    public static class NotificarDocumentsZipFormAction extends MassiveAction {
    	@NotNull
    	private NtiOrigenEnumDto ntiOrigen;
    	@NotNull
    	private DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
        @NotNull
        @ResourceField(onChangeActive = true)
        private ResourceReference<MetaDocumentResource, Long> metaDocument;

        @Transient
        private ResourceReference<ExpedientResource, Long> expedient;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    public static class DescarregarVersionFormAction implements Serializable {
    	@NotNull
    	private String version;
    }

    @Getter
    @Setter
    public static class MassivePortafirmesFilter implements Serializable {
        private ResourceReference<MetaExpedientResource, Long> procediment;
        private ResourceReference<ExpedientResource, Long> expedient;
        private ResourceReference<MetaDocumentResource, Long> metaDocument;
        private String nom;
        private Date dataCreacioInici;
        private Date dataCreacioFi;
        private ResourceReference<GrupResource, Long> grup;
        private boolean mostrarGrups;
    }

    public DocumentDto toDocumentDto() {
        DocumentDto resultat = new DocumentDto();
        MetaDocumentDto metaNode = new MetaDocumentDto();
        metaNode.setId(this.getMetaDocument().getId());
        resultat.setMetaNode(metaNode);
        resultat.setPareId(this.getPare()!=null?this.getPare().getId():this.getExpedient().getId());
        resultat.setDocumentTipus(this.getDocumentTipus());
        resultat.setNom(this.getNom());
        resultat.setDescripcio(this.getDescripcio());
        resultat.setData(Calendar.getInstance().getTime());
        resultat.setNtiOrigen(this.getNtiOrigen());
        resultat.setNtiEstadoElaboracion(this.getNtiEstadoElaboracion());
        resultat.setNtiIdDocumentoOrigen(this.getNtiIdDocumentoOrigen());
        resultat.setFitxerNom(this.fitxerNom);
        resultat.setFitxerContingut(this.getFitxerContingut());
        resultat.setFitxerContentType(getFitxerContentType());
        resultat.setAmbFirma(this.hasFirma!=null?this.hasFirma:false);
        resultat.setEstat(this.firmaParcial!=null && this.firmaParcial ? DocumentEstatEnumDto.FIRMA_PARCIAL : this.estat);
        switch (this.getDocumentFirmaTipus()) {
            case FIRMA_ADJUNTA:
                resultat.setTipusFirma(DocumentTipusFirmaEnumDto.ADJUNT);
                break;
            case FIRMA_SEPARADA:
                resultat.setTipusFirma(DocumentTipusFirmaEnumDto.SEPARAT);
                resultat.setFirmaSeparada(true);
                resultat.setFirmaNom(this.getFirmaNom());
                break;
            default:
                break;
        }
        resultat.setFirmaContingut(this.getFirmaContingut());
        resultat.setFirmaContentType(getFirmaContentType());
        return resultat;
    }
}