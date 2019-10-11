/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientTascaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus meta-expedient-tasca.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaExpedientTascaRepository extends JpaRepository<MetaExpedientTascaEntity, Long> {

	@Query(	"from " +
			"    MetaExpedientTascaEntity met " +
			"where " +
			"    met.metaExpedient.entitat = :entitat " +
			"and met.metaExpedient = :metaExpedient " +
			"and (:esNullFiltre = true or lower(met.codi) like lower('%'||:filtre||'%') or lower(met.nom) like lower('%'||:filtre||'%')) ")
	Page<MetaExpedientEntity> findByEntitatAndMetaExpedientAndFiltre(
			@Param("entitat") EntitatEntity entitat, 
			@Param("metaExpedient") MetaExpedientEntity metaExpedient, 
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,	
			Pageable pageable);
	
	List<MetaExpedientTascaEntity> findByMetaExpedientAndActivaTrue(
			MetaExpedientEntity metaExpedient);
	
	
	int countByMetaExpedient(MetaExpedientEntity metaExpedient);

}
