package es.caib.ripea.war.command;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class TascaDelegarCommand {

	
	@NotEmpty
	private String delegatCodi;
	private String comentari;
	
	public interface Create {}
	public interface Update {}


}
