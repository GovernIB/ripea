package es.caib.ripea.back.command;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

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
