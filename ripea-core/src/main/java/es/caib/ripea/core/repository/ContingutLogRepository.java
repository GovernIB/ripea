/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.aggregation.ContingutLogCountAggregation;
import es.caib.ripea.core.entity.ContingutLogEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.UsuariEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base de
 * dades del tipus ContingutLog.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ContingutLogRepository extends JpaRepository<ContingutLogEntity, Long> {

	List<ContingutLogEntity> findByContingutIdOrderByCreatedDateAsc(Long contingutId);


	@Query( "select   " +
			"    new es.caib.ripea.core.aggregation.ContingutLogCountAggregation( " +
			"	     i.documentNum, " +
			"	     e.metaExpedient, " +
			"        log.tipus, " +
			"        count(log) " +
			"    ) " +
			"from     " +
	        "    ContingutLogEntity log, ExpedientEntity e JOIN e.interessats i " +
	        "where " +
	        "         log.contingutId = e.id " +
			"     and log.objecteTipus = es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto.EXPEDIENT " +
	        "     and log.createdDate >= :createdDateIni " +
	        "     and log.createdDate <= :createdDateEnd " +
	        "group by" +
	        "     e.metaExpedient, log.tipus, i.documentNum")
	List<ContingutLogCountAggregation<String>> findLogsExpedientBetweenCreatedDateGroupByInteressatAndTipus(
			@Param("createdDateIni") Date createdDateIni,
			@Param("createdDateEnd") Date createdDateEnd);
	
	@Query( "select   " +
			"    new es.caib.ripea.core.aggregation.ContingutLogCountAggregation( " +
			"	     i.documentNum, " +
			"	     e.metaExpedient, " +
			"        log.tipus, " +
			"        count(log) " +
			"    ) " +
			"from     " +
	        "    ContingutLogEntity log, ExpedientEntity e JOIN e.interessats i " +
	        "where " +
	        "         log.contingutId = e.id " +
			"     and log.objecteTipus = es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto.EXPEDIENT " +
	        "     and log.createdDate <= :createdDateEnd " +
	        "group by" +
	        "     e.metaExpedient, log.tipus, i.documentNum")
	List<ContingutLogCountAggregation<String>> findLogsExpedientBetweenCreatedDateGroupByInteressatAndTipus(
			@Param("createdDateEnd") Date createdDateEnd);
	
	@Query( "select   " +
			"    new es.caib.ripea.core.aggregation.ContingutLogCountAggregation( " +
			"	     log.createdBy, " +
			"	     e.metaExpedient, " +
			"        log.tipus, " +
			"        count(log) " +
			"    ) " +
			"from     " +
	        "    ContingutLogEntity log, ExpedientEntity e " +
	        "where " +
	        "         log.contingutId = e.id " +
			"     and log.objecteTipus = es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto.EXPEDIENT " +
	        "     and log.createdDate >= :createdDateIni " +
	        "     and log.createdDate <= :createdDateEnd " +
	        "group by" +
	        "     e.metaExpedient, log.createdBy, log.tipus")
	List<ContingutLogCountAggregation<UsuariEntity>> findLogsExpedientBetweenCreatedDateGroupByCreatedByAndTipus(
			@Param("createdDateIni") Date createdDateIni,
			@Param("createdDateEnd") Date createdDateEnd);
	
	@Query( "select   " +
			"    new es.caib.ripea.core.aggregation.ContingutLogCountAggregation( " +
			"	     log.createdBy, " +
			"	     e.metaExpedient, " +
			"        log.tipus, " +
			"        count(log) " +
			"    ) " +
			"from     " +
	        "    ContingutLogEntity log, ExpedientEntity e " +
	        "where " +
	        "         log.contingutId = e.id " +
			"     and log.objecteTipus = es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto.EXPEDIENT " +
	        "     and log.createdDate <= :createdDateEnd " +
	        "group by" +
	        "     e.metaExpedient, log.createdBy, log.tipus")
	List<ContingutLogCountAggregation<UsuariEntity>> findLogsExpedientBetweenCreatedDateGroupByCreatedByAndTipus(
			@Param("createdDateEnd") Date createdDateEnd);
	
	@Query( "select   " +
            "    new es.caib.ripea.core.aggregation.ContingutLogCountAggregation( " +
            "	     e.metaExpedient, " +
			"	     e.metaExpedient, " +
            "        log.tipus, " +
            "        count(log) " +
            "    ) " +
            "from     " +
            "    ContingutLogEntity log, ExpedientEntity e " +
            "where " +
            "         log.contingutId = e.id " +
            "     and log.objecteTipus = es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto.EXPEDIENT " +
            "     and log.createdDate >= :createdDateIni " +
            "     and log.createdDate <= :createdDateEnd " +
            "group by" +
            "     e.metaExpedient, log.tipus")
        List<ContingutLogCountAggregation<MetaExpedientEntity>> findLogsExpedientBetweenCreatedDateGroupByMetaExpedient(
            @Param("createdDateIni") Date createdDateIni,
            @Param("createdDateEnd") Date createdDateEnd);
	
	@Query( "select   " +
            "    new es.caib.ripea.core.aggregation.ContingutLogCountAggregation( " +
            "	     e.metaExpedient, " +
			"	     e.metaExpedient, " +
            "        log.tipus, " +
            "        count(log) " +
            "    ) " +
            "from     " +
            "    ContingutLogEntity log, ExpedientEntity e " +
            "where " +
            "         log.contingutId = e.id " +
            "     and log.objecteTipus = es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto.EXPEDIENT " +
            "     and log.createdDate <= :createdDateEnd " +
            "group by" +
            "     e.metaExpedient, log.tipus")
        List<ContingutLogCountAggregation<MetaExpedientEntity>> findLogsExpedientBeforeCreatedDateGroupByMetaExpedient(
            @Param("createdDateEnd") Date createdDateEnd);
	
	
	@Query( "select   " +
            "    new es.caib.ripea.core.aggregation.ContingutLogCountAggregation( " +
            "	     e.metaExpedient, " +
			"	     e.metaExpedient, " +
            "        log.tipus, " +
            "        count(log) " +
            "    ) " +
            "from     " +
            "    ContingutLogEntity log, DocumentEntity d JOIN d.expedient e " +
            "where " +
            "         log.contingutId = d.id " +
            "     and log.objecteTipus = es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto.DOCUMENT " +
            "     and log.createdDate >= :createdDateIni " +
            "     and log.createdDate <= :createdDateEnd " +
            "group by" +
            "     e.metaExpedient, log.tipus")
        List<ContingutLogCountAggregation<MetaExpedientEntity>> findLogsDocumentBetweenCreatedDateGroupByMetaExpedient(
                @Param("createdDateIni") Date createdDateIni,
                @Param("createdDateEnd") Date createdDateEnd);
}
