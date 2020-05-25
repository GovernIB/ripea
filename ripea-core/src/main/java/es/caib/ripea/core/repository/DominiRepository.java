/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.ripea.core.entity.DominiEntity;
import es.caib.ripea.core.entity.EntitatEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus domini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DominiRepository extends JpaRepository<DominiEntity, Long> {

	List<DominiEntity> findByEntitatOrderByNomAsc(EntitatEntity entitat);
	Page<DominiEntity> findByEntitat(
			EntitatEntity entitat, 
			Pageable pageable);
	DominiEntity findByCodiAndEntitat(String codi, EntitatEntity entitat);

}
