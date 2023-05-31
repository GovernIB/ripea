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
import es.caib.ripea.core.entity.TipusDocumentalEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus meta-document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TipusDocumentalRepository extends JpaRepository<TipusDocumentalEntity, Long> {

	List<TipusDocumentalEntity> findByEntitatOrderByNomEspanyolAsc(EntitatEntity entitat);
	
	TipusDocumentalEntity findByCodi(String codi);
	
	@Query(	"from " +
			"    TipusDocumentalEntity tipusDocumental " +
			"where " +
			"    tipusDocumental.entitat = :entitat " +
			"and (:esNullFiltre = true or lower(tipusDocumental.codi) like lower('%'||:filtre||'%') or lower(tipusDocumental.nomEspanyol) like lower('%'||:filtre||'%') or lower(tipusDocumental.nomCatala) like lower('%'||:filtre||'%')) ")
	Page<TipusDocumentalEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,	
			Pageable pageable);
	TipusDocumentalEntity findByCodiAndEntitat(String codi, EntitatEntity entitat);

}
