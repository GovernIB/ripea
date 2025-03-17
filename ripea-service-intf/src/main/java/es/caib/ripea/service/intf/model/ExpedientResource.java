package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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
						type = ResourceArtifactType.FILTER,
						code = ExpedientResource.FILTER_CODE,
						formClass = ExpedientResource.ExpedientFilterForm.class)
		})
public class ExpedientResource extends NodeResource {

	public static final String PERSPECTIVE_COUNT = "COUNT";
	public static final String PERSPECTIVE_INTERESSATS_CODE = "INTERESSATS_RESUM";
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

    @Transient private List<InteressatResource> interessats;
    @Transient private int numComentaris;
    @Transient private int numSeguidors;
    @Transient private int numContingut;
    @Transient private int numDades;
    @Transient private int numInteressats;
    @Transient private int numRemeses;
    @Transient private int numPublicacions;
    @Transient private int numAnotacions;
    @Transient private int numVersions;
    @Transient private int numTasques;
    @Transient private boolean disableOrganGestor = false;
    public String getTipusStr() {
        return this.getMetaExpedient() != null ? this.getMetaExpedient().getDescription() + " - " + ntiClasificacionSia : null;
    }

    @Getter
	@Setter
	@NoArgsConstructor
	public static class ExpedientFilterForm implements Serializable {
        private String numero;
        private String nom;
        private ExpedientEstatEnumDto estat = ExpedientEstatEnumDto.OBERT;
        private String interessat;
        private ResourceReference<OrganGestorResource, Long> organGestor;
        private ResourceReference<MetaExpedientResource, Long> metaExpedient;
        private LocalDateTime dataCreacioInici = LocalDateTime.now();
        private LocalDateTime dataCreacioFinal;

        private String numeroRegistre;
        private ResourceReference<GrupResource, Long> grup;
        private ResourceReference<UsuariResource, String> agafatPer;

        private Boolean agafat;
        private Boolean pendentFirmar;
	}
}