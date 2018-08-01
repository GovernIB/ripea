/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.AlertaEntity;
import es.caib.ripea.core.entity.ContingutEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus alerta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AlertaRepository extends JpaRepository<AlertaEntity, Long> {

	Page<AlertaEntity> findByLlegidaAndContingutId(
			boolean llegida,
			Long id,
			Pageable pageable);
	
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

	@Query("select " +
			"   count(a) " +
			"from " +
			"   AlertaEntity a " +
			"where " +
			"   a.contingut in :continguts " +
			"AND " +
			"   a.llegida = :llegida")
	long countByLlegidaAndContinguts(
			@Param("llegida") boolean llegida,
			@Param("continguts") List<ContingutEntity> continguts);

	@Query("select " +
			"   a " +
			"from " +
			"   AlertaEntity a " +
			"where " +
			"   a.contingut in :continguts " +
			"AND " +
			"   a.llegida = :llegida")
	Page<AlertaEntity> findByLlegidaAndContinguts(
			@Param("llegida") boolean llegida,
			@Param("continguts") List<ContingutEntity> continguts,
			Pageable pageable);

	@Query("select " +
			"   a " +
			"from " +
			"   AlertaEntity a " +
			"where " +
			"   a.contingut in :continguts " +
			"AND " +
			"   a.llegida = :llegida")
	List<AlertaEntity> findByLlegidaAndContinguts(
			@Param("llegida") boolean llegida,
			@Param("continguts") List<ContingutEntity> continguts,
			Sort sort);

}
