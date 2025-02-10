package es.caib.ripea.core.persistence.aggregation;

import es.caib.ripea.core.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import lombok.Data;

@Data
public class ContingutLogCountAggregation<E> {

	private E itemGrouped;
	private MetaExpedientEntity metaExpedient;
	private LogTipusEnumDto tipus;
	private Long count;

	public ContingutLogCountAggregation(
			E itemGrouped,
			MetaExpedientEntity metaExpedient,
			LogTipusEnumDto tipus,
			Long count) {
		super();
		this.itemGrouped = itemGrouped;
		this.metaExpedient = metaExpedient;
		this.tipus = tipus;
		this.count = count;
	}
	public ContingutLogCountAggregation(
			E itemGrouped,
			MetaExpedientEntity metaExpedient,
			Long count) {
		super();
		this.itemGrouped = itemGrouped;
		this.metaExpedient = metaExpedient;
		this.count = count;
	}
}
