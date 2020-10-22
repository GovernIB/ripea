package es.caib.ripea.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.aggregation.HistoricExpedientAggregation;
import es.caib.ripea.core.api.dto.HistoricTipusEnumDto;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.HistoricExpedientEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;

@Transactional
public interface HistoricExpedientRepository extends HistoricRepository<HistoricExpedientEntity> {
	
	@Query( "select " +
			"    new es.caib.ripea.core.aggregation.HistoricExpedientAggregation( " +
			"	     h.data, " +
			"	     sum(h.numExpedientsCreats), " +
			"        sum(h.numExpedientsCreatsTotal), " +
			"	     sum(h.numExpedientsOberts), " +
			"	     sum(h.numExpedientsObertsTotal), " +
			"	     sum(h.numExpedientsTancats), " +
			"	     sum(h.numExpedientsTancatsTotal), " +
			"        sum(h.numExpedientsAmbAlertes), " +
			"        sum(h.numExpedientsAmbErrorsValidacio), " +
			"        sum(h.numDocsPendentsSignar), " +
			"        sum(h.numDocsSignats), " +
			"	     sum(h.numDocsPendentsNotificar), " +
			"        sum(h.numDocsNotificats) " +
			"    ) " +
			" from " +
			"    HistoricExpedientEntity h " +
			" where " +
			"         h.entitat = :entitat " +
			"     and h.data >= :dataInici " +
			"     and h.data <= :dataFi " +
			"     and h.tipus = :tipus " +
			"     and (:isNullOrgansGestors = true or (h.organGestor != null and h.organGestor.id in (:organsGestors)))  " +
			"     and (:isNullMetaExpedients = true or h.metaExpedient.id in (:metaExpedients))  " +
			" group by " +
			"    h.data ")
	List<HistoricExpedientAggregation> findByEntitatAndDateRangeGroupedByDate(
			@Param("entitat") EntitatEntity entitat,
			@Param("tipus") HistoricTipusEnumDto tipus,
			@Param("isNullOrgansGestors") boolean isNullOrgansGestors,
			@Param("organsGestors") List<Long> organsGestors,
			@Param("isNullMetaExpedients") boolean isNullMetaExpedients,
			@Param("metaExpedients") List<Long> metaExpedients,
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi);
	
	@Query( "select " +
			"    new es.caib.ripea.core.aggregation.HistoricExpedientAggregation( " +
			"	     h.data, " +
			"	     sum(h.numExpedientsCreats), " +
			"        sum(h.numExpedientsCreatsTotal), " +
			"	     sum(h.numExpedientsOberts), " +
			"	     sum(h.numExpedientsObertsTotal), " +
			"	     sum(h.numExpedientsTancats), " +
			"	     sum(h.numExpedientsTancatsTotal), " +
			"        sum(h.numExpedientsAmbAlertes), " +
			"        sum(h.numExpedientsAmbErrorsValidacio), " +
			"        sum(h.numDocsPendentsSignar), " +
			"        sum(h.numDocsSignats), " +
			"	     sum(h.numDocsPendentsNotificar), " +
			"        sum(h.numDocsNotificats) " +
			"    ) " +
			" from " +
			"    HistoricExpedientEntity h " +
			" where " +
			"         h.entitat = :entitat " +
			"     and h.data >= :dataInici " +
			"     and h.data <= :dataFi " +
			"     and h.tipus = :tipus " +
			"     and (:isNullOrgansGestors = true or (h.organGestor != null and h.organGestor.id in (:organsGestors)))  " +
			"     and (:isNullMetaExpedients = true or h.metaExpedient.id in (:metaExpedients))  " +
			" group by " +
			"    h.data " +
			" order by " +
			"    h.data ")
	Page<HistoricExpedientAggregation> findByEntitatAndDateRangeGroupedByDate(
			@Param("entitat") EntitatEntity entitat,
			@Param("tipus") HistoricTipusEnumDto tipus,
			@Param("isNullOrgansGestors") boolean isNullOrgansGestors,
			@Param("organsGestors") List<Long> organsGestors,
			@Param("isNullMetaExpedients") boolean isNullMetaExpedients,
			@Param("metaExpedients") List<Long> metaExpedients,
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi, 
			Pageable pageable);

	@Query( "select " +
			"    new es.caib.ripea.core.aggregation.HistoricExpedientAggregation( " +
			"	     h.data, " +
			"	     sum(h.numExpedientsCreats), " +
			"        sum(h.numExpedientsCreatsTotal), " +
			"	     sum(h.numExpedientsOberts), " +
			"	     sum(h.numExpedientsObertsTotal), " +
			"	     sum(h.numExpedientsTancats), " +
			"	     sum(h.numExpedientsTancatsTotal), " +
			"        sum(h.numExpedientsAmbAlertes), " +
			"        sum(h.numExpedientsAmbErrorsValidacio), " +
			"        sum(h.numDocsPendentsSignar), " +
			"        sum(h.numDocsSignats), " +
			"	     sum(h.numDocsPendentsNotificar), " +
			"        sum(h.numDocsNotificats) " +
			"    ) " +
			" from " +
			"    HistoricExpedientEntity h " +
			" where " +
			"        h.organGestor = :organGestor " +
			"    and h.data >= :dataInici " +
			"	 and h.data <= :dataFi " +
			"    and h.tipus = :tipus " +
			"    and (:isNullMetaExpedients = true or h.metaExpedient.id in (:metaExpedients))  " +
			" group by " +
			"    h.data " +
			" order by " +
			"    h.data ")
	List<HistoricExpedientAggregation> findByOrganGestorAndDateRangeGroupedByDate(
			@Param("organGestor") OrganGestorEntity organGestor,
			@Param("tipus") HistoricTipusEnumDto tipus,
			@Param("isNullMetaExpedients") boolean isNullMetaExpedients,
			@Param("metaExpedients") List<Long> metaExpedients,
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi);

}
