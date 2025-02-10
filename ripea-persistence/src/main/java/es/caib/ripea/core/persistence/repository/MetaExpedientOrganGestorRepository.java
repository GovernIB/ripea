/**
 * 
 */
package es.caib.ripea.core.persistence.repository;

import es.caib.ripea.core.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.core.persistence.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.persistence.entity.OrganGestorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus relació meta-expedient - òrgan.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaExpedientOrganGestorRepository extends JpaRepository<MetaExpedientOrganGestorEntity, Long> {

	MetaExpedientOrganGestorEntity findByMetaExpedientAndOrganGestor(
			MetaExpedientEntity metaExpedient,
			OrganGestorEntity organGestor);
	
	MetaExpedientOrganGestorEntity findByMetaExpedientIdAndOrganGestorId(
			Long metaExpedientId,
			Long organGestorId);

	List<MetaExpedientOrganGestorEntity> findByMetaExpedient(MetaExpedientEntity metaExpedient);
	
	@Query(	"select distinct " +
			"    meog.organGestor.codi " +
			"from " +
			"    MetaExpedientOrganGestorEntity meog " +
			"where " +
			"    meog.id in (:metaExpedientOrganGestorIds) ")
	public List<String> findOrganGestorCodisByMetaExpedientOrganGestorIds(
			@Param("metaExpedientOrganGestorIds") List<Long> metaExpedientOrganGestorIds);

	@Query(	"select distinct " +
			"    meog.organGestor.codi " +
			"from " +
			"    MetaExpedientOrganGestorEntity meog " +
			"where " +
			"    meog in (:metaExpedientOrganGestors) ")
	public List<String> findOrganGestorCodisByMetaExpedientOrganGestors(
			@Param("metaExpedientOrganGestors") List<MetaExpedientOrganGestorEntity> metaExpedientOrganGestors);
	

	@Query(	"select count(meog.id) from MetaExpedientOrganGestorEntity meog where meog.organGestor = :organGestor")
	Integer countByOrganGestor(@Param("organGestor") OrganGestorEntity organGestor);
}
