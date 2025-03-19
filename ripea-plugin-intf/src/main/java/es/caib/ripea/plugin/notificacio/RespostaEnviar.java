/**
 * 
 */
package es.caib.ripea.plugin.notificacio;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació retornada per l'alta d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RespostaEnviar {
	private boolean error;
	private String errorDescripcio;
	private String identificador;
	private NotificacioEstat estat;
	private List<EnviamentReferencia> referencies;
}
