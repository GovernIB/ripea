package es.caib.ripea.core.api.dto;

import java.util.Date;

import lombok.Data;

@Data
public class HistoricInteressatDto extends HistoricDto {

	private String interessatDocNum;

	public HistoricInteressatDto(HistoricTipusEnumDto tipus, Date data) {
		super(tipus, data);
	}

}
