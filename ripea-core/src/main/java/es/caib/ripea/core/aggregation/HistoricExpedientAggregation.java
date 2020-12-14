package es.caib.ripea.core.aggregation;

import java.util.Date;

import lombok.Data;

@Data
public class HistoricExpedientAggregation extends HistoricAggregation{

	private Long numExpedientsAmbAlertes;
	private Long numExpedientsAmbErrorsValidacio;
	private Long numDocsPendentsSignar;
	private Long numDocsSignats;
	private Long numDocsPendentsNotificar;
	private Long numDocsNotificats;
	
	public HistoricExpedientAggregation(
			Date data,
			Long numExpedientsCreats,
			Long numExpedientsCreatsTotal,
			Long numExpedientsOberts,
			Long numExpedientsObertsTotal,
			Long numExpedientsTancats,
			Long numExpedientsTancatsTotal,
			Long numExpedientsAmbAlertes,
			Long numExpedientsAmbErrorsValidacio,
			Long numDocsPendentsSignar,
			Long numDocsSignats,
			Long numDocsPendentsNotificar,
			Long numDocsNotificats) {
		super(
				data,
				numExpedientsCreats,
				numExpedientsCreatsTotal,
				numExpedientsOberts,
				numExpedientsObertsTotal,
				numExpedientsTancats,
				numExpedientsTancatsTotal);
		this.numExpedientsAmbAlertes = numExpedientsAmbAlertes;
		this.numExpedientsAmbErrorsValidacio = numExpedientsAmbErrorsValidacio;
		this.numDocsPendentsSignar = numDocsPendentsSignar;
		this.numDocsSignats = numDocsSignats;
		this.numDocsPendentsNotificar = numDocsPendentsNotificar;
		this.numDocsNotificats = numDocsNotificats;
	}
	
	public HistoricExpedientAggregation(Date data) {
		super(data);
		this.numExpedientsAmbAlertes = 0L;
		this.numExpedientsAmbErrorsValidacio = 0L;
		this.numDocsPendentsSignar = 0L;
		this.numDocsSignats = 0L;
		this.numDocsPendentsNotificar = 0L;
		this.numDocsNotificats = 0L;
	}
}
