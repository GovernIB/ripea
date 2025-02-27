/**
 * 
 */
package es.caib.ripea.back.command;

import org.apache.commons.lang3.builder.ToStringBuilder;
import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * Command per al expedient peticio rebutjar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientPeticioRebutjarCommand {
	
	private Long id;
	@NotEmpty @Size(max=1024)
	private String observacions;

	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(
			String observacions) {
		this.observacions = observacions != null ? observacions.trim() : null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(
				this);
	}

}
