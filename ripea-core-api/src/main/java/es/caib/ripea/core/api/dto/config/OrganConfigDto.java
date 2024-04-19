package es.caib.ripea.core.api.dto.config;



import lombok.Data;

@Data
public class OrganConfigDto {
	
	public OrganConfigDto() {
	}

	private String key;
	private String value;
	private Long organGestorId;
	
	private String organGestorCodiNom;
	
	private String typeCode;
	
    private boolean jbossProperty;

	private boolean configurableOrgansDescendents;

}
