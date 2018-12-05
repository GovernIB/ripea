/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.ripea.core.entity.ExpedientComentariEntity;
import es.caib.ripea.core.entity.ExpedientEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientComentariRepository extends JpaRepository<ExpedientComentariEntity, Long> {
	
	List<ExpedientComentariEntity> findByExpedientOrderByCreatedDateAsc(
			ExpedientEntity expedient);


}
