package es.caib.ripea.war.command;

import java.util.List;

import es.caib.ripea.core.api.dto.PinbalServeiDocPermesEnumDto;
import es.caib.ripea.core.api.dto.PinbalServeiDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PinbalServeiCommand {

    private Long id;
	private List<PinbalServeiDocPermesEnumDto> pinbalServeiDocsPermesos;
    private String codi;
    private String nom;
    private boolean actiu;

	public static PinbalServeiCommand asCommand(PinbalServeiDto dto) {
		PinbalServeiCommand command = ConversioTipusHelper.convertir(dto, PinbalServeiCommand.class);
		return command;
    }
	
    public static PinbalServeiDto asDto(PinbalServeiCommand command) {
        return ConversioTipusHelper.convertir(command, PinbalServeiDto.class);
    }
}