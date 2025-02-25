package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "codi", "nom" }, descriptionField = "nom")
public class EntitatResource extends BaseResource<Long> {

	@NotNull
	@Size(max = 64)
	private String codi;
	@NotNull
	@Size(max = 256)
	private String nom;
	@Size(max = 1024)
	private String descripcio;
	@NotNull
	@Size(max = 9)
	private String cif;
	@NotNull
	@Size(max = 9)
	private String unitatArrel;
	private boolean activa;
	@Size(max = 7)
	private String capsaleraColorFons;
	@Size(max = 7)
	private String capsaleraColorLletra;
	Date dataSincronitzacio;
	Date dataActualitzacio;
	private boolean permetreEnviamentPostal;
}