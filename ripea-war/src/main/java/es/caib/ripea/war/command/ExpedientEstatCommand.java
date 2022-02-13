/**
 * 
 */
package es.caib.ripea.war.command;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
public class ExpedientEstatCommand {

	private Long id;
	@NotEmpty
	private String codi;
	@NotEmpty
	private String nom;
	private int ordre;
	private boolean ambColor;
	private String color;
	private Long metaExpedientId;
	private boolean inicial;
	private String responsableCodi;
	private boolean comu;
	
	
	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi != null ? responsableCodi.trim() : null;
	}
	public void setCodi(String codi) {
		this.codi = codi != null ? codi.trim() : null;
	}
	public void setNom(String nom) {
		this.nom = nom != null ? nom.trim() : null;
	}

	public static ExpedientEstatCommand asCommand(ExpedientEstatDto dto) {
		ExpedientEstatCommand command = ConversioTipusHelper.convertir(
				dto,
				ExpedientEstatCommand.class);
		if (command.getColor() == null) {
			command.setColor("#FFFFFF");
		} else {
			command.setAmbColor(true);
		}
		return command;
	}
	public static ExpedientEstatDto asDto(ExpedientEstatCommand command) {
		ExpedientEstatDto dto =  ConversioTipusHelper.convertir(
				command,
				ExpedientEstatDto.class);
		if (!command.isAmbColor()) {
			command.setColor(null);
		}
		return dto;
	}
	
	public interface Create {}
	public interface Update {}

}
