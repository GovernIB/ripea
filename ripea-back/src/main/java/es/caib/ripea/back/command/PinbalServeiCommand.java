package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.PinbalServeiDocPermesEnumDto;
import es.caib.ripea.service.intf.dto.PinbalServeiDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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