package es.caib.ripea.war.command;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class TascaReassignarCommand {

	
	@NotEmpty
	private List<String> responsablesCodi;
	
	
	public interface Create {}
	public interface Update {}


}
