/**
 * 
 */
package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.ExpedientTascaComentariEntity;
import es.caib.ripea.persistence.entity.ExpedientTascaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus expedientTascaComentari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientTascaComentariRepository extends JpaRepository<ExpedientTascaComentariEntity, Long> {
	
	List<ExpedientTascaComentariEntity> findByExpedientTascaOrderByCreatedDateAsc(ExpedientTascaEntity expedientTasca);
	
	@Query(	  "select "
			+ "    count(comment) "
			+ "from "
			+ "    ExpedientTascaComentariEntity comment "
			+ "where "
			+ "    comment.expedientTasca = :expedientTasca")
	long countByExpedientTasca(
            @Param("expedientTasca") ExpedientTascaEntity expedientTasca);


}
