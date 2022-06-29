/**
 * 
 */
package es.caib.ripea.core.repository;

import es.caib.ripea.core.entity.ContingutComentariEntity;
import es.caib.ripea.core.entity.ContingutEntity;
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
public interface ContingutComentariRepository extends JpaRepository<ContingutComentariEntity, Long> {

	List<ContingutComentariEntity> findByContingutOrderByCreatedDateAsc(
			ContingutEntity contingut);
	
	@Query(	  "select "
			+ "    count(comment) "
			+ "from "
			+ "    ContingutComentariEntity comment "
			+ "where "
			+ "    comment.contingut = :contingut")
	long countByContingut(
			@Param("contingut") ContingutEntity contingut);



	// Mètodes per evitar errors al tenir continguts orfes en base de dades
	// ////////////////////////////////////////////////////////////////////

	@Modifying
	@Query(value = "delete from ipa_cont_comment where contingut_id = :contingutId ", nativeQuery = true)
	int deleteComentarisFromContingutsOrfes(@Param("contingutId") Long contingutId);
}
