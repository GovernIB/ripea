package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
		quickFilterFields = { "numero", "nom" },
		artifacts = {
				@ResourceConfigArtifact(
						type = ResourceArtifactType.FILTER,
						code = ExpedientResource.FILTER_CODE,
						formClass = ExpedientResource.ExpedientFilterForm.class)
		})
public class ExpedientResource extends NodeResource {

	public static final String FILTER_CODE = "EXPEDIENT_FILTER";

	@NotNull
	private ExpedientEstatEnumDto estat;
	@NotNull
	@Size(max = 46)
	private String ntiClasificacionSia;	
	@NotNull
	private Date ntiFechaApertura;
	@NotNull
	private ResourceReference<OrganGestorResource, Long> organGestor;
	@NotNull
	private int any;
	@NotNull
	private long sequencia;
	@NotNull
	@Size(max = 256)
	private String codi;
	@Size(max = 64)
	private String numero;

	@NotNull
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
	private String ntiVersion;
	@NotNull
	@Size(max = 52)
	private String ntiIdentificador;
	@NotNull
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
	private PrioritatEnumDto prioritat;
	@Size(max = 1024)
	private String prioritatMotiu;

    private String interessatsResum;
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
        private LocalDateTime dataCreacioInici;
        private LocalDateTime dataCreacioFinal;

        private String numeroRegistre;
        private ResourceReference<GrupResource, Long> grup;
        private ResourceReference<UsuariResource, String> agafatPer;

        private Boolean agafat;
        private Boolean pendentFirmar;
	}
}