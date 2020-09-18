/**
 *
 */
package es.caib.ripea.plugin.notificacio;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació retornada per la consulta de l'estat d'un enviament.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RespostaConsultaInfoRegistre {

	private Date dataRegistre;
	private int numRegistre;
	private String numRegistreFormatat;
	private byte[] justificant;
	private boolean error;
	private Date errorData;
	private String errorDescripcio;
	
}
