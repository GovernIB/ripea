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
import es.caib.ripea.core.entity.MetaExpedientDominiEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus meta-expedient-domini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaExpedientDominiRepository extends JpaRepository<MetaExpedientDominiEntity, Long> {

	@Query(	"from " +
			"    MetaExpedientDominiEntity met " +
			"where " +
			"    met.metaExpedient.entitat = :entitat " +
			"and met.metaExpedient = :metaExpedient " +
			"and (:esNullFiltre = true or lower(met.codi) like lower('%'||:filtre||'%') or lower(met.nom) like lower('%'||:filtre||'%')) ")
	Page<MetaExpedientDominiEntity> findByEntitatAndMetaExpedientAndFiltre(
			@Param("entitat") EntitatEntity entitat, 
			@Param("metaExpedient") MetaExpedientEntity metaExpedient, 
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,	
			Pageable pageable);
	
	List<MetaExpedientDominiEntity> findByEntitat(
			EntitatEntity entitat);
	
	List<MetaExpedientDominiEntity> findByEntitatAndMetaExpedient(
			EntitatEntity entitat, 
			MetaExpedientEntity metaExpedient);
	
	int countByMetaExpedient(MetaExpedientEntity metaExpedient);

}
