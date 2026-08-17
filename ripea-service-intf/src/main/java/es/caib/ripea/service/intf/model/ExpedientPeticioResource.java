package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.time.Year;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioEstatViewEnumDto;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.model.NodeResource.MassiveAction;
import es.caib.ripea.service.intf.resourcevalidation.AcceptarAnotacioValid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "identificador" },
        descriptionField = "identificador",
        artifacts = {
                @ResourceArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = ExpedientPeticioResource.FILTER_CODE,
                        formClass = ExpedientPeticioResource.FilterForm.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = ExpedientPeticioResource.ACTUALITZAR_ESTAT_FILTER_CODE,
                        formClass = ExpedientPeticioResource.ActualitzarEstatFilter.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = ExpedientPeticioResource.ANOTACIONS_COMUNICADES_FILTER_CODE,
                        formClass = ExpedientPeticioResource.AnotacionsComunicadesFilter.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ExpedientPeticioResource.PERSPECTIVE_REGISTRE_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ExpedientPeticioResource.PERSPECTIVE_JUSTIFICANT_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ExpedientPeticioResource.PERSPECTIVE_ESTAT_VIEW_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ExpedientPeticioResource.PERSPECTIVE_EN_PROCES_ACTUALITZAR_ESTAT_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.REPORT,
                        code = ExpedientPeticioResource.REPORT_DOWNLOAD_JUSTIFICANT,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientPeticioResource.ACTION_REBUTJAR_ANOTACIO,
                        formClass = ExpedientPeticioResource.RebutjarAnotacioForm.class,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientPeticioResource.ACTION_ACCEPTAR_ANOTACIO,
                        formClass = ExpedientPeticioResource.AcceptarAnotacioForm.class,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientPeticioResource.ACTION_ESTAT_DISTRIBUCIO,
                        formClass = MassiveAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientPeticioResource.ACTION_CONSULTAR_I_GUARDAR,
                        formClass = MassiveAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ExpedientPeticioResource.PERSPECTIVE_ANNEXOS_ERROR_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientPeticioResource.ACTION_SUBSANAR_ANNEXOS,
                        formClass = ExpedientPeticioResource.SubsanarAnnexosForm.class,
                        requiresId = true),
        }
)
public class ExpedientPeticioResource extends BaseAuditableResource<Long> {

    public static final String ANOTACIONS_COMUNICADES_FILTER_CODE = "ANOTACIONS_COMUNICADES_FILTER";
    public static final String ACTUALITZAR_ESTAT_FILTER_CODE = "ACTUALITZAR_ESTAT_FILTER";
    public static final String FILTER_CODE = "ANOTACIO_FILTER";

    public static final String PERSPECTIVE_REGISTRE_CODE = "REGISTRE";
    /**
     * Metadades del justificant de registre. Requereix una consulta a l'Arxiu per anotació, així que
     * només s'ha de demanar en obrir un detall, mai des d'un llistat: per saber si n'hi ha, la
     * perspectiva {@link #PERSPECTIVE_REGISTRE_CODE} ja informa {@code teJustificant} sense cap consulta remota.
     */
    public static final String PERSPECTIVE_JUSTIFICANT_CODE = "JUSTIFICANT";
    public static final String PERSPECTIVE_ESTAT_VIEW_CODE = "ESTAT_VIEW";
    public static final String PERSPECTIVE_EN_PROCES_ACTUALITZAR_ESTAT_CODE = "EN_PROCES_ACTUALITZAR_ESTAT";
    public static final String PERSPECTIVE_ANNEXOS_ERROR_CODE = "ANNEXOS_ERROR";
    public static final String REPORT_DOWNLOAD_JUSTIFICANT = "DOWNLOAD_JUSTIFICANT";
    public static final String ACTION_REBUTJAR_ANOTACIO = "REBUTJAR_ANOTACIO";
    public static final String ACTION_ACCEPTAR_ANOTACIO = "ACCEPTAR_ANOTACIO";
    public static final String ACTION_ESTAT_DISTRIBUCIO = "ESTAT_DISTRIBUCIO";
    public static final String ACTION_CONSULTAR_I_GUARDAR = "CONSULTAR_I_GUARDAR";
    public static final String ACTION_SUBSANAR_ANNEXOS = "SUBSANAR_ANNEXOS";

    /**
     * Clau reservada dins {@code AcceptarAnotacioForm.annexos} per al justificant de registre: la resta de
     * claus són identificadors d'annex reals, sempre majors que zero.
     */
    public static final Long ANNEX_ID_JUSTIFICANT = 0L;

//    private Long id;
    private String identificador;
    private String clauAcces;
    private Date dataAlta;
    private ExpedientPeticioEstatEnumDto estat;
    private ExpedientPeticioAccioEnumDto accio;
    private String notificaDistError;
    private Date dataActualitzacio;
    private ResourceReference<UsuariResource, String> usuariActualitzacio;
    private String observacions;

