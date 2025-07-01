package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.resourcevalidation.AdjuntValid;
import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.AllArgsConstructor;
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
                        code = ContingutResource.PERSPECTIVE_PATH_CODE),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_ENVIAR_VIA_EMAIL_CODE,
                        formClass = DocumentResource.EnviarViaEmailFormAction.class,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_GET_CSV_LINK,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_CONVERTIR_DEFINITIU,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_GUARDAR_ARXIU,
                        requiresId = true),                
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_FIRMA_WEB_INI,
                        formClass = DocumentResource.IniciarFirmaSimple.class,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_VIA_FIRMA,
                        formClass = DocumentResource.ViaFirmaForm.class,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentResource.ACTION_NEW_DOC_PINBAL,
                        formClass = DocumentResource.NewDocPinbalForm.class),            
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
                        code = DocumentResource.ACTION_RESUM_IA,
                        formClass = DocumentResource.ResumIaFormAction.class),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.ACTION,
						code = DocumentResource.ACTION_MASSIVE_NOTIFICAR_ZIP_CODE,
						formClass = DocumentResource.NotificarDocumentsZipFormAction.class),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.ACTION,
						code = DocumentResource.ACTION_MASSIVE_CANVI_TIPUS_CODE,
						formClass = DocumentResource.UpdateTipusDocumentFormAction.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.REPORT,
                        code = DocumentResource.ACTION_DESCARREGAR_MASSIU,
                        formClass = DocumentResource.MassiveAction.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.REPORT,
                        code = DocumentResource.REPORT_DESCARREGAR_VERSIO_CODE,
                        formClass = DocumentResource.DescarregarVersionFormAction.class,
                        requiresId = true),
        })
@AdjuntValid(groups = {Resource.OnCreate.class, Resource.OnUpdate.class})
public class DocumentResource extends NodeResource {

	public static final String PERSPECTIVE_COUNT_CODE = "COUNT";
    public static final String PERSPECTIVE_VERSIONS_CODE = "VERSIONS";
    public static final String PERSPECTIVE_ARXIU_DOCUMENT_CODE = "ARXIU_DOCUMENT";
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
	public static final String ACTION_DESCARREGAR_MASSIU = "DESCARREGAR_MASSIU";
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

    @NotNull
    @Transient
    @ResourceField(onChangeActive = true)
    public ResourceReference<MetaDocumentResource, Long> metaDocument;

    @Transient
    @ResourceField(onChangeActive = true)
    public Boolean hasFirma;
    @Transient private ArxiuDetallDto arxiu;
    @Transient private List<DocumentVersioDto> versions;
    @Transient private int NumMetaDades;
    @Transient private boolean documentDeAnotacio;
    @Transient private boolean ambNotificacions;
    @Transient private boolean funcionariHabilitatDigitalib;
    @Transient private boolean pluginSummarizeActiu;
    @ResourceField(enumType = true, onChangeActive = true)
    @Transient private String digitalitzacioPerfil;
    @Transient private String digitalitzacioProcesUrl;
    @Transient private MetaDocumentResource metaDocumentInfo;

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
        @NotNull
    	private VersioDocumentEnum versioDocument = VersioDocumentEnum.IMPRIMIBLE;
        private String email;
        private List<ResourceReference<UsuariResource, String>> responsables = new ArrayList<>();
    }
    
    @Getter
    @Setter
    public static class IniciarFirmaSimple implements Serializable {
    	@NotNull
    	private String motiu;
    }
    
    @Getter
    @Setter
    @FieldNameConstants
    public static class ViaFirmaForm implements Serializable {
    	@Size(max=256)
    	private String titol;
    	@Size(max=256)
    	private String descripcio;
    	@NotNull
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
    	@Size(max=256)
    	private String observacions;
    	private Boolean firmaParcial;
    	private Boolean validateCodeEnabled;
    	private String validateCode;
    	private Boolean rebreCorreu;
    }
    
    @Getter
    @Setter
    @FieldNameConstants
    public static class NewDocPinbalForm implements Serializable {
        @NotNull
        private ResourceReference<ExpedientResource, Long> expedient;

    	@NotNull
        @ResourceField(onChangeActive = true)
    	private ResourceReference<MetaDocumentResource, Long> tipusDocument;
    	@NotNull
    	private String finalitat;
    	@NotNull
    	private ResourceReference<InteressatResource, Long> titular;
    	@NotNull
    	private PinbalConsentimentEnumDto consentiment;
    	@Transient
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

        private DocumentNotificacioTipusEnumDto tipus;
        @NotNull
        private DocumentNotificacioEstatEnumDto estat = DocumentNotificacioEstatEnumDto.PENDENT;
        @NotNull
        @NotEmpty
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
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone="Europe/Madrid")
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
    @FieldNameConstants
    public static class EnviarPortafirmesFormAction implements Serializable {
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
        private List<ResourceReference<UsuariResource, String>> responsables = new ArrayList<>();
        private List<String> nifsManuals = new ArrayList<>();
        @ResourceField(enumType = true)
        private List<String> carrecs = new ArrayList<>();
        private MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus;

        // FLUX
        @Transient
        @ResourceField(enumType = true, onChangeActive = true)
    	private String portafirmesEnviarFluxId;
        @Transient private String portafirmesFluxUrl;
        @Transient private String urlInicioFlujoFirma;
        @Transient private MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus;
        @Transient private PortafirmesFluxRespostaDto fluxCreat;
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
        resultat.setFitxerContentType(this.getFitxerContentType());
        resultat.setAmbFirma(this.isAmbFirma());
        switch (this.getDocumentFirmaTipus()) {
            case FIRMA_ADJUNTA:
                resultat.setTipusFirma(DocumentTipusFirmaEnumDto.ADJUNT);
                break;
            case FIRMA_SEPARADA:
                resultat.setTipusFirma(DocumentTipusFirmaEnumDto.SEPARAT);
                break;
            default:
                break;
        }
        resultat.setFirmaContingut(this.getFirmaContingut());
        resultat.setFirmaContentType(this.getFirmaContentType());
        return resultat;
    }
}