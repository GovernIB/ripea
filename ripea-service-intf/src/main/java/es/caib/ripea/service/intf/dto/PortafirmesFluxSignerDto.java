package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
