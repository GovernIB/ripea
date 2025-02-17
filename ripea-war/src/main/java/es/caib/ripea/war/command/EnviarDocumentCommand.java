package es.caib.ripea.war.command;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class EnviarDocumentCommand {

	private String email;
	private List<String> responsablesCodi;

    public List<String> getResponsablesCodi() {
        if (responsablesCodi == null) {
            responsablesCodi = new ArrayList<>();
        }
        return responsablesCodi;
    }
}
