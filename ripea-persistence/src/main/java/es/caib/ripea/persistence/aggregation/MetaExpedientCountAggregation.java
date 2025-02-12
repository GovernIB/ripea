package es.caib.ripea.persistence.aggregation;

import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import lombok.Data;

@Data
public class MetaExpedientCountAggregation {
	private MetaExpedientEntity metaExpedient;
	private long count;
	
	public MetaExpedientCountAggregation(MetaExpedientEntity metaExpedient, long count) {
		super();
		this.metaExpedient = metaExpedient;
		this.count = count;
	}
	
	
}
