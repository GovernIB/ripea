package es.caib.ripea.war.command;

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
		this.usuariCodi = usuariCodi.trim();
	}


	
	public interface Create {}
	public interface Update {}


}
