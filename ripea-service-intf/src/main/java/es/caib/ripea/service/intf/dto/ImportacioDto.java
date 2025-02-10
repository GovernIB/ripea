package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter @Setter
public class ImportacioDto {

	private TipusImportEnumDto tipusImportacio;
	private String codiEni;
	private String numeroRegistre;
	private TipusRegistreEnumDto tipusRegistre;
	private Date dataPresentacioFormatted;
	private Set<ArbreJsonDto> estructuraCarpetes;
	private String destiId;
	
}
