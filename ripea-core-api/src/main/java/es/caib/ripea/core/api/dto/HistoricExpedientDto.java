package es.caib.ripea.core.api.dto;

import lombok.Data;

@Data
public class HistoricExpedientDto extends HistoricDto{
	private Long numExpedientsAmbAlertes;
	private Long numExpedientsAmbErrorsValidacio;
	private Long numDocsPendentsSignar;
	private Long numDocsSignats;
	private Long numDocsPendentsNotificar;
	private Long numDocsNotificats;
}
