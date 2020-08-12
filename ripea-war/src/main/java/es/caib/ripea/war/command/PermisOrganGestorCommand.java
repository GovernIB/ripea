package es.caib.ripea.war.command;

import es.caib.ripea.core.api.dto.PermisOrganGestorDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PermisOrganGestorCommand extends PermisCommand 
{
    private Long organGestorId;

    public static PermisOrganGestorCommand asCommand(PermisOrganGestorDto dto) {
	PermisOrganGestorCommand permisCommand = ConversioTipusHelper.convertir(dto, PermisOrganGestorCommand.class);

	permisCommand.setSelectAll(false);
	if (permisCommand.isCreate() && permisCommand.isDelete() && permisCommand.isRead() && permisCommand.isWrite())
	    permisCommand.setSelectAll(true);
	permisCommand.setOrganGestorId(dto.getOrganGestor().getId());
	return permisCommand;
    }
}
