package es.caib.ripea.back.command;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class MarcarProcessatCommand {

	@NotBlank
	private String motiu;

	public String getMotiu() {
		return motiu;
	}

	public void setMotiu(String motiu) {
		this.motiu = motiu != null ? motiu.trim() : null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}