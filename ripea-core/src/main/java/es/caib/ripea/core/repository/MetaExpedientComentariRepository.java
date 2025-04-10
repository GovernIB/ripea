/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.MetaExpedientComentariEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;


public interface MetaExpedientComentariRepository extends JpaRepository<MetaExpedientComentariEntity, Long> {
	
	List<MetaExpedientComentariEntity> findByMetaExpedientOrderByCreatedDateAsc(
			MetaExpedientEntity metaExpedient);
	
	@Query(	  "select "
			+ "    count(comment) "
			+ "from "
			+ "    MetaExpedientComentariEntity comment "
			+ "where "
			+ "    comment.metaExpedient = :metaExpedient")
	long countByMetaExpedient(
			@Param("metaExpedient") MetaExpedientEntity metaExpedient);	
	
	
	public List<MetaExpedientComentariEntity> findByEmailEnviatFalseAndCreatedDateGreaterThan(Date createdDate);

	@Modifying
 	@Query(value = "UPDATE IPA_METAEXP_COMMENT " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
 	void updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}
