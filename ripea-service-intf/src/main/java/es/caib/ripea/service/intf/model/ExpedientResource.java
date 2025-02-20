package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseResource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Informació d'una aplicació a monitoritzar.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "codi", "nom" })
public class ExpedientResource extends BaseResource<Long> {

	@NotNull
	@Size(max = 16)
	private String codi;
	@NotNull
	@Size(max = 100)
	private String nom;
	@Size(max = 1000)
	private String descripcio;
	@NotNull
	@Size(max = 200)
	private String infoUrl;
	@NotNull
	private Integer infoInterval = 1;
	@Setter(AccessLevel.NONE)
	private LocalDateTime infoData;
	@NotNull
	@Size(max = 200)
	private String salutUrl;
	@NotNull
	private Integer salutInterval = 1;
	@Size(max = 10)
	@Setter(AccessLevel.NONE)
	private String versio;

}
