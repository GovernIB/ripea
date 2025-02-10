package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.ConsultaPinbalEstatEnumDto;
import es.caib.ripea.service.intf.dto.SeguimentConsultaFiltreDto;
import es.caib.ripea.service.intf.dto.SeguimentFiltreDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class SeguimentConsultaFiltreCommand {

	private Long expedientId;
	private Long metaExpedientId;
	private String servei;
	private String createdByCodi;
	private Date dataInici;
	private Date dataFinal;
	private ConsultaPinbalEstatEnumDto estat;
	
	public static SeguimentConsultaFiltreCommand asCommand(SeguimentFiltreDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				SeguimentConsultaFiltreCommand.class);
	}
	
	public static SeguimentConsultaFiltreDto asDto(SeguimentConsultaFiltreCommand command){
		SeguimentConsultaFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				SeguimentConsultaFiltreDto.class);
		return dto;
	}
}