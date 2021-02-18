/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;

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

	List<MetaExpedientOrganGestorEntity> findByMetaExpedient(MetaExpedientEntity metaExpedient);

	@Query(	"select " +
			"    meog.organGestor.id " + 
			"from " +
			"    MetaExpedientOrganGestorEntity meog " +
			"where meog.metaExpedient = :metaExpedient ")
	List<Long> findOrganGestorIdByMetaExpedient(
			@Param("metaExpedient") MetaExpedientEntity metaExpedient);

}
