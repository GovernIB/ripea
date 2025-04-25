package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.CrearReglaDistribucioEstatEnumDto;
import es.caib.ripea.service.intf.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.service.intf.dto.TipusClassificacioEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Informació d'una aplicació a monitoritzar.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@FieldNameConstants
@NoArgsConstructor
@ResourceConfig(
		quickFilterFields = { "codi", "nom" },
		descriptionField = "nom"
)
public class MetaExpedientResource extends MetaNodeResource {

	@NotNull
	@Size(max = 64)
	private String codi;
	@NotNull
	@Size(max = 256)
	private String nom;
	@Size(max = 4000)
	private String descripcio;
	private boolean actiu = true;
	@NotNull
	@Size(max = 64)
	private String codiPropi;
	@NotNull
	@Size(max = 3)
	private TipusClassificacioEnumDto tipusClassificacio;
	@NotNull
	@Size(max = 30)
	private String classificacio;
	@NotNull
	@Size(max = 30)
	private String serieDocumental;
	@Size(max = 100)
	private String expressioNumero;
	private boolean notificacioActiva;
	private boolean permetMetadocsGenerals;
	private boolean gestioAmbGrupsActiva;
	private boolean permisDirecte = false;
	@Size(max = 8)
	private MetaExpedientRevisioEstatEnumDto revisioEstat;
	@Size(max = 1024)
	private String revisioComentari;
	@Size(max = 10)
	private CrearReglaDistribucioEstatEnumDto crearReglaDistribucioEstat;
	@Size(max = 1024)
	private String crearReglaDistribucioError;
	private boolean organNoSincronitzat;
	private boolean interessatObligatori;

	private ResourceReference<EntitatResource, Long> entitat;
	private ResourceReference<MetaExpedientResource, Long> pare;
//	private ResourceReference<EntitatResource, Long> entitatPropia;
	private ResourceReference<OrganGestorResource, Long> organGestor;
	private ResourceReference<GrupResource, Long> grupPerDefecte;

}
