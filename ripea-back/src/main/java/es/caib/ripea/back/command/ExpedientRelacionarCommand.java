/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.command.ExpedientRelacionarCommand.Relacionar;
import es.caib.ripea.back.validation.ExpedientRelacionar;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotNull;

/**
 * Command per a relacionar expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientRelacionar(groups = {Relacionar.class})
public class ExpedientRelacionarCommand {

	@NotNull
	protected Long entitatId;
	@NotNull
	protected Long expedientId;
	@NotNull
	protected Long relacionatId;


	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public Long getRelacionatId() {
		return relacionatId;
	}
	public void setRelacionatId(Long relacionatId) {
		this.relacionatId = relacionatId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public interface Relacionar {}

}
