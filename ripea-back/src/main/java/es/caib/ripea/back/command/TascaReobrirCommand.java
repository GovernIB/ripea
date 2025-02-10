package es.caib.ripea.back.command;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class TascaReobrirCommand {

	
	@NotEmpty
	private List<String> responsablesCodi;
	private String motiu;
	
	public interface Create {}
	public interface Update {}


}
