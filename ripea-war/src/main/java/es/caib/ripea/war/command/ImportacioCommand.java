/**
 * 
 */
package es.caib.ripea.war.command;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Command per al manteniment d'importació de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ImportacioCommand {

	@NotEmpty
	private String numeroRegistre;
	protected Long pareId;
	
	public String getNumeroRegistre() {
		return numeroRegistre;
	}
	public Long getPareId() {
		return pareId;
	}
	public void setPareId(Long pareId) {
		this.pareId = pareId;
	}
	public void setNumeroRegistre(String numeroRegistre) {
		this.numeroRegistre = numeroRegistre;
	}
}
