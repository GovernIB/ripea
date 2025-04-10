/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.MetaExpedientCarpetaEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades que representa les carpetes d'un procediment
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaExpedientCarpetaRepository extends JpaRepository<MetaExpedientCarpetaEntity, Long> {

	List<MetaExpedientCarpetaEntity> findByMetaExpedientAndPare(MetaExpedientEntity metaExpedient, MetaExpedientCarpetaEntity pare);
	List<MetaExpedientCarpetaEntity> findByMetaExpedient(MetaExpedientEntity metaExpedient);
	
	@Modifying
 	@Query(value = "UPDATE IPA_METAEXPEDIENT_CARPETA " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
 	void updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
	
	@Modifying
 	@Query(value = "UPDATE IPA_METAEXPEDIENT_METADOCUMENT " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
 	void updateUsuariAuditoriaMetaDoc(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}
