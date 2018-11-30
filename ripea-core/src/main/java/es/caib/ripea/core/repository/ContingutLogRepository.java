/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.ContingutLogEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus ContingutLog.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ContingutLogRepository extends JpaRepository<ContingutLogEntity, Long> {

	List<ContingutLogEntity> findByContingutIdOrderByCreatedDateAsc(
			Long contingutId);

}
