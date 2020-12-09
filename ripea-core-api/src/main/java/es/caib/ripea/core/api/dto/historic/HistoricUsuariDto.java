package es.caib.ripea.core.api.dto.historic;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class HistoricUsuariDto extends HistoricDto{
	@JsonIgnore
	private String usuariCodi;
	
	private Long numTasquesTramitades;
	
	public HistoricUsuariDto(HistoricTipusEnumDto tipus, Date data) {
		super(tipus, data);
		this.numTasquesTramitades = 0L;
	}
	
}
