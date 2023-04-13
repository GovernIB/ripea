/**
 * 
 */
package es.caib.ripea.plugin.notificacio;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació retornada per la consulta de l'estat d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RespostaConsultaEstatNotificacio {
	private NotificacioEstat estat;
	private boolean error;
	private Date errorData;
	private String errorDescripcio;
	
	private Date dataEnviada;
	private Date dataFinalitzada;
}
