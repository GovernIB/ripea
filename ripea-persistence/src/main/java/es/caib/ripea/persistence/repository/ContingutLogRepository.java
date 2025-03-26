package es.caib.ripea.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.aggregation.ContingutLogCountAggregation;
import es.caib.ripea.persistence.entity.ContingutLogEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;

@Component
public interface ContingutLogRepository extends JpaRepository<ContingutLogEntity, Long> {

	List<ContingutLogEntity> findByContingutIdOrderByCreatedDateAsc(Long contingutId);

	@Query( "select   " +
			"    new es.caib.ripea.persistence.aggregation.ContingutLogCountAggregation( " +
			"	     i.documentNum, " +
			"	     e.metaExpedient, " +
			"        log.tipus, " +
			"        count(log) " +
			"    ) " +
			"from     " +
	        "    ContingutLogEntity log, ExpedientEntity e JOIN e.interessats i " +
	        "where " +
	        "         log.contingutId = e.id " +
			"     and log.objecteTipus = es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto.EXPEDIENT " +
	        "     and log.createdDate >= :createdDateIni " +
	        "     and log.createdDate <= :createdDateEnd " +
	        "group by" +
	        "     e.metaExpedient, log.tipus, i.documentNum")
	List<ContingutLogCountAggregation<String>> findLogsExpedientBetweenCreatedDateGroupByInteressatAndTipus(
			@Param("createdDateIni") LocalDateTime createdDateIni,
			@Param("createdDateEnd") LocalDateTime createdDateEnd);
	
	@Query( "select   " +
			"    new es.caib.ripea.persistence.aggregation.ContingutLogCountAggregation( " +
			"	     i.documentNum, " +
			"	     e.metaExpedient, " +
			"        log.tipus, " +
			"        count(log) " +
			"    ) " +
			"from     " +
	        "    ContingutLogEntity log, ExpedientEntity e JOIN e.interessats i " +
	        "where " +
	        "         log.contingutId = e.id " +
			"     and log.objecteTipus = es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto.EXPEDIENT " +
	        "     and log.createdDate <= :createdDateEnd " +
	        "group by" +
	        "     e.metaExpedient, log.tipus, i.documentNum")
	List<ContingutLogCountAggregation<String>> findLogsExpedientBetweenCreatedDateGroupByInteressatAndTipus(
			@Param("createdDateEnd") LocalDateTime createdDateEnd);
	
	@Query( "select   " +
			"    new es.caib.ripea.persistence.aggregation.ContingutLogCountAggregation( " +
			"	     log.createdBy, " +
			"	     e.metaExpedient, " +
			"        log.tipus, " +
			"        count(log) " +
			"    ) " +
			"from     " +
	        "    ContingutLogEntity log, ExpedientEntity e " +
	        "where " +
	        "         log.contingutId = e.id " +
			"     and log.objecteTipus = es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto.EXPEDIENT " +
	        "     and log.createdDate >= :createdDateIni " +
	        "     and log.createdDate <= :createdDateEnd " +
	        "group by" +
	        "     e.metaExpedient, log.createdBy, log.tipus")
	List<ContingutLogCountAggregation<UsuariEntity>> findLogsExpedientBetweenCreatedDateGroupByCreatedByAndTipus(
			@Param("createdDateIni") LocalDateTime createdDateIni,
			@Param("createdDateEnd") LocalDateTime createdDateEnd);
	
	@Query( "select   " +
			"    new es.caib.ripea.persistence.aggregation.ContingutLogCountAggregation( " +
			"	     log.createdBy, " +
			"	     e.metaExpedient, " +
			"        log.tipus, " +
			"        count(log) " +
			"    ) " +
			"from     " +
	        "    ContingutLogEntity log, ExpedientEntity e " +
	        "where " +
	        "         log.contingutId = e.id " +
			"     and log.objecteTipus = es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto.EXPEDIENT " +
	        "     and log.createdDate <= :createdDateEnd " +
	        "group by" +
	        "     e.metaExpedient, log.createdBy, log.tipus")
	List<ContingutLogCountAggregation<UsuariEntity>> findLogsExpedientBetweenCreatedDateGroupByCreatedByAndTipus(
			@Param("createdDateEnd") LocalDateTime createdDateEnd);
	
