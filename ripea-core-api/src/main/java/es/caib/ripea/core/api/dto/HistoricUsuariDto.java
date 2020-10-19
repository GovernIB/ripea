package es.caib.ripea.core.api.dto;

import java.util.Date;

import lombok.Data;

@Data
public class HistoricUsuariDto extends HistoricDto{
	private String usuariCodi;
	private Long numTasquesTramitades;
	
	public HistoricUsuariDto(HistoricTipusEnumDto tipus, Date data) {
		super(tipus, data);
		this.numTasquesTramitades = 0L;
	}
	
}
