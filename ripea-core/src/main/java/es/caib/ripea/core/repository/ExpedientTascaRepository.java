/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientTascaEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
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
			"    count(tasca) " +
			"from " +
			"    ExpedientTascaEntity tasca " +
			"where " +
			"     tasca.responsable = :responsable " +
			" and tasca.expedient.metaExpedient = :metaExpedient " +
			" and tasca.estat IN :estats")
	long countByResponsableAndEstat(
			@Param("responsable") UsuariEntity responsable,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
			@Param("estats") TascaEstatEnumDto[] estats);
	
	@Query(	"select " +
			"    count(tasca) " +
			"from " +
			"    ExpedientTascaEntity tasca " +
			"where tasca.responsable = :responsable " +
			"and (tasca.estat='PENDENT' or tasca.estat='INICIADA')")
	public long countTasquesPendents(
			@Param("responsable") UsuariEntity responsable);

}
