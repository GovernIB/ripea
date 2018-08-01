/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus meta-dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaDadaRepository extends JpaRepository<MetaDadaEntity, Long> {

	MetaDadaEntity findByEntitatAndCodi(EntitatEntity entitat, String codi);

	List<MetaDadaEntity> findByEntitat(EntitatEntity entitat, Sort sort);
	List<MetaDadaEntity> findByEntitat(EntitatEntity entitat, Pageable pageable);

	@Query(	"from " +
			"    MetaDadaEntity md " +
			"where " +
			"    md.entitat = ?1 " +
			"and md.activa = true " +
			"and (md.globalExpedient = false or (?2 = true and md.globalExpedient = true)) " +
			"and (md.globalDocument = false or (?3 = true or md.globalDocument = true)) " +
			"order by " +
			"    md.nom asc")
	List<MetaDadaEntity> findByEntitatAndActivaTrueAndGlobalsOrderByNomAsc(
			EntitatEntity entitat,
			boolean incloureGlobalsExpedient,
			boolean incloureGlobalsDocument);

	List<MetaDadaEntity> findByEntitatAndGlobalExpedientTrueOrderByIdAsc(EntitatEntity entitat);

	List<MetaDadaEntity> findByEntitatAndGlobalExpedientTrueAndActivaTrueOrderByIdAsc(
			EntitatEntity entitat);

	List<MetaDadaEntity> findByEntitatAndGlobalDocumentTrueOrderByIdAsc(EntitatEntity entitat);

	List<MetaDadaEntity> findByEntitatAndGlobalDocumentTrueAndActivaTrueOrderByIdAsc(
			EntitatEntity entitat);
	
}
