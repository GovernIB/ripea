/**
 * 
 */
package es.caib.ripea.war.command;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDateTime;

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
	@NotNull
	private String dataPresentacio;
	
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
	public String getDataPresentacio() {
		return dataPresentacio;
	}
	public void setDataPresentacio(String dataPresentacio) {
		this.dataPresentacio = dataPresentacio;
	}
	public static ImportacioCommand asCommand(ImportacioDto dto) {
		ImportacioCommand command = ConversioTipusHelper.convertir(
				dto,
				ImportacioCommand.class);
		return command;
	}
	public static ImportacioDto asDto(ImportacioCommand command) throws ParseException {
		ImportacioDto importacioDto = ConversioTipusHelper.convertir(
				command,
				ImportacioDto.class);
		importacioDto.setDataPresentacioFormatted(convertToDateViaSqlTimestamp(command.getDataPresentacio()));
		importacioDto.setTipusRegistre(TipusRegistreEnumDto.ENTRADA);
		return importacioDto;
	}
	
	private static Date convertToDateViaSqlTimestamp(String dataPresentacioStr) throws ParseException {
//		dateToConvert.toString("dd/MM/yyyy HH:mm:ss")
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dataPresentacioStr);
	}
}
