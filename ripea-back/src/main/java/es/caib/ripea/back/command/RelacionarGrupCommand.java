/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.MetaExpedientTascaDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;


@Getter
@Setter
public class RelacionarGrupCommand {

	@NotNull
    private Long grupId;
    private Long organId;
    private boolean perDefecte;

    public static RelacionarGrupCommand asCommand(MetaExpedientTascaDto dto) {
        RelacionarGrupCommand command = ConversioTipusHelper.convertir(dto,
                RelacionarGrupCommand.class);
        return command;
    }

    public static MetaExpedientTascaDto asDto(RelacionarGrupCommand command) {
        return ConversioTipusHelper.convertir(command, MetaExpedientTascaDto.class);
    }



}
