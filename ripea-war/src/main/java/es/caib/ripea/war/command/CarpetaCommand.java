/**
 * 
 */
package es.caib.ripea.war.command;

import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CarpetaCommand extends ContenidorCommand {

	public static CarpetaCommand asCommand(CarpetaDto dto) {
		CarpetaCommand command = ConversioTipusHelper.convertir(
				dto,
				CarpetaCommand.class);
		if (dto.getPare() != null)
			command.setPareId(dto.getPare().getId());
		return command;
	}
	public static CarpetaDto asDto(CarpetaCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				CarpetaDto.class);
	}

}
