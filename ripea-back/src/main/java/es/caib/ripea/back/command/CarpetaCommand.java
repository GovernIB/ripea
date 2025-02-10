/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.CarpetaDto;

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
