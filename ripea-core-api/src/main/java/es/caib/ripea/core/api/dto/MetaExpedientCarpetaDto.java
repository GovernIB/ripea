package es.caib.ripea.core.api.dto;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MetaExpedientCarpetaDto extends AuditoriaDto {

	private Long id;
	private String nom;
	private MetaExpedientCarpetaDto pare;
	private Set<MetaExpedientCarpetaDto> fills = new HashSet<MetaExpedientCarpetaDto>();
	private MetaExpedientDto metaExpedient;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
