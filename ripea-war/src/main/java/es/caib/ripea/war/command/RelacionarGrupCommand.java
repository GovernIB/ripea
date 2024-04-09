/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.NotNull;

import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;


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
