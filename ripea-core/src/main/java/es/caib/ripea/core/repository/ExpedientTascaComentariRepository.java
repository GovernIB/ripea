/**
 * 
 */
package es.caib.ripea.core.repository;

import es.caib.ripea.core.entity.ExpedientTascaComentariEntity;
import es.caib.ripea.core.entity.ExpedientTascaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus expedientTascaComentari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientTascaComentariRepository extends JpaRepository<ExpedientTascaComentariEntity, Long> {
	
	List<ExpedientTascaComentariEntity> findByExpedientTascaOrderByCreatedDateAsc(ExpedientTascaEntity expedientTasca);
	
	@Query(	  "select "
			+ "    count(comment) "
			+ "from "
			+ "    ExpedientTascaComentariEntity comment "
			+ "where "
			+ "    comment.expedientTasca = :expedientTasca")
	long countByExpedientTasca(
            @Param("expedientTasca") ExpedientTascaEntity expedientTasca);

	@Modifying
 	@Query(value = "UPDATE IPA_EXP_TASCA_COMMENT " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}
