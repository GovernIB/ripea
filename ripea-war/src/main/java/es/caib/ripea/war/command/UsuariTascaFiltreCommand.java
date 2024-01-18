/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Date;

import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.api.dto.UsuariTascaFiltreDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class UsuariTascaFiltreCommand {


	private TascaEstatEnumDto estat;
	private Long expedientId;
	private Date dataInici;
	private Date dataFi;
	private Date dataLimitInici;
	private Date dataLimitFi;
	
	
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

	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}

	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}

	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}

	public void setDataLimitInici(Date dataLimitInici) {
		this.dataLimitInici = dataLimitInici;
	}

	public void setDataLimitFi(Date dataLimitFi) {
		this.dataLimitFi = dataLimitFi;
	}
	
	
	

}
