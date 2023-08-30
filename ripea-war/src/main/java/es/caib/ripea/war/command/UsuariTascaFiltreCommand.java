/**
 * 
 */
package es.caib.ripea.war.command;

import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.api.dto.UsuariTascaFiltreDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class UsuariTascaFiltreCommand {


	private TascaEstatEnumDto estat;
	
	
	
	public static UsuariTascaFiltreCommand asCommand(UsuariTascaFiltreDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				UsuariTascaFiltreCommand.class);
	}
	
	public static UsuariTascaFiltreDto asDto(UsuariTascaFiltreCommand command){
		UsuariTascaFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				UsuariTascaFiltreDto.class);
		return dto;
	}

	public void setEstat(TascaEstatEnumDto estat) {
		this.estat = estat;
	}
	

}
