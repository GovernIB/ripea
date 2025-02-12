package es.caib.ripea.persistence.repository.historic;

import es.caib.ripea.persistence.aggregation.HistoricExpedientAggregation;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.HistoricExpedientEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.service.intf.dto.historic.HistoricTipusEnumDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
public interface HistoricExpedientRepository extends HistoricRepository<HistoricExpedientEntity> {
	static String commonConstructor = "    new es.caib.ripea.core.aggregation.HistoricExpedientAggregation( " +
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
			"    ) ";
	static String entitatFiltre = 
			"         h.entitat = :entitat " +
					"     and h.data >= :dataInici " +
					"     and h.data <= :dataFi " +
					"     and h.tipus = :tipus " +
					"     and ((:isNullOrgansGestors = true and :incorporarExpedientsComuns = true) or " +
					"					(:isNullOrgansGestors = true or h.organGestorId in (:organsGestors)) or " +
					"					(:incorporarExpedientsComuns = true and h.organGestorId = null)" +
					"     )  " +
					"     and (:isNullMetaExpedients = true or h.metaExpedient.id in (:metaExpedients))  ";

	@Query( "select " +
				commonConstructor +
			" from " +
			"    HistoricExpedientEntity h " +
			" where " +
				entitatFiltre +
			" group by " +
			"    h.data " +
			" order by " +
			"    h.data desc ")
	List<HistoricExpedientAggregation> findByEntitatAndDateRangeGroupedByDate(
			@Param("entitat") EntitatEntity entitat,
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
			"    HistoricExpedientEntity h " +
			" where " +
				entitatFiltre +
			" group by " +
			"    h.data ")
	Page<HistoricExpedientAggregation> findByEntitatAndDateRangeGroupedByDate(
			@Param("entitat") EntitatEntity entitat,
			@Param("tipus") HistoricTipusEnumDto tipus,
			@Param("isNullOrgansGestors") boolean isNullOrgansGestors,
			@Param("organsGestors") List<Long> organsGestors,
			@Param("incorporarExpedientsComuns") boolean incorporarExpedientsComuns,
			@Param("isNullMetaExpedients") boolean isNullMetaExpedients,
			@Param("metaExpedients") List<Long> metaExpedients,
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi, 
			Pageable pageable);

	@Query( "select " +
				commonConstructor +
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
			"    h.data desc ")
	List<HistoricExpedientAggregation> findByOrganGestorAndDateRangeGroupedByDate(
			@Param("organGestor") OrganGestorEntity organGestor,
			@Param("tipus") HistoricTipusEnumDto tipus,
			@Param("isNullMetaExpedients") boolean isNullMetaExpedients,
			@Param("metaExpedients") List<Long> metaExpedients,
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi);

	@Query( "select " +
				commonConstructor +
			" from " +
			"    HistoricExpedientEntity h " +
			" where " +
			"        h.organGestor = null " +
			"    and h.data >= :dataInici " +
			"	 and h.data <= :dataFi " +
			"    and h.tipus = :tipus " +
			"    and (:isNullMetaExpedients = true or h.metaExpedient.id in (:metaExpedients))  " +
			" group by " +
			"    h.data " +
			" order by " +
			"    h.data desc ")
	List<HistoricExpedientAggregation> findByExpedientsComunsAndDateRangeGroupedByDate(
			@Param("tipus") HistoricTipusEnumDto tipus,
			@Param("isNullMetaExpedients") boolean isNullMetaExpedients,
			@Param("metaExpedients") List<Long> metaExpedients,
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi);
	
	
	
	
	
	@Query( "select " +
			"h "+
		" from " +
		"    HistoricExpedientEntity h " +
		" where " +
		"    	 h.data = :data " +
		"    and h.tipus = :tipus " )
	List<HistoricExpedientEntity> findByDateAndTipus(
			@Param("data") Date data,
			@Param("tipus") HistoricTipusEnumDto tipus);

}
