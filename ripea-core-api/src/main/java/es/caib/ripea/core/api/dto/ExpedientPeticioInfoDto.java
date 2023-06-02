/**
 * 
 */
package es.caib.ripea.core.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExpedientPeticioInfoDto {
	

	public ExpedientPeticioInfoDto(
			String identificador,
			String clauAcces,
			ExpedientPeticioEstatEnumDto estat) {
		this.identificador = identificador;
		this.clauAcces = clauAcces;
		this.estat = estat;
	}
	private String identificador;
	private String clauAcces;
	private ExpedientPeticioEstatEnumDto estat;


	

}