    private ResourceReference<RegistreResource, Long> registre;
    @ResourceField(onChangeActive = true)
    private ResourceReference<MetaExpedientResource, Long> metaExpedient;
    private ResourceReference<GrupResource, Long> grup;
    private ResourceReference<ExpedientResource, Long> expedient;
    
    private boolean pendentCanviEstatDistribucio;
    private int reintentsCanviEstatDistribucio;

    private boolean consultaWsError;
    private String consultaWsErrorDesc;
    private Date consultaWsErrorDate;

    @Transient private RegistreResource registreInfo;
    @Transient private String registreExtracte;
    @Transient private boolean mostrarGrups = false;
    @Transient private ExpedientPeticioEstatViewEnumDto estatView;
    @Transient private Long execucioMassivaActualitzarEstatId;
    @Transient private boolean teAnnexosAmbError;
    /** Indica si l'anotació té justificant de registre incorporable, sense consultar-ne les metadades a l'Arxiu. */
    @Transient private boolean teJustificant;

    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    public static class FilterForm implements Serializable {
        private String numRegistre;
        private String extracte;
        private String destinacio;
        private ResourceReference<MetaExpedientResource, Long> metaExpedient;
        private Date dataRecepcioInicial;
        private Date dataRecepcioFinal;
        private ExpedientPeticioEstatViewEnumDto estat = ExpedientPeticioEstatViewEnumDto.PENDENT;
        private String interessat;
        private ResourceReference<GrupResource, Long> grup;
    }

    @Getter
    @Setter
    public static class AnotacionsComunicadesFilter implements Serializable {
        private String numRegistre;
        private ExpedientPeticioEstatEnumDto estat = ExpedientPeticioEstatEnumDto.CREAT;
        private Date dataAltaInici;
        private Date dataAltaFi;
        private boolean nomesAmbErrors = true;
    }

    @Getter
    @Setter
    public static class ActualitzarEstatFilter implements Serializable {
        private String numero;
        private Date dataAltaInici;
        private Date dataAltaFi;
        private ExpedientPeticioEstatEnumDto estat;
        private ResourceReference<MetaExpedientResource, Long> procediment;
        private ResourceReference<ExpedientResource, Long> expedient;
        private ResourceReference<GrupResource, Long> grup;
        private boolean nomesPendents = true;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    @AcceptarAnotacioValid
    public static class AcceptarAnotacioForm implements Serializable {
        @NotNull
    	private ExpedientPeticioAccioEnumDto accio = ExpedientPeticioAccioEnumDto.CREAR;
        @NotNull @ResourceField(onChangeActive = true)
    	private ResourceReference<MetaExpedientResource, Long> metaExpedient;
    	private ResourceReference<ExpedientResource, Long> expedient;
    	private String newExpedientTitol;
    	private PrioritatEnumDto prioritat = PrioritatEnumDto.B_NORMAL;
    	private String prioritatMotiu;
        @ResourceField(onChangeActive = true)
    	private Integer any = Year.now().getValue();
    	private Long sequencia;
        @Transient boolean gestioAmbGrupsActiva;
        @Transient private boolean disableOrganGestor = false;
        @Transient private String expedientNoTrobatMissatge;
        /**
         * Id del tipus de document REGISTRE_JUSTIFICANT_ENTRADA del procediment seleccionat, que el front
         * proposa com a valor per defecte de la fila del justificant. Null si el procediment no en té cap d'actiu.
         */
        @Transient private Long metaDocumentJustificantId;
        private ResourceReference<GrupResource, Long> grup;
    	private boolean associarInteressats = true;
    	private boolean agafarExpedient = true;
    	private ResourceReference<OrganGestorResource, Long> organGestor;
        @NotNull
    	private List<Long> interessats;
        @NotNull @NotEmpty
        private Map<Long, String> annexos = new HashMap<>();
        @Transient @ResourceField(enumType = true)
        private String tipusDocument;
        private boolean seguidor;
    }
    
    @Getter
    @Setter
    public static class RebutjarAnotacioForm implements Serializable {
    	@NotNull
    	private String motiu;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    public static class SubsanarAnnexosForm implements Serializable {
        //Mapa amb clau l'id de l'annex en error i valor l'id del meta-document seleccionat
        @NotNull @NotEmpty
        private Map<Long, String> annexos = new HashMap<>();
        @Transient @ResourceField(enumType = true)
        private String tipusDocument;
    }
}