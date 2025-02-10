package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ExpedientCarpetaArbreDto extends AuditoriaDto {

	private Long id;
	private String nom;
	private ExpedientCarpetaArbreDto pare;
	private List<ExpedientCarpetaArbreDto> fills = new ArrayList<ExpedientCarpetaArbreDto>();
	private ExpedientDto expedient;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (int) (prime * result + id);
		result = prime * result + ((pare == null) ? 0 : pare.hashCode());
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object object) {
		ExpedientCarpetaArbreDto carpeta = (ExpedientCarpetaArbreDto)object;
		return this.getId().equals(carpeta.getId());
	}

}
