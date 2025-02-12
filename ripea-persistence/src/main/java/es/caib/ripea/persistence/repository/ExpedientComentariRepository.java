/**
 * 
 */
package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.ExpedientComentariEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientComentariRepository extends JpaRepository<ExpedientComentariEntity, Long> {
	
	List<ExpedientComentariEntity> findByExpedientOrderByCreatedDateAsc(
			ExpedientEntity expedient);
	
	@Query(	  "select "
			+ "    count(comment) "
			+ "from "
			+ "    ExpedientComentariEntity comment "
			+ "where "
			+ "    comment.expedient = :expedient")
	long countByExpedient(
			@Param("expedient") ExpedientEntity expedient);	


}
