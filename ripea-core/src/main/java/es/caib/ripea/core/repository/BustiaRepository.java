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

import es.caib.ripea.core.entity.BustiaEntity;
import es.caib.ripea.core.entity.EntitatEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus bústia.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface BustiaRepository extends JpaRepository<BustiaEntity, Long> {

	List<BustiaEntity> findByEntitatAndPareNotNull(EntitatEntity entitat);
	
	List<BustiaEntity> findByEntitatAndActivaTrueAndPareNotNull(EntitatEntity entitat);

	List<BustiaEntity> findByEntitatAndUnitatCodiAndPareNotNull(
			EntitatEntity entitat,
			String unitatCodi);

	BustiaEntity findByEntitatAndUnitatCodiAndPareNull(
			EntitatEntity entitat,
			String unitatCodi);

	BustiaEntity findByEntitatAndUnitatCodiAndPerDefecteTrue(
			EntitatEntity entitat,
			String unitatCodi);

	@Query(	"from " +
			"    BustiaEntity b " +
			"where " +
			"    b.entitat = :entitat " +
			"and (b.id in (:bustiaIds)) " +
			"and (:esNullFiltre = true or lower(b.nom) like lower('%'||:filtre||'%') or lower(b.pare.nom) like lower('%'||:filtre||'%')) ")
	Page<BustiaEntity> findByEntitatAndIdsAndFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("bustiaIds") List<Long> bustiaIds, 
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);
	
	@Query(	"from " +
			"    BustiaEntity b " +
			"where " +
			"    b.entitat = :entitat " +
			"and (:esNullFiltreUnitat = true or b.unitatCodi = :unitatCodi) " +
			"and (:esNullFiltreNom = true or lower(b.nom) like lower('%'||:filtreNom||'%') or lower(b.pare.nom) like lower('%'||:filtreNom||'%')) ")
	Page<BustiaEntity> findByEntitatAndUnitatCodiAndBustiaNomFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltreUnitat") boolean esNullFiltreUnitat,
			@Param("unitatCodi") String unitatCodi, 
			@Param("esNullFiltreNom") boolean esNullFiltreNom,
			@Param("filtreNom") String filtreNom,		
			Pageable pageable);
}
