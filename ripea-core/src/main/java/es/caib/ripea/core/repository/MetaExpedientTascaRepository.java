package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientTascaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus meta-expedient-tasca.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaExpedientTascaRepository extends JpaRepository<MetaExpedientTascaEntity, Long> {

	@Query(	"from " +
			"    MetaExpedientTascaEntity met " +
			"where " +
			"    met.metaExpedient.entitat = :entitat " +
			"and met.metaExpedient = :metaExpedient " +
			"and (:esNullFiltre = true or lower(met.codi) like lower('%'||:filtre||'%') or lower(met.nom) like lower('%'||:filtre||'%')) ")
	Page<MetaExpedientEntity> findByEntitatAndMetaExpedientAndFiltre(
			@Param("entitat") EntitatEntity entitat, 
			@Param("metaExpedient") MetaExpedientEntity metaExpedient, 
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,	
			Pageable pageable);

	@Modifying
 	@Query(value = "UPDATE IPA_METAEXP_TASCA " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);

	 @Modifying
     @Query(value = "UPDATE IPA_METAEXP_TASCA SET RESPONSABLE = :codiNou WHERE RESPONSABLE = :codiAntic", nativeQuery = true)
	 public int updateUsuariResponsable(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
	
	int deleteByCodi(String codi);

	int deleteMetaExpedientTascaByMetaExpedient(MetaExpedientEntity metaExpedient);

	List<MetaExpedientTascaEntity> findByMetaExpedientAndActivaTrue(MetaExpedientEntity metaExpedient);

	List<MetaExpedientTascaEntity> findByActivaTrue();
	
	MetaExpedientTascaEntity findByMetaExpedientAndCodi(MetaExpedientEntity metaExpedient, String codi);
	
	int countByMetaExpedient(MetaExpedientEntity metaExpedient);
}