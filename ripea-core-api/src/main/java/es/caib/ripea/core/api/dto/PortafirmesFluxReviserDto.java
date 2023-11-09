package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Estructura de dades d'un revisor
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class PortafirmesFluxReviserDto implements Serializable {

	private String llinatges;
	private String nif;
	private String nom;
	
	private static final long serialVersionUID = 7596085767014933225L;

}
