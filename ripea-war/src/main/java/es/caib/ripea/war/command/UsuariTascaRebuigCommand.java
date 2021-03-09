/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Command per al usuari tasca rebuig
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UsuariTascaRebuigCommand {
	
	private Long id;
	
	@NotEmpty @Size(max=1024)
	private String motiu;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMotiu() {
		return motiu;
	}

	public void setMotiu(String motiu) {
		this.motiu = motiu != null ? motiu.trim() : null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(
				this);
	}

}
