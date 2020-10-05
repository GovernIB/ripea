package es.caib.ripea.core.api.dto;

import java.util.Date;

import lombok.Data;

@Data
public class HistoricDto extends AuditoriaDto {
	protected Long entitatId;
	protected Long organGestorId;
	protected Long metaExpedientId;
	protected HistoricTipusEnumDto tipus;
	protected Date data;

	protected Long numExpedientsCreats;
	protected Long numExpedientsCreatsTotal;
	protected Long numExpedientsOberts;
	protected Long numExpedientsObertsTotal;
	protected Long numExpedientsTancats;
	protected Long numExpedientsTancatsTotal;
}
