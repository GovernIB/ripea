package es.caib.ripea.war.command;

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

	public String getMotiu() {
		return motiu;
	}

	public void setMotiu(String motiu) {
		this.motiu = motiu;
	}


}