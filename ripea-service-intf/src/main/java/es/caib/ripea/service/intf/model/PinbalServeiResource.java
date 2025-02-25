package es.caib.ripea.service.intf.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "codi", "nom" }, descriptionField = "nom")
public class PinbalServeiResource extends BaseAuditableResource<Long> {
	
	@NotNull
	@Size(max = 64)
	private String codi;
	@Size(max = 256)
	private String nom;
	@NotNull
	private boolean pinbalServeiDocPermesDni;
	@NotNull
	private boolean pinbalServeiDocPermesNif;
	@NotNull
	private boolean pinbalServeiDocPermesCif;
	@NotNull
	private boolean pinbalServeiDocPermesNie;
	@NotNull
	private boolean pinbalServeiDocPermesPas;
	private boolean actiu;
}