	@Query( "select   " +
            "    new es.caib.ripea.persistence.aggregation.ContingutLogCountAggregation( " +
            "	     e.metaExpedient, " +
			"	     e.metaExpedient, " +
            "        log.tipus, " +
            "        count(log) " +
            "    ) " +
            "from     " +
            "    ContingutLogEntity log, ExpedientEntity e " +
            "where " +
            "         log.contingutId = e.id " +
            "     and log.objecteTipus = es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto.EXPEDIENT " +
            "     and log.createdDate >= :createdDateIni " +
            "     and log.createdDate <= :createdDateEnd " +
            "group by" +
            "     e.metaExpedient, log.tipus")
        List<ContingutLogCountAggregation<MetaExpedientEntity>> findLogsExpedientBetweenCreatedDateGroupByMetaExpedient(
            @Param("createdDateIni") LocalDateTime createdDateIni,
            @Param("createdDateEnd") LocalDateTime createdDateEnd);
	
	@Query( "select   " +
            "    new es.caib.ripea.persistence.aggregation.ContingutLogCountAggregation( " +
            "	     e.metaExpedient, " +
			"	     e.metaExpedient, " +
            "        log.tipus, " +
            "        count(log) " +
            "    ) " +
            "from     " +
            "    ContingutLogEntity log, ExpedientEntity e " +
            "where " +
            "         log.contingutId = e.id " +
            "     and log.objecteTipus = es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto.EXPEDIENT " +
            "     and log.createdDate <= :createdDateEnd " +
            "group by" +
            "     e.metaExpedient, log.tipus")
        List<ContingutLogCountAggregation<MetaExpedientEntity>> findLogsExpedientBeforeCreatedDateGroupByMetaExpedient(
            @Param("createdDateEnd") LocalDateTime createdDateEnd);
	
	
	@Query( "select   " +
            "    new es.caib.ripea.persistence.aggregation.ContingutLogCountAggregation( " +
            "	     e.metaExpedient, " +
			"	     e.metaExpedient, " +
            "        log.tipus, " +
            "        count(log) " +
            "    ) " +
            "from     " +
            "    ContingutLogEntity log, DocumentEntity d JOIN d.expedient e " +
            "where " +
            "         log.contingutId = d.id " +
            "     and log.objecteTipus = es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto.DOCUMENT " +
            "     and log.createdDate >= :createdDateIni " +
            "     and log.createdDate <= :createdDateEnd " +
            "group by" +
            "     e.metaExpedient, log.tipus")
        List<ContingutLogCountAggregation<MetaExpedientEntity>> findLogsDocumentBetweenCreatedDateGroupByMetaExpedient(
                @Param("createdDateIni") LocalDateTime createdDateIni,
                @Param("createdDateEnd") LocalDateTime createdDateEnd);
	
	
	@Query( "select   " +
            "    new es.caib.ripea.persistence.aggregation.ContingutLogCountAggregation( " +
            "	     e.metaExpedient, " +
			"	     e.metaExpedient, " +
            "        log.objecteLogTipus, " +
            "        count(log) " +
            "    ) " +
            "from     " +
            "    ContingutLogEntity log, DocumentEntity d JOIN d.expedient e " +
            "where " +
            "         log.contingutId = d.id " +
            "     and log.objecteTipus = es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto.NOTIFICACIO " +
            "     and log.createdDate >= :createdDateIni " +
            "     and log.createdDate <= :createdDateEnd " +
            "group by" +
            "     e.metaExpedient, log.objecteLogTipus")
        List<ContingutLogCountAggregation<MetaExpedientEntity>> findLogsNotificacioBetweenCreatedDateGroupByMetaExpedient(
                @Param("createdDateIni") LocalDateTime createdDateIni,
                @Param("createdDateEnd") LocalDateTime createdDateEnd);

	// Mètodes per evitar errors al tenir continguts orfes en base de dades
	// ////////////////////////////////////////////////////////////////////

	@Modifying
	@Query(value = "delete from ipa_cont_log where contingut_id = :contingutId ", nativeQuery = true)
	int deleteLogsFromContingutsOrfes(@Param("contingutId") Long contingutId);
}
