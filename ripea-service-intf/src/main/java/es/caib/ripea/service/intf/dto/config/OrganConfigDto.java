package es.caib.ripea.service.intf.dto.config;



import lombok.Data;

@Data
public class OrganConfigDto {
	
	public OrganConfigDto() {
	}

	private String key;
	private String value;
	/**
	 * Etiqueta traduida del valor, emplenada nomes a la capa web. Si el valor no te
	 * traduccio definida no s'emplena i la vista mostra el valor tal qual.
	 */
	private String valueLabel;
	private Long organGestorId;
	
	private String organGestorCodiNom;
	
	private String typeCode;
	
    private boolean jbossProperty;

	private boolean configurableOrgansDescendents;

}
