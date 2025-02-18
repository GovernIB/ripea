/**
 * 
 */
package es.caib.ripea.back.command;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotNull;

/**
 * Command per a acumular expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientAcumularCommand {

	@NotNull
	protected Long expedientId;
	@NotNull
	protected Long expedientAcumulatId;



	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public Long getExpedientAcumulatId() {
		return expedientAcumulatId;
	}
	public void setExpedientAcumulatId(Long expedientAcumulatId) {
		this.expedientAcumulatId = expedientAcumulatId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
