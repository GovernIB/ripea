/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command per a rebutjar anotacions de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreRebutjarCommand {

	@NotNull
	protected Long registreId;
	@Size(max=1024)
	protected String motiu;



	public Long getRegistreId() {
		return registreId;
	}
	public void setRegistreId(Long registreId) {
		this.registreId = registreId;
	}
	public String getMotiu() {
		return motiu;
	}
	public void setMotiu(String motiu) {
		this.motiu = motiu.trim();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
