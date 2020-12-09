package es.caib.ripea.core.api.dto.historic;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class HistoricExpedientDto extends HistoricDto {
//	private Long numExpedientsAmbAlertes;
//	private Long numExpedientsAmbErrorsValidacio;
//	private Long numDocsPendentsSignar;
	
	private Long numDocsSignats;
//	private Long numDocsPendentsNotificar;
	
	private Long numDocsNotificats;
	

	public HistoricExpedientDto(HistoricTipusEnumDto tipus, Date data) {
		super(tipus, data);
		initValues();
	}
	
	public HistoricExpedientDto() {
		initValues();
	}
		
	public void combinarAmb(HistoricExpedientDto historic) {
		super.combinarAmb(historic);
//		this.numExpedientsAmbAlertes += historic.getNumExpedientsAmbAlertes();
//		this.numExpedientsAmbErrorsValidacio += historic.getNumExpedientsAmbErrorsValidacio();
//		this.numDocsPendentsSignar += historic.getNumDocsPendentsNotificar();
		this.numDocsSignats += historic.getNumDocsSignats();
//		this.numDocsPendentsNotificar += historic.getNumDocsPendentsNotificar();
		this.numDocsNotificats += historic.getNumDocsNotificats();
	}
	
	private void initValues() {
//		this.numExpedientsAmbAlertes = 0L;
//		this.numExpedientsAmbErrorsValidacio = 0L;
//		this.numDocsPendentsSignar = 0L;
		this.numDocsSignats = 0L;
//		this.numDocsPendentsNotificar = 0L;
		this.numDocsNotificats = 0L;
	}

}
