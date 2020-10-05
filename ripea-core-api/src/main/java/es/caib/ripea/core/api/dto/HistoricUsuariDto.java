package es.caib.ripea.core.api.dto;

import lombok.Data;

@Data
public class HistoricUsuariDto extends HistoricDto{
	private String usuariCodi;
	private Long numTasquesTramitades;
}
