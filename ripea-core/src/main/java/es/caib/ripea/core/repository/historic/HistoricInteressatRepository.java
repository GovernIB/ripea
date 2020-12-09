package es.caib.ripea.core.repository.historic;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.aggregation.HistoricAggregation;
import es.caib.ripea.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.ripea.core.entity.HistoricInteressatEntity;

@Transactional
public interface HistoricInteressatRepository extends HistoricRepository<HistoricInteressatEntity> {
	@Query( "select " +
			"    new es.caib.ripea.core.aggregation.HistoricAggregation( " +
			"	     h.data, " +
			"	     sum(h.numExpedientsCreats), " +
			"        sum(h.numExpedientsCreatsTotal), " +
			"	     sum(h.numExpedientsOberts), " +
			"	     sum(h.numExpedientsObertsTotal), " +
			"	     sum(h.numExpedientsTancats), " +
			"	     sum(h.numExpedientsTancatsTotal) " +
			"    ) " +
			" from " +
			"    HistoricInteressatEntity h " +
			" where " +
			"         h.interessatDocNum = :interessatDocNum " +
			"     and h.data >= :dataInici " +
			"     and h.data <= :dataFi " +
			"     and h.tipus = :tipus " +
			"     and ((:isNullOrgansGestors = true and :incorporarExpedientsComuns = true) or " +
			"					(:isNullOrgansGestors = true or h.organGestorId in (:organsGestors)) or " +
			"					(:incorporarExpedientsComuns = true and h.organGestorId = null)" +
			"     )  " +
			"     and (:isNullMetaExpedients = true or h.metaExpedient.id in (:metaExpedients))  " +
			" group by " +
			"    h.data, h.interessatDocNum " +
			" order by " +
			"    h.data desc ")
	List<HistoricAggregation> findByDateRangeGroupedByDate(
			@Param("interessatDocNum") String interessatDocNum,
			@Param("tipus") HistoricTipusEnumDto tipus,
			@Param("isNullOrgansGestors") boolean isNullOrgansGestors,
			@Param("organsGestors") List<Long> organsGestors,
			@Param("incorporarExpedientsComuns") boolean incorporarExpedientsComuns,
			@Param("isNullMetaExpedients") boolean isNullMetaExpedients,
			@Param("metaExpedients") List<Long> metaExpedients,
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi);
}
