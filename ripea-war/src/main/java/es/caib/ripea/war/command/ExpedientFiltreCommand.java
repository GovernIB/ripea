/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.ripea.core.api.dto.ExpedientFiltreDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al filtre d'expedients dels arxius.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ExpedientFiltreCommand {

	private Long arxiuId;
	private Long metaExpedientId;
	private String metaExpedientDominiCodi;
	private String nom;
	private Date dataCreacioInici;
	private Date dataCreacioFi;
	private String numero;
	private Long expedientEstatId;
	private Date dataTancatInici;
	private Date dataTancatFi;
	private boolean meusExpedients;

	private Long tipusId;
	private String interessat;
	private String metaExpedientDominiValor;


	public static ExpedientFiltreCommand asCommand(ExpedientFiltreDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				ExpedientFiltreCommand.class);
	}
	public static ExpedientFiltreDto asDto(ExpedientFiltreCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				ExpedientFiltreDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
