package es.caib.ripea.core.api.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ImportacioDto {

	private TipusImportEnumDto tipusImportacio;
	private String codiEni;
	private String numeroRegistre;
	private TipusRegistreEnumDto tipusRegistre;
	private Date dataPresentacioFormatted;
	private TipusDestiEnumDto destiTipus;
	private String carpetaNom;
	
}
