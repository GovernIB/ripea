package es.caib.ripea.war.command;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class TascaCancelarDelegacioCommand {

	private String comentari;
	
	public interface Create {}
	public interface Update {}


}
