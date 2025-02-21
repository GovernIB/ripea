package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Informació d'una aplicació a monitoritzar.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "numero", "nom" })
public class ExpedientResource extends BaseResource<Long> {

	@NotNull
	@Size(max = 256)
	private String nom;
	private int any;
	private long sequencia;
	@NotNull
	@Size(max = 256)
	private String codi;
	@NotNull
	@Size(max = 64)
	private String numero;

	@NotNull
	private ResourceReference<MetaExpedientResource, Long> metaExpedient;
	@NotNull
	private ResourceReference<EntitatResource, Long> entitat;
	protected ResourceReference<UsuariResource, String> agafatPer;
	private ResourceReference<ExpedientEstatResource, Long> estatAdditional;
	private ResourceReference<GrupResource, Long> grup;
	private ResourceReference<OrganGestorResource, Long> organGestor;

	@NotNull
	private ExpedientEstatEnumDto estat;

	// Tancat
	private Date tancatData;
	private String tancatMotiu;
	private Date tancatProgramat;

	// Esborrat
	private int esborrat;
	private Date esborratData;

	// Arxiu
	@Size(max = 36)
	private String arxiuUuid;
	private Date arxiuDataActualitzacio;
	private Date arxiuIntentData;
	private int arxiuReintents;
	private boolean arxiuPropagat;

	// Registre
	@Size(max = 80)
	private String numeroRegistre;
	@Size(max = 4000)
	protected String registresImportats;

	// NTI
	@NotNull
	@Size(max = 5)
	protected String ntiVersion;
	@NotNull
	@Size(max = 52)
	protected String ntiIdentificador;
	@NotNull
	@Size(max = 9)
	protected String ntiOrgano;
	@NotNull
	protected Date ntiFechaApertura;
	@NotNull
	@Size(max = 6)
	protected String ntiClasificacionSia;

	// Sistra
	@Size(max = 16)
	protected String sistraBantelNum;
	protected boolean sistraPublicat;
	@Size(max = 9)
	protected String sistraUnitatAdministrativa;
	@Size(max = 100)
	protected String sistraClau;

	// Prioritat
	private PrioritatEnumDto prioritat;
	@Size(max = 1024)
	private String prioritatMotiu;

}
