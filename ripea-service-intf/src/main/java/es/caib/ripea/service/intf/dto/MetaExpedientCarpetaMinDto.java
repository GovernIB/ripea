package es.caib.ripea.service.intf.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MetaExpedientCarpetaMinDto {

	private Long id;
	private String nom;
	private MetaExpedientCarpetaMinDto pare;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
