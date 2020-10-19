package es.caib.ripea.core.api.dto;

import java.util.Date;

import lombok.Data;

@Data
public class HistoricExpedientDto extends HistoricDto{
//	private Long numExpedientsAmbAlertes;
//	private Long numExpedientsAmbErrorsValidacio;
//	private Long numDocsPendentsSignar;
	private Long numDocsSignats;
//	private Long numDocsPendentsNotificar;
	private Long numDocsNotificats;
	
	public HistoricExpedientDto() {
		
	}
	
	public HistoricExpedientDto(HistoricTipusEnumDto tipus, Date data) {
		super(tipus, data);
//		this.numExpedientsAmbAlertes = 0L;
//		this.numExpedientsAmbErrorsValidacio = 0L;
//		this.numDocsPendentsSignar = 0L;
		this.numDocsSignats = 0L;
//		this.numDocsPendentsNotificar = 0L;
		this.numDocsNotificats = 0L;
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
	
}
