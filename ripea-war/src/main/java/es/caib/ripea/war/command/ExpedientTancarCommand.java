/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Command per a l'acció de tancar expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTancarCommand extends ContenidorCommand {

	protected Long id;
	@NotEmpty @Size(max=1024)
	protected String motiu;

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
		this.motiu = motiu;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
