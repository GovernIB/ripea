package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.AvisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
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
