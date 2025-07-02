package es.caib.ripea.service.intf.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;

public class DominiResource extends BaseAuditableResource<Long> {

	@NotNull
	@Size(max = 64)
	private String codi;
	@NotNull
	@Size(max = 256)
	private String nom;
	@Size(max = 256)
	private String descripcio;
	@NotNull
	@Size(max = 256)
	private String consulta;
	@NotNull
	@Size(max = 256)
	private String cadena;
	@NotNull
	@Size(max = 256)
	private String contrasenya;
	@NotNull
	private ResourceReference<EntitatResource, Long> entitat;
	
	private static final long serialVersionUID = -6179980025160325170L;
}