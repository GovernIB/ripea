package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Estructura de dades d'un signant
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class PortafirmesFluxSignerDto implements Serializable {

	private String llinatges;
	private String nif;
	private String nom;
	
	private boolean obligat;
	
	private List<PortafirmesFluxReviserDto> revisors = new ArrayList<PortafirmesFluxReviserDto>();


	private static final long serialVersionUID = 5066900852184560484L;

}
