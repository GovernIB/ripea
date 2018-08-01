/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.ContingutComentariEntity;
import es.caib.ripea.core.entity.ContingutEntity;

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

}
