/**
 * 
 */
package es.caib.ripea.war.command;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpedientPeticioModificarCommand {

	private Long id;
	private String numero;
	private String extracte;
	private Long metaExpedientId;

}
