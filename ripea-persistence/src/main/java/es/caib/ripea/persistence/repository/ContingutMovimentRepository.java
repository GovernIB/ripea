package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.ContingutMovimentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ContingutMovimentRepository extends JpaRepository<ContingutMovimentEntity, Long> {

	List<ContingutMovimentEntity> findByContingutIdOrderByCreatedDateAsc(Long contingutId);

	int countByContingutId(Long contingutId);
	
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