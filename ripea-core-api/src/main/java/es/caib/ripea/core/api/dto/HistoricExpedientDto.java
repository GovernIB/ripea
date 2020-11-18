package es.caib.ripea.core.api.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class HistoricExpedientDto extends HistoricDto {
//	private Long numExpedientsAmbAlertes;
//	private Long numExpedientsAmbErrorsValidacio;
//	private Long numDocsPendentsSignar;
	
	@JsonProperty("DOCUMENTS_SIGNATS")
	private Long numDocsSignats;
//	private Long numDocsPendentsNotificar;
	
	@JsonProperty("DOCUMENTS_NOTIFICATS")
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
