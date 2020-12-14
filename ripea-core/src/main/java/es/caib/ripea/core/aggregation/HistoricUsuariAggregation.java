package es.caib.ripea.core.aggregation;

import java.util.Date;

import es.caib.ripea.core.entity.UsuariEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class HistoricUsuariAggregation extends HistoricAggregation{

	private UsuariEntity usuari;
	private Long numTasquesTramitades;
	
	public HistoricUsuariAggregation(
			Date data,
			Long numExpedientsCreats,
			Long numExpedientsCreatsTotal,
			Long numExpedientsOberts,
			Long numExpedientsObertsTotal,
			Long numExpedientsTancats,
			Long numExpedientsTancatsTotal,
			UsuariEntity usuari,
			Long numTasquesTramitades) {
		super(
				data,
				numExpedientsCreats,
				numExpedientsCreatsTotal,
				numExpedientsOberts,
				numExpedientsObertsTotal,
				numExpedientsTancats,
				numExpedientsTancatsTotal);
		this.usuari = usuari;
		this.numTasquesTramitades = numTasquesTramitades;
	}
	public HistoricUsuariAggregation(
			Date data) {
		super(data);
		this.usuari = null;
		this.numTasquesTramitades = 0L;
	}
}
