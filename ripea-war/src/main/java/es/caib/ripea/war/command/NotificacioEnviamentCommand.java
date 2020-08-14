package es.caib.ripea.war.command;

import es.caib.ripea.core.api.dto.ServeiTipusEnumDto;
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