package es.caib.ripea.plugin.portafirmes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Estructura de dades del plugin d'un signant
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class PortafirmesFluxSigner implements Serializable {

	private String llinatges;
	private String nif;
	private String nom;
	
	private List<PortafirmesFluxReviser> revisers = new ArrayList<PortafirmesFluxReviser>();
	
	private static final long serialVersionUID = 5066900852184560484L;

}
