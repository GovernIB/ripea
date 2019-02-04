/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientEstatRepository extends JpaRepository<ExpedientEstatEntity, Long> {


	Page<ExpedientEstatEntity> findByMetaExpedientOrderByOrdreAsc(MetaExpedientEntity metaExpedient, Pageable pageable);
	
	List<ExpedientEstatEntity> findByMetaExpedientOrderByOrdreAsc(MetaExpedientEntity metaExpedient);
	
	ExpedientEstatEntity findByMetaExpedientAndOrdre(MetaExpedientEntity metaExpedient, int ordre);
	
	int countByMetaExpedient(MetaExpedientEntity metaExpedient);
	
	
	
	
	
	
}
