/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.validation.CodiMetaExpedientNoRepetit;
import lombok.Data;

/**
 * Command per al manteniment de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@CodiMetaExpedientNoRepetit(campId = "id", campCodi = "codi", campEntitatId = "entitatId")
@Data
public class MetaExpedientCommand {

    private Long id;

    @NotEmpty
    @Size(max = 64)
    private String codi;
    @NotEmpty
    @Size(max = 256)
    private String nom;
    @Size(max = 1024)
    private String descripcio;
    @NotEmpty
    @Size(max = 30)
    private String classificacioSia;
    @NotEmpty
    @Size(max = 30)
    private String serieDocumental;
    @Size(max = 100)
    private String expressioNumero;

    private Long organGestorId;
    
    private boolean notificacioActiva;

    private boolean permetMetadocsGenerals;

    private Long pareId;
    private Long entitatId;

    public static List<MetaExpedientCommand> toEntitatCommands(List<MetaExpedientDto> dtos) {
        List<MetaExpedientCommand> commands = new ArrayList<MetaExpedientCommand>();
        for (MetaExpedientDto dto : dtos) {
            commands.add(ConversioTipusHelper.convertir(dto, MetaExpedientCommand.class));
        }
        return commands;
    }

    public static MetaExpedientCommand asCommand(MetaExpedientDto dto) {
        return ConversioTipusHelper.convertir(dto, MetaExpedientCommand.class);
    }

    public static MetaExpedientDto asDto(MetaExpedientCommand command) {
        return ConversioTipusHelper.convertir(command, MetaExpedientDto.class);
    }

}
