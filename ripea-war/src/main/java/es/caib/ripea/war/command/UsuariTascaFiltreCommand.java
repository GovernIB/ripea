/**
 *
 */
package es.caib.ripea.war.command;

import java.util.Date;

import es.caib.ripea.core.api.dto.PrioritatEnumDto;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.api.dto.UsuariTascaFiltreDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
