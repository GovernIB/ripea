/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Command per al expedient peticio rebutjar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientPeticioIncorporarCommand {
	
	private Long id;

	
	
	@NotEmpty @Size(max=1024)
	private String observacions;

	
	public Long getId() {
		return id;
	}

	public void setId(
			Long id) {
		this.id = id;
	}

	public String getObservacions() {
		return observacions;
	}

	public void setObservacions(
			String observacions) {
		this.observacions = observacions;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(
				this);
	}

}
