/**
 *
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.dto.TascaEstatEnumDto;
import es.caib.ripea.service.intf.dto.UsuariTascaFiltreDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class UsuariTascaFiltreCommand {

	private TascaEstatEnumDto[] estats;
	private Long expedientId;
	private Date dataInici;
	private Date dataFi;
	private Date dataLimitInici;
	private Date dataLimitFi;
	private String duracio;

	private String titol;
	private Long metaExpedientId;
	private Long metaExpedientTascaId;
	private PrioritatEnumDto prioritat;

	public static UsuariTascaFiltreCommand asCommand(UsuariTascaFiltreDto dto) {
		return ConversioTipusHelper.convertir(
			dto,
			UsuariTascaFiltreCommand.class);
	}

	public static UsuariTascaFiltreDto asDto(UsuariTascaFiltreCommand command) {
		return ConversioTipusHelper.convertir(
			command,
			UsuariTascaFiltreDto.class);
	}
}
