/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.aggregation.ContingutLogCountAggregation;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientTascaEntity;
import es.caib.ripea.core.entity.UsuariEntity;

public interface ExpedientTascaRepository extends JpaRepository<ExpedientTascaEntity, Long> {

	
	List<ExpedientTascaEntity> findByExpedient(
			ExpedientEntity expedient);
	
	@Query(	"select " +
			"    tasca " +
			"from " +
			"    ExpedientTascaEntity tasca " +
			"where tasca.responsable = :responsable " +
			"      and (tasca.estat='PENDENT' or tasca.estat='INICIADA')")
	List<ExpedientTascaEntity> findByResponsableAndEstat(
			@Param("responsable") UsuariEntity responsable,
			Pageable pageable);
	
	@Query(	"select " +
            "    new es.caib.ripea.core.aggregation.ContingutLogCountAggregation( " +
            "	     tasca.responsable, " +
			"	     e.metaExpedient, " +
            "        count(tasca) " +
            "    ) " +
			"from " +
			"    ExpedientTascaEntity tasca JOIN tasca.expedient e " +
			"where " +
			"    tasca.estat IN :estats " +
			"group by" +
			"    tasca.responsable, e.metaExpedient")
    List<ContingutLogCountAggregation<UsuariEntity>> countByResponsableAndEstat(
			@Param("estats") TascaEstatEnumDto[] estats);
	
	@Query(	"select " +
			"    count(tasca) " +
			"from " +
			"    ExpedientTascaEntity tasca " +
			"where " +
			"        tasca.responsable = :responsable " +
			"    and (tasca.estat='PENDENT' or tasca.estat='INICIADA')")
	long countTasquesPendents(
			@Param("responsable") UsuariEntity responsable);

}
