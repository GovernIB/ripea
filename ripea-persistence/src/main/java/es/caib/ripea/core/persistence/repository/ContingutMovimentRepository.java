/**
 * 
 */
package es.caib.ripea.core.persistence.repository;

import es.caib.ripea.core.persistence.entity.ContingutMovimentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus ContingutMoviment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ContingutMovimentRepository extends JpaRepository<ContingutMovimentEntity, Long> {

	List<ContingutMovimentEntity> findByContingutIdOrderByCreatedDateAsc(
			Long contingutId);

	// Mètodes per evitar errors al tenir continguts orfes en base de dades
	// ////////////////////////////////////////////////////////////////////

	@Modifying
	@Query(value = "delete from ipa_cont_mov where contingut_id = :contingutId ", nativeQuery = true)
	int deleteMovimentsFromContingutsOrfes(@Param("contingutId") Long contingutId);

}
