/**
 * 
 */
package es.caib.ripea.core.repository;

import es.caib.ripea.core.persistence.AvisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Repositori per gestionar una entitat de base de dades del tipus avís.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AvisRepository extends JpaRepository<AvisEntity, Long> {

	@Query(	"from " +
			"    AvisEntity a " +
			"where " +
			"    a.actiu = true " +
			"and (a.entitatId is null or a.entitatId = :entitatId)" +
			"and a.dataInici <= :currentDate " +
			"and a.dataFinal >= :currentDate")
	List<AvisEntity> findActiveAdmin(@Param("currentDate") Date currentDate, @Param("entitatId") Long entitatId);

	@Query(	"from " +
			"    AvisEntity a " +
			"where " +
			"    a.actiu = true " +
			"and a.avisAdministrador = false " +
			"and a.dataInici <= :currentDate " +
			"and a.dataFinal >= :currentDate")
	List<AvisEntity> findActive(@Param("currentDate") Date currentDate);

	List<AvisEntity> findByEntitatIdAndAssumpte(Long entitatId, String assumpte);
}
