package es.caib.ripea.service.intf.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;

import es.caib.ripea.service.intf.registre.RegistreInteressat;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ImportacioRegistreParamsDto {

	private TipusImportEnumDto tipusImportacio;
	private String codiEni;
	private String numeroRegistre;
	private TipusRegistreEnumDto tipusRegistre;
	private Date dataPresentacioFormatted;
	private Set<ArbreJsonDto> estructuraCarpetes;
	private String destiId;
	private List<RegistreInteressat> interessats;
	
}
