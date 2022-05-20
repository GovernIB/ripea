/**
 * 
 */
package es.caib.ripea.war.command;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



import es.caib.ripea.core.api.dto.ImportacioDto;
import es.caib.ripea.core.api.dto.TipusDestiEnumDto;
import es.caib.ripea.core.api.dto.TipusImportEnumDto;
import es.caib.ripea.core.api.dto.TipusRegistreEnumDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.validation.Importacio;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment d'importació de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@Importacio
public class ImportacioCommand {

	private TipusImportEnumDto tipusImportacio;
	private String codiEni;
	private String numeroRegistre;
	private String dataPresentacio;	
	private TipusDestiEnumDto destiTipus;
	private String carpetaNom;
	protected Long pareId;
	
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
		if (command.getTipusImportacio().equals(TipusImportEnumDto.NUMERO_REGISTRE)) {
			importacioDto.setDataPresentacioFormatted(convertToDateViaSqlTimestamp(command.getDataPresentacio()));
			importacioDto.setTipusRegistre(TipusRegistreEnumDto.ENTRADA);
		}
		return importacioDto;
	}
	
	private static Date convertToDateViaSqlTimestamp(String dataPresentacioStr) throws ParseException {
//		dateToConvert.toString("dd/MM/yyyy HH:mm:ss")
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dataPresentacioStr);
	}
}
