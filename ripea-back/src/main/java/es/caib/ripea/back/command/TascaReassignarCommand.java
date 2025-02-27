package es.caib.ripea.back.command;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;

import java.util.List;

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
