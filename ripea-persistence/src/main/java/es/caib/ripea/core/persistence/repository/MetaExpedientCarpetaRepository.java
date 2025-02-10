/**
 * 
 */
package es.caib.ripea.core.persistence.repository;

import es.caib.ripea.core.persistence.entity.MetaExpedientCarpetaEntity;
import es.caib.ripea.core.persistence.entity.MetaExpedientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades que representa les carpetes d'un procediment
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaExpedientCarpetaRepository extends JpaRepository<MetaExpedientCarpetaEntity, Long> {

	List<MetaExpedientCarpetaEntity> findByMetaExpedientAndPare(MetaExpedientEntity metaExpedient, MetaExpedientCarpetaEntity pare);
	List<MetaExpedientCarpetaEntity> findByMetaExpedient(MetaExpedientEntity metaExpedient);
}
