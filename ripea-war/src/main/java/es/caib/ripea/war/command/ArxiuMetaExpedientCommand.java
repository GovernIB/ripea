/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.NotNull;

import es.caib.ripea.core.api.dto.MetaExpedientDto;
/**
 * Command per a la relació arxiu-metaExpedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuMetaExpedientCommand {

	@NotNull(groups = {Create.class, Update.class})
	private Long metaExpedientId;

	public Long getMetaExpedientId() {
		return metaExpedientId;
	}
	public void setMetaExpedientId(Long metaExpedientId) {
		this.metaExpedientId = metaExpedientId;
	}
	
	public static ArxiuMetaExpedientCommand asCommand(MetaExpedientDto dto) {
		ArxiuMetaExpedientCommand command = new ArxiuMetaExpedientCommand();
		command.setMetaExpedientId(dto.getId());
		return command;
	}
	public static MetaExpedientDto asDto(ArxiuMetaExpedientCommand command) {
		MetaExpedientDto dto = new MetaExpedientDto();
		dto.setId(command.getMetaExpedientId());
		return dto;
	}

	public interface Create {}
	public interface Update {}
}
