package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ArxiuDetallDto;
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		quickFilterFields = { "numero", "nom" },
        descriptionField = "nom",
		artifacts = {
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
                        code = ExpedientResource.ACTION_FOLLOW_CODE,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_UNFOLLOW_CODE,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_AGAFAR_CODE,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientResource.ACTION_RETORNAR_CODE,
                        requiresId = true),
		})
public class ExpedientResource extends NodeResource {

	private static final long serialVersionUID = 7440910672703796468L;
	public static final String ACTION_FOLLOW_CODE = "FOLLOW";
	public static final String ACTION_UNFOLLOW_CODE = "UNFOLLOW";
	public static final String ACTION_AGAFAR_CODE = "AGAFAR";
	public static final String ACTION_RETORNAR_CODE = "RETORNAR";
	public static final String PERSPECTIVE_FOLLOWERS = "FOLLOWERS";
	public static final String PERSPECTIVE_ARXIU_EXPEDIENT = "ARXIU_EXPEDIENT";
	public static final String PERSPECTIVE_COUNT = "COUNT";
	public static final String PERSPECTIVE_INTERESSATS_CODE = "INTERESSATS_RESUM";
	public static final String PERSPECTIVE_ESTAT_CODE = "ESTAT";
	public static final String PERSPECTIVE_RELACIONAT_CODE = "RELACIONAT";
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

    @Transient
    private ExpedientEstatResource estatAdditionalInfo;

    @Transient private List<InteressatResource> interessats;
    @Transient private List<ResourceReference<UsuariResource, String>> seguidors;
    @Transient private boolean seguidor = false;
    @Transient private int numComentaris;
    @Transient private int numSeguidors;
    @Transient private int numContingut;
    @Transient private int numDades;
    @Transient private int numMetaDades;
    @Transient private int numInteressats;
    @Transient private int numRemeses;
    @Transient private int numPublicacions;
    @Transient private int numAnotacions;
    @Transient private int numTasques;
    @Transient private boolean disableOrganGestor = false;
    public String getTipusStr() {
        return this.getMetaExpedient() != null ? this.getMetaExpedient().getDescription() + " - " + ntiClasificacionSia : null;
    }

    @Transient
    private ArxiuDetallDto arxiu;

    @Transient private List<ResourceReference<ExpedientResource, Long>> relacionatsPer = new ArrayList<>();
    @Transient private List<ResourceReference<ExpedientResource, Long>> relacionatsAmb = new ArrayList<>();

    @Transient private boolean conteDocuments;
    @Transient private boolean conteDocumentsFirmats;
    @Transient private boolean conteDocumentsEnProcessDeFirma;
    @Transient private boolean conteDocumentsDePortafirmesNoCustodiats;
    @Transient private boolean conteDocumentsDeAnotacionesNoMogutsASerieFinal;
    @Transient private boolean conteDocumentsPendentsReintentsArxiu;
    @Transient private boolean potTancar;
    @Transient private boolean usuariActualWrite;

    @Getter
	@Setter
    @NoArgsConstructor
    @FieldNameConstants
	public static class ExpedientFilterForm implements Serializable {
		private static final long serialVersionUID = 647178646210565833L;
		private String numero;
        private String nom;
        private ExpedientEstatEnumDto estat = ExpedientEstatEnumDto.OBERT;
        private String interessat;
        private ResourceReference<OrganGestorResource, Long> organGestor;
        private ResourceReference<MetaExpedientResource, Long> metaExpedient;
        @ResourceField(onChangeActive = true)
        private LocalDateTime dataCreacioInici = LocalDateTime.now().withMonth(LocalDateTime.now().getMonth().getValue()-3);
        @ResourceField(onChangeActive = true)
        private LocalDateTime dataCreacioFinal;

        private String numeroRegistre;
        private ResourceReference<GrupResource, Long> grup;
        @ResourceField(onChangeActive = true)
        private ResourceReference<UsuariResource, String> agafatPer;

        @ResourceField(onChangeActive = true)
        private Boolean agafat;
        private Boolean pendentFirmar;
        private Boolean seguit;
	}
}