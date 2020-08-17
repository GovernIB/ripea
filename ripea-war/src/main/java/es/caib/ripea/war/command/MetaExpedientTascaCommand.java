/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Date;

import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.validation.CodiMetaExpedientTascaNoRepetit;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per a les tasques del meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@CodiMetaExpedientTascaNoRepetit(campId = "id", campCodi = "codi", campEntitatId = "entitatId", campMetaExpedientId = "metaExpedientId")
public class MetaExpedientTascaCommand {

    private Long id;
    @NotEmpty
    private String codi;
    @NotEmpty
    private String nom;
    @NotEmpty
    private String descripcio;
    private String responsable;
    private boolean activa;
    private Date dataLimit;
    private Long estatIdCrearTasca;
    private Long estatIdFinalitzarTasca;

    private Long entitatId;
    private Long metaExpedientId;

    public static MetaExpedientTascaCommand asCommand(MetaExpedientTascaDto dto) {
        MetaExpedientTascaCommand command = ConversioTipusHelper.convertir(dto,
                MetaExpedientTascaCommand.class);
        return command;
    }

    public static MetaExpedientTascaDto asDto(MetaExpedientTascaCommand command) {
        return ConversioTipusHelper.convertir(command, MetaExpedientTascaDto.class);
    }

    public interface Create {
    }

    public interface Update {
    }

}
