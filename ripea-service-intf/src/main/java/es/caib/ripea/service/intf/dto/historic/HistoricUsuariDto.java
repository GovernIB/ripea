package es.caib.ripea.service.intf.dto.historic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class HistoricUsuariDto extends HistoricDto {
	@JsonIgnore
	private String usuariCodi;
	
	private Long numTasquesTramitades;
	
	public HistoricUsuariDto(HistoricTipusEnumDto tipus, Date data) {
		super(tipus, data);
		this.numTasquesTramitades = 0L;
	}
	
}
