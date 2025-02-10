package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.HashSet;
import java.util.Set;

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
