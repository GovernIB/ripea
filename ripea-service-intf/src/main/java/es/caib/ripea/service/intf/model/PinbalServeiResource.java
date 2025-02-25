package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "codi", "nom" }, descriptionField = "nom")
public class PinbalServeiResource extends BaseResource<Long> {
	private String codi;
	private String nom;
	private boolean pinbalServeiDocPermesDni;
	private boolean pinbalServeiDocPermesNif;
	private boolean pinbalServeiDocPermesCif;
	private boolean pinbalServeiDocPermesNie;
	private boolean pinbalServeiDocPermesPas;
	private boolean actiu;
}
