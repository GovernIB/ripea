/**
 *
 */
package es.caib.ripea.core.persistence.repository;

import es.caib.ripea.core.persistence.aggregation.ContingutLogCountAggregation;
import es.caib.ripea.core.persistence.entity.*;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.dto.TascaEstatEnumDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ExpedientTascaRepository extends JpaRepository<ExpedientTascaEntity, Long> {

	List<ExpedientTascaEntity> findByExpedient(ExpedientEntity expedient, Pageable pageable);

	@Query("select " +
		"    tasca " +
		"from " +
		"    ExpedientTascaEntity tasca " +
		"inner join tasca.responsables responsable " +
		"left join tasca.observadors observador " +
		"where " +
		"	 (responsable = :responsable or observador = :responsable or tasca.delegat = :responsable) " +
		"and (tasca.expedient.esborrat = 0) " +
		"and (:esNullExpedient = true or tasca.expedient = :expedient) " +
		"and (:esNullDataInici = true or tasca.dataInici >= :dataInici) " +
		"and (:esNullDataFi = true or tasca.dataInici <= :dataFi) " +
		"and (:esNullDataLimitInici = true or tasca.dataLimit >= :dataLimitInici) " +
		"and (:esNullDataLimitFi = true or tasca.dataLimit <= :dataLimitFi) " +
		"and (:esNullTitol = true or lower(tasca.titol) like lower('%'||:titol||'%'))" +
		"and (:esNullPrioritat = true or tasca.prioritat = :prioritat) " +
		"and (:esNullMetaExpedientTasca = true or tasca.metaExpedientTasca = :metaExpedientTasca) " +
		"and (:esNullMetaExpedient = true or tasca.metaExpedientTasca.metaExpedient = :metaExpedient) "
	)
	Page<ExpedientTascaEntity> findByResponsable(
		@Param("responsable") UsuariEntity responsable,
		@Param("esNullExpedient") boolean esNullExpedient,
		@Param("expedient") ExpedientEntity expedient,
		@Param("esNullDataInici") boolean esNullDataInici,
		@Param("dataInici") Date dataInici,
		@Param("esNullDataFi") boolean esNullDataFi,
		@Param("dataFi") Date dataFi,
		@Param("esNullDataLimitInici") boolean esNullDataLimitInici,
		@Param("dataLimitInici") Date dataLimitInici,
		@Param("esNullDataLimitFi") boolean esNullDataLimitFi,
		@Param("dataLimitFi") Date dataLimitFi,
		@Param("esNullTitol") boolean esNullTitol,
		@Param("titol") String titol,
		@Param("esNullPrioritat") boolean esNullPrioritat,
		@Param("prioritat") PrioritatEnumDto prioritat,
		@Param("esNullMetaExpedientTasca") boolean esNullMetaExpedientTasca,
		@Param("metaExpedientTasca") MetaExpedientTascaEntity metaExpedientTasca,
		@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
		@Param("metaExpedient") MetaExpedientEntity metaExpedient,
		Pageable pageable);

	@Query("select " +
		"    tasca " +
		"from " +
		"    ExpedientTascaEntity tasca " +
		"inner join tasca.responsables responsable " +
		"left join tasca.observadors observador " +
		"where " +
		"	 (responsable = :responsable or observador = :responsable or tasca.delegat = :responsable) " +
		"and (tasca.estat in (:estats)) " +
		"and (tasca.expedient.esborrat = 0) " +
		"and (:esNullExpedient = true or tasca.expedient = :expedient) " +
		"and (:esNullDataInici = true or tasca.dataInici >= :dataInici) " +
		"and (:esNullDataFi = true or tasca.dataInici <= :dataFi) " +
		"and (:esNullDataLimitInici = true or tasca.dataLimit >= :dataLimitInici) " +
		"and (:esNullDataLimitFi = true or tasca.dataLimit <= :dataLimitFi) " +
		"and (:esNullTitol = true or lower(tasca.titol) like lower('%'||:titol||'%'))" +
		"and (:esNullPrioritat = true or tasca.prioritat = :prioritat) " +
		"and (:esNullMetaExpedientTasca = true or tasca.metaExpedientTasca = :metaExpedientTasca) " +
		"and (:esNullMetaExpedient = true or tasca.metaExpedientTasca.metaExpedient = :metaExpedient) "
	)
	Page<ExpedientTascaEntity> findByResponsableAndEstat(
		@Param("responsable") UsuariEntity responsable,
		@Param("estats") TascaEstatEnumDto[] estats,
		@Param("esNullExpedient") boolean esNullExpedient,
		@Param("expedient") ExpedientEntity expedient,
		@Param("esNullDataInici") boolean esNullDataInici,
		@Param("dataInici") Date dataInici,
		@Param("esNullDataFi") boolean esNullDataFi,
		@Param("dataFi") Date dataFi,
		@Param("esNullDataLimitInici") boolean esNullDataLimitInici,
		@Param("dataLimitInici") Date dataLimitInici,
		@Param("esNullDataLimitFi") boolean esNullDataLimitFi,
		@Param("dataLimitFi") Date dataLimitFi,
		@Param("esNullTitol") boolean esNullTitol,
		@Param("titol") String titol,
		@Param("esNullPrioritat") boolean esNullPrioritat,
		@Param("prioritat") PrioritatEnumDto prioritat,
		@Param("esNullMetaExpedientTasca") boolean esNullMetaExpedientTasca,
		@Param("metaExpedientTasca") MetaExpedientTascaEntity metaExpedientTasca,
		@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
		@Param("metaExpedient") MetaExpedientEntity metaExpedient,
		Pageable pageable);

	@Query("select " +
		"    new es.caib.ripea.core.aggregation.ContingutLogCountAggregation( " +
		"	     responsable, " +
		"	     e.metaExpedient, " +
		"        count(tasca) " +
		"    ) " +
		"from " +
		"    ExpedientTascaEntity tasca JOIN tasca.expedient e " +
		"inner join tasca.responsables responsable " +
		"where " +
		"    tasca.estat IN :estats " +
		"group by" +
		"    responsable, e.metaExpedient")
	List<ContingutLogCountAggregation<UsuariEntity>> countByResponsableAndEstat(
		@Param("estats") TascaEstatEnumDto[] estats);

	@Query("select " +
		"    count(tasca) " +
		"from " +
		"    ExpedientTascaEntity tasca " +
		"join tasca.responsables responsable " +
		"left join tasca.observadors observador " +
		"where " +
		"       (responsable = :responsable or observador = :responsable or tasca.delegat = :responsable) " +
		"    and (tasca.estat='PENDENT' or tasca.estat='INICIADA')")
	long countTasquesPendents(
		@Param("responsable") UsuariEntity responsable);


	@Query("select et from " +
		"    ExpedientTascaEntity et " +
		"left join et.responsables responsable " +
		"where " +
		"    (et.expedient.entitat = :entitat) " +
		"and (:esNullExpedientNom = true or lower(et.expedient.nom) like lower('%'||:expedientNom||'%')) " +
		"and (:esNullMetaTasca = true or et.metaExpedientTasca = :metaTasca) " +
		"and (:esNullDataInici = true or et.createdDate >= :dataInici) " +
		"and (:esNullDataFinal = true or et.createdDate <= :dataFinal) " +
		"and (:esNullResponsable = true or responsable = :responsable)" +
		"and (:esNullEstat = true or et.estat = :estat) ")
	public Page<ExpedientTascaEntity> findAmbFiltrePaginat(
		@Param("entitat") EntitatEntity entitat,
		@Param("esNullExpedientNom") boolean esNullExpedientNom,
		@Param("expedientNom") String expedientNom,
		@Param("esNullMetaTasca") boolean esNullMetaTasca,
		@Param("metaTasca") MetaExpedientTascaEntity metaTasca,
		@Param("esNullDataInici") boolean esNullDataInici,
		@Param("dataInici") Date dataInici,
		@Param("esNullDataFinal") boolean esNullDataFinal,
		@Param("dataFinal") Date dataFinal,
		@Param("esNullResponsable") boolean esNullResponsable,
		@Param("responsable") UsuariEntity responsable,
		@Param("esNullEstat") boolean esNullEstat,
		@Param("estat") TascaEstatEnumDto estat,
		Pageable paginacio);


}
