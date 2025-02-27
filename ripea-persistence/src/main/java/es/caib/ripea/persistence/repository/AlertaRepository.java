package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.AlertaEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AlertaRepository extends JpaRepository<AlertaEntity, Long> {
	
	List<AlertaEntity> findByLlegidaAndContingutId(
			boolean llegida,
			Long id,
			Sort sort);

	@Query("select " +
			"   count(a) " +
			"from " +
			"   AlertaEntity a " +
			"where " +
			"   a.contingut.id = :id " +
			"AND " +
			"   a.llegida = :llegida")
	long countByLlegidaAndContingutId(
			@Param("llegida") boolean llegida,
			@Param("id") Long id);

	@Modifying
	@Query(value = "delete from ipa_alerta where contingut_id = :contingutId ", nativeQuery = true)
	int deleteAlertesFromContingutsOrfes(@Param("contingutId") Long contingutId);
}