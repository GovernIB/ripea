package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.ExecucioMassivaContingutEntity;
import es.caib.ripea.persistence.entity.ExecucioMassivaEntity;
import es.caib.ripea.service.intf.dto.ExecucioMassivaEstatDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public interface ExecucioMassivaContingutRepository extends JpaRepository<ExecucioMassivaContingutEntity, Long> {
	
	public List<ExecucioMassivaContingutEntity> findByExecucioMassivaOrderByOrdreAsc (ExecucioMassivaEntity execucioMassiva);
	
	@Query("select min(e.id) " +
			"from	ExecucioMassivaContingutEntity e " +
			"where	e.execucioMassiva.id = :nextMassiu " +
			"   and	e.dataFi is null ")
	public Long findNextExecucioMassivaContingut(@Param("nextMassiu") Long nextMassiu);
	
	@Query("select min(e.id) " +
			"from	ExecucioMassivaContingutEntity e " +
			"where	e.execucioMassiva.id =	" +
			"			(select min(id) " +
			"			 from 	ExecucioMassivaEntity " +
			"			 where 	dataInici <= :ara " +
			"					and dataFi is null) " +
			"	and	e.dataFi is null ")
	public Long findExecucioMassivaContingutId(@Param("ara") Date ara);

	@Modifying
	@Query(value = "delete from ipa_massiva_contingut where contingut_id = :contingutId ", nativeQuery = true)
	int deleteExecucioMassivaFromContingutsOrfes(@Param("contingutId") Long contingutId);

	long countByElementIdInAndEstat(List<Long> elementIds, ExecucioMassivaEstatDto estat);
	
	@Modifying
 	@Query(value = "UPDATE IPA_MASSIVA_CONTINGUT " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);	
}