package es.caib.ripea.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.ExpedientComentariEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;

@Component
public interface ExpedientComentariRepository extends JpaRepository<ExpedientComentariEntity, Long> {
	
	List<ExpedientComentariEntity> findByExpedientOrderByCreatedDateAsc(
			ExpedientEntity expedient);
	
	@Query(	  "select "
			+ "    count(comment) "
			+ "from "
			+ "    ExpedientComentariEntity comment "
			+ "where "
			+ "    comment.expedient = :expedient")
	long countByExpedient(
			@Param("expedient") ExpedientEntity expedient);	

	@Modifying
 	@Query(value = "UPDATE IPA_EXP_COMMENT " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}