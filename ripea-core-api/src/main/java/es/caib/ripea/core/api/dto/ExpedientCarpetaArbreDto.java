package es.caib.ripea.core.api.dto;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpedientCarpetaArbreDto extends AuditoriaDto {

	private Long id;
	private String nom;
	private ExpedientCarpetaArbreDto pare;
	private Set<ExpedientCarpetaArbreDto> fills = new HashSet<ExpedientCarpetaArbreDto>();
	private ExpedientDto expedient;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public boolean equals(Object object) {
		ExpedientCarpetaArbreDto carpeta = (ExpedientCarpetaArbreDto)object;
		return this.getId().equals(carpeta.getId());
	}

}
