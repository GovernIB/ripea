package es.caib.ripea.persistence.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.ExecucioMassivaEntity;

@Component
public interface ExecucioMassivaRepository extends JpaRepository<ExecucioMassivaEntity, Long> {
	
	List<ExecucioMassivaEntity> findByCreatedByAndEntitatIdOrderByCreatedDateDesc(String createdBy, Long entitatId, Pageable pageable);
	
	List<ExecucioMassivaEntity> findByEntitatIdOrderByCreatedDateDesc(Long entitatId, Pageable pageable);
	
	@Query("select min(id) " +
			"from 	ExecucioMassivaEntity " +
			"where 	dataInici <= :ara " +
			"	and dataFi is null ")
	Long getNextMassiu(@Param("ara") Date ara);
	
	@Query("select e " +
			"from 	ExecucioMassivaEntity e " +
			"where 	dataInici <= :ara " +
			"	and dataFi is null " +
			"	order by e.id asc")
	List<ExecucioMassivaEntity> getMassivesPerProcessar(@Param("ara") Date ara);
	
	@Query("select min(id) " +
			"	from 	ExecucioMassivaEntity " +
			"	where 	dataInici <= :ara " +
			"	and dataFi is null")
	Long getMinExecucioMassiva(@Param("ara") Date ara);
	
	@Modifying
 	@Query(value = "UPDATE IPA_EXECUCIO_MASSIVA " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}