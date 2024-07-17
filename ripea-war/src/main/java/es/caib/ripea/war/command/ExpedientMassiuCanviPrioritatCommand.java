/**
 * 
 */
package es.caib.ripea.war.command;

import es.caib.ripea.core.api.dto.PrioritatEnumDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command per canvi massiu d'estat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpedientMassiuCanviPrioritatCommand {

	private PrioritatEnumDto prioritat = PrioritatEnumDto.NORMAL;

}
