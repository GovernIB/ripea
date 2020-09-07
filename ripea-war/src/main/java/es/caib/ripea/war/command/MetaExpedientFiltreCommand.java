package es.caib.ripea.war.command;

import es.caib.ripea.core.api.dto.MetaExpedientActiuEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientFiltreDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Data;

@Data
public class MetaExpedientFiltreCommand {

	private String codi;
	private String nom;
	private Long organGestorId;
	private Boolean veureTots = true;
	private MetaExpedientActiuEnumDto actiu;

	public MetaExpedientFiltreDto asDto() {
		return ConversioTipusHelper.convertir(
				this,
				MetaExpedientFiltreDto.class);
	}
}
