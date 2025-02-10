/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Informació d'un domini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ResultatDominiDto {
	
	private int totalElements;
	private List<ResultatConsultaDto> resultat;
}
