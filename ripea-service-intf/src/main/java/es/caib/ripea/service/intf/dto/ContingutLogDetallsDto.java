/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Detalls de log d'una accio realitzada damunt un node.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ContingutLogDetallsDto extends ContingutLogDto {

	private ContingutMovimentDto contingutMoviment;
	private ContingutLogDto pare;
	private String objecteNom;

}
