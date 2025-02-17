package es.caib.ripea.back.command;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnviarDocumentCommand {

	private String email;
	private List<String> responsablesCodi;


	public interface Create {}
	public interface Update {}

	public List<String> getResponsablesCodi() {
        if (responsablesCodi == null) {
            responsablesCodi = new ArrayList<>();
        }
        return responsablesCodi;
    }
}
