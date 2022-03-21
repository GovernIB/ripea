package es.caib.ripea.war.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Command per tancament massiu d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientMassiuTancamentCommand {

	@NotEmpty @Size(max=1024)
	private String motiu;
	private List<ExpedientTancarCommand> expedientsTancar = new ArrayList<>();
	
	public String getMotiu() {
		return motiu;
	}
	public void setMotiu(String motiu) {
		this.motiu = motiu != null ? motiu.trim() : null;
	}
	public List<ExpedientTancarCommand> getExpedientsTancar() {
		return expedientsTancar;
	}
	public void setExpedientsTancar(List<ExpedientTancarCommand> expedientsTancar) {
		this.expedientsTancar = expedientsTancar;
	}


}
