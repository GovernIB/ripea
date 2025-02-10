package es.caib.ripea.service.intf.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrganismeDto implements Serializable{

	private Long id;
	@EqualsAndHashCode.Include
	private String codi;
	private String nom;
	private String pare;
	private List<String> fills;
	private Boolean sir;
	private OrganEstatEnumDto estat;

	public String getNomComplet() {
		return codi + " - " + nom;
	}

	private static final long serialVersionUID = -3831959843313056718L;

}
