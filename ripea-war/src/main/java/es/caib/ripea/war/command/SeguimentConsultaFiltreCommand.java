/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Date;

import es.caib.ripea.core.api.dto.ConsultaPinbalEstatEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentPinbalServeiEnumDto;
import es.caib.ripea.core.api.dto.SeguimentConsultaFiltreDto;
import es.caib.ripea.core.api.dto.SeguimentFiltreDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class SeguimentConsultaFiltreCommand {


	private Long expedientId;
	private Long metaExpedientId;
	private MetaDocumentPinbalServeiEnumDto servei;
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
