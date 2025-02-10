package es.caib.ripea.back.command;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientAssignarCommand {

	@NotEmpty
	private String usuariCodi;
	
	
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi != null ? usuariCodi.trim() : null;
	}


	
	public interface Create {}
	public interface Update {}


}
