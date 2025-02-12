/**
 * 
 */
package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.ExpedientOrganPareEntity;
import es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus òrgan pare d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientOrganPareRepository extends JpaRepository<ExpedientOrganPareEntity, Long> {

	@Query(	"select " +
			"    eop.metaExpedientOrganGestor.organGestor " + 
			"from " +
			"    ExpedientOrganPareEntity eop " +
			"where " +
			"    eop.expedient.id = :expedientId")
	List<OrganGestorEntity> findOrganGestorByExpedientId(
			@Param("expedientId") Long expedientId);

	@Query(	"select " +
			"    eop.metaExpedientOrganGestor " + 
			"from " +
			"    ExpedientOrganPareEntity eop " +
			"where " +
			"    eop.expedient.id = :expedientId")
	List<MetaExpedientOrganGestorEntity> findMetaExpedientOrganGestorByExpedientId(
			@Param("expedientId") Long expedientId);

}
