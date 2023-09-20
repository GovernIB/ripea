/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ExecucioMassivaContingutDto extends AuditoriaDto {


	private Date dataInici;
	private Date dataFi;
	private ExecucioMassivaEstatDto estat;
	private String error;
	private int ordre;
	private ExecucioMassivaDto execucioMassiva;
	private Long elementId;
	private String elementNom;
	private ElementTipusEnumDto elementTipus;
	private Throwable throwable;
	
	
	
	public ExecucioMassivaContingutDto() {
	}

	public ExecucioMassivaContingutDto(
			Date dataInici,
			Date dataFi,
			Long elementId,
			Throwable throwable) {
		this.dataInici = dataInici;
		this.dataFi = dataFi;
		this.estat = throwable != null ? ExecucioMassivaEstatDto.ESTAT_ERROR : ExecucioMassivaEstatDto.ESTAT_FINALITZAT;
		this.elementId = elementId;
		this.throwable = throwable;
	}

	
	public String getDataFiAmbFormat() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return dataFi != null ? sdf.format(dataFi) : "";
	}



}
