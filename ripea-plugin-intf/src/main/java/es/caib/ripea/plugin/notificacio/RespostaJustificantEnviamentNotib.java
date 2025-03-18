/**
 *
 */
package es.caib.ripea.plugin.notificacio;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació retornada per la consulta de justificant d'un enviament.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RespostaJustificantEnviamentNotib {

	private boolean error;
	private Date errorData;
	private String errorDescripcio;
	private byte[] justificant;
	
	
}
