package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.back.helper.RequestSessionHelper;
import es.caib.ripea.service.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.ripea.service.intf.dto.IntegracioFiltreDto;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Getter
@Setter
public class IntegracioFiltreCommand {

    private String entitatCodi;
	private Date dataInici;
	private Date dataFi;
	private IntegracioAccioTipusEnumDto tipus;
	private String descripcio;
	private IntegracioAccioEstatEnumDto estat;

    public static IntegracioFiltreCommand asCommand(IntegracioFiltreDto dto) {
        return dto != null ? ConversioTipusHelper.convertir(dto, IntegracioFiltreCommand.class ) : null;
    }

    public IntegracioFiltreDto asDto() {
        return ConversioTipusHelper.convertir(this, IntegracioFiltreDto.class);
    }

    public static IntegracioFiltreCommand getFiltreCommand(HttpServletRequest request, String filtre) {

        IntegracioFiltreCommand command = (IntegracioFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(request, filtre);
        if (command == null) {
            command = new IntegracioFiltreCommand();
            RequestSessionHelper.actualitzarObjecteSessio(request, filtre, command);
        }
        return command;
    }
}
