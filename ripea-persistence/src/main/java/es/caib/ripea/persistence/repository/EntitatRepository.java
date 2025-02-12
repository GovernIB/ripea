/**
 * 
 */
package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.EntitatEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatRepository extends JpaRepository<EntitatEntity, Long> {

	EntitatEntity findByCodi(String codi);

	EntitatEntity findByUnitatArrel(String unitatArrel);

	List<EntitatEntity> findByActiva(boolean activa);

	Page<EntitatEntity> findBy(
			Pageable pageable);

	List<EntitatEntity> findBy(
			Sort sort);

	@Query("select org.entitat " + 
			"from " + 
			"    OrganGestorEntity org " + 
			" where org.id in (:ids)")
	public List<EntitatEntity> findByOrgansIds(@Param("ids") List<Long> ids);

}
