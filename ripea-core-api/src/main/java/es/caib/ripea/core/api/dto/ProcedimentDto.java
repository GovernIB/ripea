package es.caib.ripea.core.api.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProcedimentDto {
	private String codi;
	private String nom;
	private String codiSia;
	private boolean comu;
	private String unitatOrganitzativaCodi;
	private String resum;
	private Long organId;

}
