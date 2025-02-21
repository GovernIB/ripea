package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.OrganEstatEnumDto;
import es.caib.ripea.service.intf.dto.TipusTransicioEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Informació d'una aplicació a monitoritzar.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "codi", "nom" }, descriptionField = "codiINom")
public class OrganGestorResource extends BaseResource<Long> {

	@NotNull
	@Size(max = 64)
	private String codi;
	@Size(max = 1000)
	private String nom; // nomCatala
	@Size(max = 1000)
	private String nomEspanyol;

	private ResourceReference<EntitatResource, Long> entitat;
	private ResourceReference<OrganGestorResource, Long> pare;

	private boolean actiu;
	@Size(max = 10)
	private String cif;
	private boolean utilitzarCifPinbal;
	private boolean permetreEnviamentPostal;
	private boolean permetreEnviamentPostalDescendents;
	@Size(max = 1)
	private OrganEstatEnumDto estat;
	@Size(max = 12)
	private TipusTransicioEnumDto tipusTransicio;

	public String getCodiINom() {
		return codi + " - " + nom;
	}
}
