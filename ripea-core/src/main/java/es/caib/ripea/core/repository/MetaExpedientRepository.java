/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaExpedientRepository extends JpaRepository<MetaExpedientEntity, Long> {

	MetaExpedientEntity findByEntitatAndCodi(EntitatEntity entitat, String codi);

	List<MetaExpedientEntity> findByEntitatOrderByNomAsc(EntitatEntity entitat);
	
	List<MetaExpedientEntity> findByEntitat(EntitatEntity entitat);
	
	
	@Query(	"from " +
			"    MetaExpedientEntity me " +
			"where " +
			"    me.entitat = :entitat " +
			"and (:esNullFiltre = true or lower(me.codi) like lower('%'||:filtre||'%') or lower(me.nom) like lower('%'||:filtre||'%')) ")
	List<MetaExpedientEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,	
			Sort sort);
	
	
	@Query(	"from " +
			"    MetaExpedientEntity me " +
			"where " +
			"    me.entitat = :entitat " +
			"and (:esNullFiltre = true or lower(me.codi) like lower('%'||:filtre||'%') or lower(me.nom) like lower('%'||:filtre||'%')) ")
	Page<MetaExpedientEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,	
			Pageable pageable);
	
	
	List<MetaExpedientEntity> findByEntitatAndActiuTrueOrderByNomAsc(EntitatEntity entitat);
	
	List<MetaExpedientEntity> findByEntitatAndClassificacioSia(EntitatEntity entitat, String classificacioSia);
	

}
