/**
 * 
 */
package es.caib.ripea.core.repository;

import es.caib.ripea.core.entity.ContingutMovimentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus ContingutMoviment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ContingutMovimentRepository extends JpaRepository<ContingutMovimentEntity, Long> {

	List<ContingutMovimentEntity> findByContingutIdOrderByCreatedDateAsc(Long contingutId);

	// Mètodes per evitar errors al tenir continguts orfes en base de dades
	// ////////////////////////////////////////////////////////////////////

	@Modifying
	@Query(value = "delete from ipa_cont_mov where contingut_id = :contingutId ", nativeQuery = true)
	int deleteMovimentsFromContingutsOrfes(@Param("contingutId") Long contingutId);

	 @Modifying
     @Query(value = "UPDATE IPA_CONT_MOV SET REMITENT_CODI = :codiNou WHERE REMITENT_CODI = :codiAntic", nativeQuery = true)
	 public int updateRemitentCodi(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
	 
	@Modifying
 	@Query(value = "UPDATE IPA_CONT_MOV " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}