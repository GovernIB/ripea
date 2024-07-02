package es.caib.ripea.core.api.dto.historic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class HistoricInteressatDto extends HistoricDto {
	@JsonIgnore
	private String interessatDocNum;

	public HistoricInteressatDto(HistoricTipusEnumDto tipus, Date data) {
		super(tipus, data);
	}

}
