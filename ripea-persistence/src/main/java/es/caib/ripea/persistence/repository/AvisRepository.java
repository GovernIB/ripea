package es.caib.ripea.persistence.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.AvisEntity;

@Component
public interface AvisRepository extends JpaRepository<AvisEntity, Long> {

	@Query(	"from " +
			"    AvisEntity a " +
			"where " +
			"    a.actiu = true " +
			"and (a.entitatId is null or a.entitatId = :entitatId)" +
			"and a.dataInici <= :currentDate " +
			"and a.dataFinal >= :currentDate")
	List<AvisEntity> findActiveAdmin(@Param("currentDate") Date currentDate, @Param("entitatId") Long entitatId);

	@Query(	"from " +
			"    AvisEntity a " +
			"where " +
			"    a.actiu = true " +
			"and a.avisAdministrador = false " +
			"and a.dataInici <= :currentDate " +
			"and a.dataFinal >= :currentDate")
	List<AvisEntity> findActive(@Param("currentDate") Date currentDate);

	List<AvisEntity> findByEntitatIdAndAssumpte(Long entitatId, String assumpte);
	
	@Modifying
 	@Query(value = "UPDATE IPA_AVIS " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}