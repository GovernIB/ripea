/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.databind.JsonMappingException;

import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;

/**
 * Command per revisio de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
public class MetaExpedientRevisioCommand {

	private Long id;
	private MetaExpedientRevisioEstatEnumDto revisioEstat;
	@Size(max = 1024)
	private String revisioComentari;
    


	public static MetaExpedientRevisioCommand asCommand(MetaExpedientDto dto) {
		MetaExpedientRevisioCommand command = ConversioTipusHelper.convertir(dto, MetaExpedientRevisioCommand.class);
		return command;
	}

	public MetaExpedientDto asDto() throws JsonMappingException {
		MetaExpedientDto dto = ConversioTipusHelper.convertir(this, MetaExpedientDto.class);
		return dto;
	}

	public void setRevisioEstat(MetaExpedientRevisioEstatEnumDto revisioEstat) {
		this.revisioEstat = revisioEstat;
	}

	public void setRevisioComentari(String revisioComentari) {
		this.revisioComentari = revisioComentari != null ? revisioComentari.trim() : null;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	

}
