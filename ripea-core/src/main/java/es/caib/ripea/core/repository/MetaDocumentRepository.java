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

import es.caib.ripea.core.api.dto.MetaDocumentTipusGenericEnumDto;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus meta-document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaDocumentRepository extends JpaRepository<MetaDocumentEntity, Long> {

	MetaDocumentEntity findByMetaExpedientAndCodi(
			MetaExpedientEntity metaExpedient,
			String codi);

	@Query(	"from " +
			"    MetaDocumentEntity md " +
			"where " +
			"    md.metaExpedient is null " +
			"and lower(:codi) = lower(md.codi)")
	MetaDocumentEntity findByMetaExpedientAndCodi(
			@Param("codi") String codi);
	
	List<MetaDocumentEntity> findByMetaExpedient(
			MetaExpedientEntity metaExpedient);
	
	@Query(	"from " +
			"    MetaDocumentEntity md " +
			"where " +
			"    md.metaExpedient = :metaExpedient " +
			"and (:esNullFiltre = true or lower(md.codi) like lower('%'||:filtre||'%') or lower(md.nom) like lower('%'||:filtre||'%')) ")
	Page<MetaDocumentEntity> findByMetaExpedient(
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,	
			Pageable pageable);
	
	@Query(	"from " +
			"    MetaDocumentEntity md " +
			"where " +
			"    md.metaExpedient = :metaExpedient " +
			"and (:esNullFiltre = true or lower(md.codi) like lower('%'||:filtre||'%') or lower(md.nom) like lower('%'||:filtre||'%')) ")
	List<MetaDocumentEntity> findByMetaExpedient(
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,	
			Sort sort);
	
	@Query(	"from " +
			"    MetaDocumentEntity md " +
			"where " +
			"    md.metaExpedient is null " +
			"and (:esNullFiltre = true or lower(md.codi) like lower('%'||:filtre||'%') or lower(md.nom) like lower('%'||:filtre||'%')) ")
	List<MetaDocumentEntity> findWithoutMetaExpedient(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,	
			Sort sort);

	
	List<MetaDocumentEntity> findByMetaExpedientIdIn(
			List<Long> metaExpedientIds);

	List<MetaDocumentEntity> findByEntitat(
			EntitatEntity entitat);

	@Query(	"from " +
			"    MetaDocumentEntity md " +
			"where " +
			"   (:esNullEntitat = true or md.entitat = :entitat) " +
			"and md.metaDocumentTipusGeneric = :metaDocumentTipusGeneric")
	MetaDocumentEntity findByEntitatAndTipusGeneric(
			@Param("esNullEntitat") boolean esNullEntitat,
			@Param("entitat") EntitatEntity entitat,
			@Param("metaDocumentTipusGeneric") MetaDocumentTipusGenericEnumDto metaDocumentTipusGeneric);
	
	@Query(	"from " +
			"    MetaDocumentEntity md " +
			"where (md.metaExpedient = :metaExpedient) " +
			"and (md.firmaPortafirmesActiva = 1) "+
			"and (md.portafirmesResponsables != null or md.portafirmesFluxId != null)")
	List<MetaDocumentEntity> findByMetaExpedientAndFirmaPortafirmesActivaAmbFluxOResponsable(
			@Param("metaExpedient") MetaExpedientEntity metaExpedient);
}
