package es.caib.ripea.persistence.repository.historic;

import es.caib.ripea.persistence.aggregation.HistoricUsuariAggregation;
import es.caib.ripea.persistence.entity.HistoricUsuariEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.service.intf.dto.historic.HistoricTipusEnumDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
public interface HistoricUsuariRepository extends HistoricRepository<HistoricUsuariEntity> {
	static String commonFilter =
			"         h.usuari = :usuari " +
			"     and h.data >= :dataInici " +
			"     and h.data <= :dataFi " +
			"     and h.tipus = :tipus " +
			"     and ((:isNullOrgansGestors = true and :incorporarExpedientsComuns = true) or " +
			"					(:isNullOrgansGestors = true or h.organGestorId in (:organsGestors)) or " +
			"					(:incorporarExpedientsComuns = true and h.organGestorId = null)" +
			"     )  " +
			"     and (:isNullMetaExpedients = true or h.metaExpedient.id in (:metaExpedients))  ";
	
	static String commonConstructor =
			"    new es.caib.ripea.persistence.aggregation.HistoricUsuariAggregation( " +
			"	     h.data, " +
			"	     sum(h.numExpedientsCreats), " +
			"        sum(h.numExpedientsCreatsTotal), " +
			"	     sum(h.numExpedientsOberts), " +
			"	     sum(h.numExpedientsObertsTotal), " +
			"	     sum(h.numExpedientsTancats), " +
			"	     sum(h.numExpedientsTancatsTotal), " +
			"	     h.usuari, " +
			"	     sum(h.numTasquesTramitades) " +
			"    ) ";
	
	@Query( "select " +
				commonConstructor +
			" from " +
			"    HistoricUsuariEntity h " +
			" where " +
				commonFilter +
			" group by " +
			"    h.data, h.usuari " +
			" order by " +
			"    h.data desc ")
	List<HistoricUsuariAggregation> findByDateRangeGroupedByDate(
			@Param("usuari") UsuariEntity usuari,
			@Param("tipus") HistoricTipusEnumDto tipus,
			@Param("isNullOrgansGestors") boolean isNullOrgansGestors,
			@Param("organsGestors") List<Long> organsGestors,
			@Param("incorporarExpedientsComuns") boolean incorporarExpedientsComuns,
			@Param("isNullMetaExpedients") boolean isNullMetaExpedients,
			@Param("metaExpedients") List<Long> metaExpedients,
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi);
	
	@Query( "select " +
				commonConstructor +
			" from " +
			"    HistoricUsuariEntity h " +
			" where " +
				commonFilter +
			" group by " +
			"    h.data, h.usuari ")
	Page<HistoricUsuariAggregation> findByDateRangeGroupedByDate(
			@Param("usuari") UsuariEntity usuari,
			@Param("tipus") HistoricTipusEnumDto tipus,
			@Param("isNullOrgansGestors") boolean isNullOrgansGestors,
			@Param("organsGestors") List<Long> organsGestors,
			@Param("incorporarExpedientsComuns") boolean incorporarExpedientsComuns,
			@Param("isNullMetaExpedients") boolean isNullMetaExpedients,
			@Param("metaExpedients") List<Long> metaExpedients,
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi, 
			Pageable pageable);
}
