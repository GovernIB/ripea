/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.service.intf.utils.Utils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;


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
