/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.service.intf.dto.InteressatAssociacioAccioEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Informació d'un registre annex.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class RegistreInteressatsCommand {

	private String interessatDocNumero;
	private InteressatAssociacioAccioEnum accio;

}
