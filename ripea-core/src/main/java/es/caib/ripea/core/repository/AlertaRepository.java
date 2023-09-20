/**
 * 
 */
package es.caib.ripea.core.repository;

import es.caib.ripea.core.entity.AlertaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus alerta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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




	// Mètodes per evitar errors al tenir continguts orfes en base de dades
	// ////////////////////////////////////////////////////////////////////

//	@Modifying
//	@Query(value = "delete from ipa_alerta " +
//			" where contingut_id in (" +
//			"	select c.id " +
//			"	  from ipa_contingut n " +
//			"	 where c.id not in (select id from ipa_node) " +
//			"	   and c.id not in (select id from ipa_carpeta))", nativeQuery = true)
//    int deleteAlertesFromContingutsOrfes();

	@Modifying
	@Query(value = "delete from ipa_alerta where contingut_id = :contingutId ", nativeQuery = true)
	int deleteAlertesFromContingutsOrfes(@Param("contingutId") Long contingutId);

}
