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
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
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
			"	     log.createdBy, " +
			"	     e.metaExpedient, " +
			"        log.tipus, " +
			"        count(log) " +
			"    ) " +
			"from     " +
	        "    ContingutLogEntity log, ExpedientEntity e " +
	        "where " +
	        "         log.contingutId = e.id " +
			"     and log.objecteTipus = :objecteTipus " +
	        "     and log.createdDate >= :createdDateIni " +
	        "     and log.createdDate <= :createdDateEnd " +
	        "group by" +
	        "     e.metaExpedient, log.createdBy, log.tipus")
	List<ContingutLogCountAggregation<UsuariEntity>> findLogsBetweenCreatedDateGroupByCreatedByAndTipus(
			@Param("objecteTipus") LogObjecteTipusEnumDto objecteTipus,
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
			"     and log.objecteTipus = :objecteTipus " +
	        "     and log.createdDate <= :createdDateEnd " +
	        "group by" +
	        "     e.metaExpedient, log.createdBy, log.tipus")
	List<ContingutLogCountAggregation<UsuariEntity>> findLogsBetweenCreatedDateGroupByCreatedByAndTipus(
			@Param("objecteTipus") LogObjecteTipusEnumDto objecteTipus,
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
            "     and log.objecteTipus = :objecteTipus " +
            "     and log.createdDate >= :createdDateIni " +
            "     and log.createdDate <= :createdDateEnd " +
            "group by" +
            "     e.metaExpedient, log.tipus")
        List<ContingutLogCountAggregation<MetaExpedientEntity>> findLogsBetweenCreatedDateGroupByMetaExpedient(
            @Param("objecteTipus") LogObjecteTipusEnumDto objecteTipus,
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
            "     and log.objecteTipus = :objecteTipus " +
            "     and log.createdDate <= :createdDateEnd " +
            "group by" +
            "     e.metaExpedient, log.tipus")
        List<ContingutLogCountAggregation<MetaExpedientEntity>> findLogsBeforeCreatedDateGroupByMetaExpedient(
            @Param("objecteTipus") LogObjecteTipusEnumDto objecteTipus,
            @Param("createdDateEnd") Date createdDateEnd);
	
	
	
	
	
	
	
	
//	
//	@Query( "select   " +
//			"     log " +
//			"from     " +
//	         "    ContingutLogEntity log, ExpedientEntity e " +
//	         "where " +
//	         "     log.contingutId = e.id " +
//	         " and e.metaExpedient = :metaExpedient " +
//	         " and log.tipus = :tipus " +
//			 " and log.objecteTipus = :objecteTipus " +
//	         " and log.createdDate >= :createdDateIni " +
//	         " and log.createdDate <= :createdDateEnd ")
//	List<ContingutLogEntity> findLogsExpedientBetweenCreatedDate(
//			@Param("objecteTipus") LogObjecteTipusEnumDto objecteTipus,
//			@Param("tipus") LogTipusEnumDto tipus,
//			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
//			@Param("createdDateIni") Date createdDateIni,
//			@Param("createdDateEnd") Date createdDateEnd);
//	
//	@Query( "select   " +
//			"     log " +
//			"from     " +
//	         "    ContingutLogEntity log, ExpedientEntity e " +
//	         "where " +
//	         "     log.contingutId = e.id " +
//	         " and e.metaExpedient = :metaExpedient " +
//	         " and log.tipus = :tipus " +
//			 " and log.objecteTipus = :objecteTipus " +
//	         " and log.createdDate <=  :createdDate ")
//	List<ContingutLogEntity> findLogsExpedientByCreateDateBefore(
//			@Param("objecteTipus") LogObjecteTipusEnumDto objecteTipus,
//			@Param("tipus") LogTipusEnumDto tipus,
//			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
//			@Param("createdDate") Date createdDate);
	
	@Query( "select   " +
			"     log " +
			"from     " +
	         "    ContingutLogEntity log, ExpedientEntity e JOIN e.interessats i " +
	         "where " +
	         "     log.contingutId = e.id " +
	         " and i.documentNum = :documentNum " +
	         " and e.metaExpedient = :metaExpedient " +
	         " and log.tipus = :tipus " +
			 " and log.objecteTipus = :objecteTipus " +
	         " and log.createdDate >= :createdDateIni " +
	         " and log.createdDate <= :createdDateEnd ")
	List<ContingutLogEntity> findLogsExpedientByInteressatAndBetweenCreatedDate(
			@Param("objecteTipus") LogObjecteTipusEnumDto objecteTipus,
			@Param("tipus") LogTipusEnumDto tipus,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
			@Param("documentNum") String documentNum,
			@Param("createdDateIni") Date createdDateIni,
			@Param("createdDateEnd") Date createdDateEnd);
	
	@Query( "select   " +
			"     log " +
			"from     " +
	         "    ContingutLogEntity log, ExpedientEntity e JOIN e.interessats i " +
	         "where " +
	         "     log.contingutId = e.id " +
	         " and i.documentNum = :documentNum " +
	         " and e.metaExpedient = :metaExpedient " +
	         " and log.tipus = :tipus " +
			 " and log.objecteTipus = :objecteTipus " +
	         " and log.createdDate <=  :createdDate ")
	List<ContingutLogEntity> findLogsExpedientByInteressatAndCreateDateBefore(
			@Param("objecteTipus") LogObjecteTipusEnumDto objecteTipus,
			@Param("tipus") LogTipusEnumDto tipus,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
			@Param("documentNum") String documentNum,
			@Param("createdDate") Date createdDate);
}
