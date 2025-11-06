package es.caib.ripea.service.intf.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe del model de dades que representa un grup d'interessats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class InteressatGrupDto {

	private String nom;
	private String descripcio;
	
	private List<InteressatDto> interessats;
	
}
