/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.utils.Utils;


public class FirmaSimpleWebCommand {

	@NotEmpty @Size(max=256)
	private String motiu;

	public String getMotiu() {
		return motiu;
	}
	public void setMotiu(String motiu) {
		this.motiu = Utils.trim(motiu);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
