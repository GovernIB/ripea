/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.aggregation.ContingutLogCountAggregation;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientTascaEntity;
import es.caib.ripea.core.entity.MetaExpedientTascaEntity;
import es.caib.ripea.core.entity.UsuariEntity;

public interface ExpedientTascaRepository extends JpaRepository<ExpedientTascaEntity, Long> {

	
	List<ExpedientTascaEntity> findByExpedient(
			ExpedientEntity expedient);
	
	@Query(	"select " +
			"    tasca " +
			"from " +
			"    ExpedientTascaEntity tasca " +
			"inner join tasca.responsables responsable " +
			"where responsable = :responsable " +
			"      and (tasca.estat='PENDENT' or tasca.estat='INICIADA')")
	List<ExpedientTascaEntity> findByResponsableAndEstat(
			@Param("responsable") UsuariEntity responsable,
			Pageable pageable);
	
	@Query(	"select " +
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
	
	@Query(	"select " +
			"    count(tasca) " +
			"from " +
			"    ExpedientTascaEntity tasca " +
			"join tasca.responsables responsable " +
			"where " +
			"       responsable = :responsable " +
			"    and (tasca.estat='PENDENT' or tasca.estat='INICIADA')")
	long countTasquesPendents(
			@Param("responsable") UsuariEntity responsable);
	
	

	
	@Query(	"select et from " +
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
