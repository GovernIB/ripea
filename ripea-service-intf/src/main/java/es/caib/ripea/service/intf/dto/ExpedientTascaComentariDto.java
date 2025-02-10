package es.caib.ripea.service.intf.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Informació del comentari d'una tasca.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ExpedientTascaComentariDto extends AuditoriaDto {

	private Long id;
	private String text;

}
