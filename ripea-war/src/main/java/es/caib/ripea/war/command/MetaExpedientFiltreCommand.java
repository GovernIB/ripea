package es.caib.ripea.war.command;

import es.caib.ripea.core.api.dto.MetaExpedientActiuEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientAmbitEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientFiltreDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Data;

@Data
public class MetaExpedientFiltreCommand {

	private String codi;
	private String nom;
	private String classificacioSia;
	private Long organGestorId;
	private MetaExpedientActiuEnumDto actiu;
	private MetaExpedientAmbitEnumDto ambit;
	

	public MetaExpedientFiltreDto asDto() {
		return ConversioTipusHelper.convertir(
				this,
				MetaExpedientFiltreDto.class);
	}
}
