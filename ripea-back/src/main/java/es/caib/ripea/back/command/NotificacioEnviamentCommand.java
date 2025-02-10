package es.caib.ripea.back.command;

import es.caib.ripea.service.intf.dto.ServeiTipusEnumDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificacioEnviamentCommand {

    private InteressatCommand titular;
    private InteressatCommand destinatari;
    private ServeiTipusEnumDto serveiTipusEnum;
    private boolean entregaPostal;

}