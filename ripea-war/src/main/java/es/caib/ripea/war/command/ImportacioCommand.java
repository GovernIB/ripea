/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.ImportacioDto;
import es.caib.ripea.core.api.dto.TipusRegistreEnumDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment d'importació de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ImportacioCommand {

	@NotEmpty
	private String numeroRegistre;
	
	protected Long pareId;
	
	public String getNumeroRegistre() {
		return numeroRegistre;
	}
	public Long getPareId() {
		return pareId;
	}
	public void setPareId(Long pareId) {
		this.pareId = pareId;
	}
	public void setNumeroRegistre(String numeroRegistre) {
		this.numeroRegistre = numeroRegistre;
	}
	public static ImportacioCommand asCommand(ImportacioDto dto) {
		ImportacioCommand command = ConversioTipusHelper.convertir(
				dto,
				ImportacioCommand.class);
		return command;
	}
	public static ImportacioDto asDto(ImportacioCommand command) {
		ImportacioDto importacioDto = ConversioTipusHelper.convertir(
				command,
				ImportacioDto.class);
		importacioDto.setTipusRegistre(TipusRegistreEnumDto.ENTRADA);
		return importacioDto;
	}
}
