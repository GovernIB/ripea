/**
 * 
 */
package es.caib.ripea.core.repository;

import es.caib.ripea.core.entity.AlertaEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus alerta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AlertaRepository extends JpaRepository<AlertaEntity, Long> {
	
	List<AlertaEntity> findByLlegidaAndContingutId(
			boolean llegida,
			Long id,
			Sort sort);

	@Query("select " +
			"   count(a) " +
			"from " +
			"   AlertaEntity a " +
			"where " +
			"   a.contingut.id = :id " +
			"AND " +
			"   a.llegida = :llegida")
	long countByLlegidaAndContingutId(
			@Param("llegida") boolean llegida,
			@Param("id") Long id);

	@Modifying
	@Query(value = "delete from ipa_alerta where contingut_id = :contingutId ", nativeQuery = true)
	int deleteAlertesFromContingutsOrfes(@Param("contingutId") Long contingutId);

	@Modifying
 	@Query(value = "UPDATE IPA_ALERTA " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
 	void updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}
