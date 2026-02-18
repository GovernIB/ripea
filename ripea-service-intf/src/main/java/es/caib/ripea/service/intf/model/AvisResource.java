package es.caib.ripea.service.intf.model;

import java.util.Date;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.AvisNivellEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "assumpte", "missatge" }, descriptionField = "assumpte")
public class AvisResource extends BaseAuditableResource<Long> {

	private String assumpte;
	private String missatge;
	private Date dataInici;
	private Date dataFinal;
	private Boolean actiu;
	private AvisNivellEnumDto avisNivell;
	private Boolean avisAdministrador;
	protected ResourceReference<EntitatResource, Long> entitat;
	
	private static final long serialVersionUID = 1624417428355961779L;
}