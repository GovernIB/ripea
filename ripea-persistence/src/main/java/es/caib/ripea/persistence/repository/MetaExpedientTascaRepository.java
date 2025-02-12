package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientTascaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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


	int deleteByCodi(String codi);

	int deleteMetaExpedientTascaByMetaExpedient(MetaExpedientEntity metaExpedient);

	List<MetaExpedientTascaEntity> findByMetaExpedientAndActivaTrue(MetaExpedientEntity metaExpedient);

	List<MetaExpedientTascaEntity> findByActivaTrue();
	
	MetaExpedientTascaEntity findByMetaExpedientAndCodi(MetaExpedientEntity metaExpedient, String codi);
	
	int countByMetaExpedient(MetaExpedientEntity metaExpedient);
}