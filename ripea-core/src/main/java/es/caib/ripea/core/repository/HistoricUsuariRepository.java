package es.caib.ripea.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.aggregation.HistoricUsuariAggregation;
import es.caib.ripea.core.api.dto.HistoricTipusEnumDto;
import es.caib.ripea.core.entity.HistoricUsuariEntity;
import es.caib.ripea.core.entity.UsuariEntity;

@Transactional
public interface HistoricUsuariRepository extends HistoricRepository<HistoricUsuariEntity> {
	
	@Query( "select " +
			"    new es.caib.ripea.core.aggregation.HistoricUsuariAggregation( " +
			"	     h.data, " +
			"	     sum(h.numExpedientsCreats), " +
			"        sum(h.numExpedientsCreatsTotal), " +
			"	     sum(h.numExpedientsOberts), " +
			"	     sum(h.numExpedientsObertsTotal), " +
			"	     sum(h.numExpedientsTancats), " +
			"	     sum(h.numExpedientsTancatsTotal), " +
			"	     h.usuari, " +
			"	     sum(h.numTasquesTramitades) " +
			"    ) " +
			" from " +
			"    HistoricUsuariEntity h " +
			" where " +
			"         h.usuari = :usuari " +
			"     and h.data >= :dataInici " +
			"     and h.data <= :dataFi " +
			"     and h.tipus = :tipus " +
			"     and (:isNullOrgansGestors = true or (h.organGestor != null and h.organGestor.id in (:organsGestors)))  " +
			"     and (:isNullMetaExpedients = true or h.metaExpedient.id in (:metaExpedients))  " +
			" group by " +
			"    h.data ")
	List<HistoricUsuariAggregation> findByDateRangeGroupedByDate(
			@Param("usuari") UsuariEntity usuari,
			@Param("tipus") HistoricTipusEnumDto tipus,
			@Param("isNullOrgansGestors") boolean isNullOrgansGestors,
			@Param("organsGestors") List<Long> organsGestors,
			@Param("isNullMetaExpedients") boolean isNullMetaExpedients,
			@Param("metaExpedients") List<Long> metaExpedients,
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi);
	
	@Query( "select " +
			"    new es.caib.ripea.core.aggregation.HistoricUsuariAggregation( " +
			"	     h.data, " +
			"	     sum(h.numExpedientsCreats), " +
			"        sum(h.numExpedientsCreatsTotal), " +
			"	     sum(h.numExpedientsOberts), " +
			"	     sum(h.numExpedientsObertsTotal), " +
			"	     sum(h.numExpedientsTancats), " +
			"	     sum(h.numExpedientsTancatsTotal), " +
			"	     h.usuari, " +
			"	     sum(h.numTasquesTramitades) " +
			"    ) " +
			" from " +
			"    HistoricUsuariEntity h " +
			" where " +
			"         h.usuari = :usuari " +
			"     and h.data >= :dataInici " +
			"     and h.data <= :dataFi " +
			"     and h.tipus = :tipus " +
			"     and (:isNullOrgansGestors = true or (h.organGestor != null and h.organGestor.id in (:organsGestors)))  " +
			"     and (:isNullMetaExpedients = true or h.metaExpedient.id in (:metaExpedients))  " +
			" group by " +
			"    h.data ")
	Page<HistoricUsuariAggregation> findByDateRangeGroupedByDate(
			@Param("usuari") UsuariEntity usuari,
			@Param("tipus") HistoricTipusEnumDto tipus,
			@Param("isNullOrgansGestors") boolean isNullOrgansGestors,
			@Param("organsGestors") List<Long> organsGestors,
			@Param("isNullMetaExpedients") boolean isNullMetaExpedients,
			@Param("metaExpedients") List<Long> metaExpedients,
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi, 
			Pageable pageable);
}
