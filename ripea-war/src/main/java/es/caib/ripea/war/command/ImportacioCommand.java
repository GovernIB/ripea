/**
 * 
 */
package es.caib.ripea.war.command;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.ImportacioDto;
import es.caib.ripea.core.api.dto.TipusDestiEnumDto;
import es.caib.ripea.core.api.dto.TipusRegistreEnumDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.validation.Importacio;

/**
 * Command per al manteniment d'importació de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Importacio
public class ImportacioCommand {

	@NotEmpty
	private String numeroRegistre;
	@NotEmpty
	private String dataPresentacio;
	
	private TipusDestiEnumDto destiTipus;
	
	private String carpetaNom;
	
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
		this.numeroRegistre = numeroRegistre != null ? numeroRegistre.trim() : null;
	}
	public String getDataPresentacio() {
		return dataPresentacio;
	}
	public void setDataPresentacio(String dataPresentacio) {
		this.dataPresentacio = dataPresentacio != null ? dataPresentacio.trim() : null;
	}
	public TipusDestiEnumDto getDestiTipus() {
		return destiTipus;
	}
	public void setDestiTipus(TipusDestiEnumDto destiTipus) {
		this.destiTipus = destiTipus;
	}
	public String getCarpetaNom() {
		return carpetaNom;
	}
	public void setCarpetaNom(String carpetaNom) {
		this.carpetaNom = carpetaNom;
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
