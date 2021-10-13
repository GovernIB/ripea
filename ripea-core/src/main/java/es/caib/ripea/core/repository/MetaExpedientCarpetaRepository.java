/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.ripea.core.entity.MetaExpedientCarpetaEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;

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
