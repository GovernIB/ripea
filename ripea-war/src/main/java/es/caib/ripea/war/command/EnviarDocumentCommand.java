package es.caib.ripea.war.command;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class EnviarDocumentCommand {

	@NotEmpty
	private List<String> responsablesCodi;


	public interface Create {}
	public interface Update {}


}
