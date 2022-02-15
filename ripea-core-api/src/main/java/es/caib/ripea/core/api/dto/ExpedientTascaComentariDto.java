package es.caib.ripea.core.api.dto;

import lombok.Data;

/**
 * Informació del comentari d'una tasca.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ExpedientTascaComentariDto extends AuditoriaDto {

	private Long id;
	private String text;

}
