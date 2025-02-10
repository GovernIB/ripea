/**
 * 
 */
package es.caib.ripea.core.persistence.repository;

import es.caib.ripea.core.persistence.entity.ExpedientCarpetaArbreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar la vista que representra les carpetes d'un expedient
 * en arbre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientCarpetaArbreRepository extends JpaRepository<ExpedientCarpetaArbreEntity, Long> {

	@Query(	"select c " +
			"from " +
			"    ExpedientCarpetaArbreEntity c " +
			"where " +
			"	c.entitat = :entitat " +
			"	and (" + 
			"			(c.pare is null and c.expedient.id = :pare) " + 
			"				or (c.pare = :pare))")
	List<ExpedientCarpetaArbreEntity> findByPare(
			@Param("entitat") Long entitat, 
			@Param("pare") Long pare);

}
