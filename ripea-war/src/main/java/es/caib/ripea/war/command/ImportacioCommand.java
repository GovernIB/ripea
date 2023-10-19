/**
 * 
 */
package es.caib.ripea.war.command;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.ripea.core.api.dto.ArbreJsonDto;
import es.caib.ripea.core.api.dto.ImportacioDto;
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
	protected Long pareId;
	
	private String estructuraCarpetesJson;
	private String destiId;
	
	public static ImportacioCommand asCommand(ImportacioDto dto) {
		ImportacioCommand command = ConversioTipusHelper.convertir(
				dto,
				ImportacioCommand.class);
		return command;
	}
	public static ImportacioDto asDto(ImportacioCommand command) throws ParseException, JsonMappingException {
		ImportacioDto importacioDto = ConversioTipusHelper.convertir(
				command,
				ImportacioDto.class);
		if (command.getTipusImportacio().equals(TipusImportEnumDto.NUMERO_REGISTRE)) {
			importacioDto.setDataPresentacioFormatted(convertToDateViaSqlTimestamp(command.getDataPresentacio()));
			importacioDto.setTipusRegistre(TipusRegistreEnumDto.ENTRADA);
		}
		
		try {
			if (command.getEstructuraCarpetesJson() != null) {
				ObjectMapper objectMapper = new ObjectMapper();
				List<ArbreJsonDto> listCarpetes = objectMapper.readValue(command.getEstructuraCarpetesJson(), new TypeReference<List<ArbreJsonDto>>(){});
				importacioDto.setEstructuraCarpetes(new HashSet<ArbreJsonDto>(listCarpetes));
			}
		} catch (IOException ex) {
			throw new JsonMappingException("Hi ha hagut un error en la conversió del json de jstree a List<ArbreJsonDto>", ex);
		}
		
		return importacioDto;
	}
	
	public void setEstructuraCarpetesJson(String estructuraCarpetesJson) {
		this.estructuraCarpetesJson = estructuraCarpetesJson != null ? estructuraCarpetesJson.trim() : null;
	}
	
	private static Date convertToDateViaSqlTimestamp(String dataPresentacioStr) throws ParseException {
//		dateToConvert.toString("dd/MM/yyyy HH:mm:ss")
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dataPresentacioStr);
	}
}
